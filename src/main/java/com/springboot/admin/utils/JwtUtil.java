package com.springboot.admin.utils;

import com.springboot.admin.config.JwtProperties;
import com.springboot.admin.constants.CommonConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

/**
 * Jwt 工具类
 * 完全兼容 jjwt 0.13.x
 * 纯响应式设计，返回 Mono<T>
 *
 * 功能：
 * 1. 生成 AccessToken / RefreshToken
 * 2. 解析 JWT 获取 Claims
 * 3. 校验 Token 是否有效
 * 4. 获取剩余有效时间
 * 5. 从 Authorization Header 提取 Bearer Token
 */
@Slf4j
@Component
public class JwtUtil {

    private final JwtProperties jwtProperties;
    private SecretKey key;

    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    /**
     * 初始化 SecretKey
     * 使用 HS256 算法
     */
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }

    /**
     * 生成 AccessToken
     *
     * @param username 用户名
     * @param extraClaims 可选额外 Claims，例如权限列表
     * @return Mono<String> AccessToken
     */
    public Mono<String> generateAccessToken(String username, Map<String, Object> extraClaims) {
        return Mono.fromSupplier(() -> generateToken(username, "access", jwtProperties.getAccessTokenExpiration(), extraClaims));
    }

    /**
     * 生成 RefreshToken
     *
     * @param username 用户名
     * @return Mono<String> RefreshToken
     */
    public Mono<String> generateRefreshToken(String username) {
        return Mono.fromSupplier(() -> generateToken(username, "refresh", jwtProperties.getRefreshTokenExpiration(), null));
    }

    /**
     * 通用生成 Token 方法
     *
     * @param username 用户名
     * @param type token 类型：access / refresh
     * @param expirationMillis 过期时间（毫秒）
     * @param extraClaims 可选额外 claims
     * @return token 字符串
     */
    private String generateToken(String username, String type, long expirationMillis, Map<String, Object> extraClaims) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMillis);

        var builder = Jwts.builder()
                .setSubject(username)      // 用户名
                .claim("type", type)       // token 类型
                .setIssuedAt(now)          // 签发时间
                .setExpiration(exp)        // 过期时间
                .signWith(key, SignatureAlgorithm.HS256); // 签名算法

        if (extraClaims != null && !extraClaims.isEmpty()) {
            builder.addClaims(extraClaims);
        }

        return builder.compact();
    }

    /**
     * 解析 Token，返回 Claims
     * 调整点：兼容 jjwt 0.13.x
     *
     * @param token JWT 字符串
     * @return Mono<Claims> 响应式返回 Claims
     */
    public Mono<Claims> parseToken(String token) {
        return Mono.fromCallable(() -> {
            try {
                return Jwts.parser()           // jjwt 0.13.x parser
                        .verifyWith(key)       // 用 verifyWith 校验签名
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();         // 获取 Claims
            } catch (Exception e) {
                log.error("解析 Token 失败: {}", e.getMessage(), e);
                throw e;
            }
        });
    }

    /**
     * 获取用户名
     *
     * @param token JWT
     * @return Mono<String> 用户名
     */
    public Mono<String> getUsername(String token) {
        return parseToken(token).map(Claims::getSubject);
    }

    /**
     * 获取 Token 类型（access / refresh）
     *
     * @param token JWT
     * @return Mono<String> token type
     */
    public Mono<String> getTokenType(String token) {
        return parseToken(token).map(c -> c.get("type", String.class));
    }

    /**
     * 校验 AccessToken 是否有效
     *
     * @param token JWT
     * @return Mono<Boolean>
     */
    public Mono<Boolean> isAccessTokenValid(String token) {
        return isTokenValid(token, "access");
    }

    /**
     * 校验 RefreshToken 是否有效
     *
     * @param token JWT
     * @return Mono<Boolean>
     */
    public Mono<Boolean> isRefreshTokenValid(String token) {
        return isTokenValid(token, "refresh");
    }

    /**
     * 校验 Token 是否有效（未过期，类型匹配）
     *
     * @param token JWT
     * @param expectedType access / refresh
     * @return Mono<Boolean>
     */
    private Mono<Boolean> isTokenValid(String token, String expectedType) {
        return parseToken(token)
                .map(claims -> claims.getExpiration().after(new Date())
                        && expectedType.equals(claims.get("type", String.class)))
                .onErrorReturn(false);
    }

    /**
     * 获取 Token 剩余有效时间（毫秒）
     *
     * @param token JWT
     * @return Mono<Long> 毫秒
     */
    public Mono<Long> getRemainingTime(String token) {
        return parseToken(token)
                .map(claims -> claims.getExpiration().getTime() - System.currentTimeMillis())
                .onErrorReturn(0L);
    }

    // -------------------- 从 Authorization Header 提取 Bearer Token --------------------

    /**
     * 从请求头 Authorization 提取 Bearer Token
     *
     * @param authHeader Authorization Header
     * @return JWT 字符串，如果不存在返回空字符串
     */
    public static String extractBearerToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith(CommonConstants.JWT_BEARER_PREFIX)) {
            return authHeader.substring(CommonConstants.JWT_BEARER_PREFIX.length());
        }
        return "";
    }
}
