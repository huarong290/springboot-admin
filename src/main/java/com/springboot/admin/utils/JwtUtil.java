package com.springboot.admin.utils;

import com.springboot.admin.config.JwtProperties;
import com.springboot.admin.constants.CommonConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

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
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }
    // ===================== Token 生成 =====================

    /**
     * 生成 AccessToken
     *
     * @param username 用户名
     * @param extraClaims 可选额外 Claims，例如权限列表
     * @return Mono<String> AccessToken
     */
    public String generateAccessToken(String username, Map<String, Object> extraClaims) {
        return generateToken(username, "access",
                jwtProperties.getAccessTokenExpiration(), extraClaims);
    }

    /**
     * 生成 RefreshToken
     *
     * @param username 用户名
     * @return Mono<String> RefreshToken
     */
    public String generateRefreshToken(String username) {
        return generateToken(username, "refresh",
                jwtProperties.getRefreshTokenExpiration(), null);
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
    private String generateToken(String username,
                                 String type,
                                 long expirationMillis,
                                 Map<String, Object> extraClaims) {

        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMillis);
        // 生成全局唯一 jti
        String jti = UUID.randomUUID().toString();
        // 构建 Claims
        var claimsBuilder = Jwts.builder()
                .claims()
                .subject(username)      // 标准字段：sub
                .issuedAt(now)          // 标准字段：iat
                .expiration(exp)        // 标准字段：exp
                .add("type", type)      // 自定义字段：token 类型
                .id(jti) ;       // 自定义字段：JWT ID（防重放核心）

        // 扩展 claims（如角色、权限、orgId）
        if (extraClaims != null && !extraClaims.isEmpty()) {
            claimsBuilder.add(extraClaims);
        }
        // 签名并生成 Token
        return claimsBuilder
                .and()
                .signWith(key)   // ✅ 不再传 SignatureAlgorithm
                .compact();
    }

    // ===================== Token 解析 =====================
    /**
     * 解析 Token，返回 Claims
     * 调整点：兼容 jjwt 0.13.x
     *
     * @param token JWT 字符串
     * @return Mono<Claims> 响应式返回 Claims
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("JWT 解析失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 获取用户名
     *
     * @param token JWT
     * @return Mono<String> 用户名
     */
    public String getUsername(String token) {
        return parseToken(token).getSubject();
    }

    /**
     * 获取 Token 类型（access / refresh）
     *
     * @param token JWT
     * @return Mono<String> token type
     */
    public String getTokenType(String token) {
        return parseToken(token).get("type", String.class);
    }
    // ===================== Token 校验 =====================
    /**
     * 校验 AccessToken 是否有效
     *
     * @param token JWT
     * @return Mono<Boolean>
     */
    public boolean isAccessTokenValid(String token) {
        return isTokenValid(token, "access");
    }
    /**
     * 校验 RefreshToken 是否有效
     *
     * @param token JWT
     * @return Mono<Boolean>
     */
    public boolean isRefreshTokenValid(String token) {
        return isTokenValid(token, "refresh");
    }
    /**
     * 校验 Token 是否有效（未过期，类型匹配）
     *
     * @param token JWT
     * @param expectedType access / refresh
     * @return Mono<Boolean>
     */
    private boolean isTokenValid(String token, String expectedType) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().after(new Date())
                    && expectedType.equals(claims.get("type", String.class));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取 Token 剩余有效时间（毫秒）
     *
     * @param token JWT
     * @return Mono<Long> 毫秒
     */
    public long getRemainingTime(String token) {
        try {
            return parseToken(token).getExpiration().getTime()
                    - System.currentTimeMillis();
        } catch (Exception e) {
            return 0L;
        }
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

    /**
     * 获取 JWT 的 jti（唯一标识，用于防重放）
     *
     * @param token JWT 字符串
     * @return jti
     */
    public String getJti(String token) {
        return parseToken(token).getId();
    }
}
