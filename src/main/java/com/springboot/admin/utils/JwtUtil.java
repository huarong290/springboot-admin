package com.springboot.admin.utils;

import com.springboot.admin.config.properties.JwtProperties;
import com.springboot.admin.constants.security.JwtConstants;
import com.springboot.admin.exception.BusinessException;
import com.springboot.admin.exception.JwtAuthenticationException;
import io.jsonwebtoken.*;
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
 * JWT 工具类
 * <p>
 * 基于 jjwt 0.13.x
 * 使用同步阻塞方式，适用于传统 Spring MVC 架构
 *
 * 功能：
 * 1. 生成 AccessToken / RefreshToken
 * 2. 解析 JWT 获取 Claims
 * 3. 校验 Token 是否有效
 * 4. 获取 Token 剩余有效时间
 * 5. 从 Authorization Header 提取 Bearer Token
 */
@Slf4j
@Component
public class JwtUtil {

    private final JwtProperties jwtProperties;

    /**
     * HMAC 密钥
     */
    private SecretKey secretKey;

    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    /**
     * 初始化 SecretKey
     * 使用 HS256 算法
     */
    @PostConstruct public void init() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("JWT Secret 长度不足，HS256 至少需要 32 字节");
        }
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    // ===================== Token 生成 =====================

    /**
     * 生成 AccessToken
     *
     * @param username    用户名
     * @param extraClaims 扩展 Claims（角色、权限、租户等）
     * @return AccessToken
     */
    public String generateAccessToken(String username, Map<String, Object> extraClaims) {
        return generateToken(
                username,
                JwtConstants.TOKEN_TYPE_ACCESS,
                jwtProperties.getAccessTokenExpiration(),
                extraClaims
        );
    }

    /**
     * 生成 RefreshToken
     *
     * @param username 用户名
     * @return RefreshToken
     */
    public String generateRefreshToken(String username) {
        return generateToken(
                username,
                JwtConstants.TOKEN_TYPE_REFRESH,
                jwtProperties.getRefreshTokenExpiration(),
                null
        );
    }

    /**
     * 通用生成 Token 方法
     */
    private String generateToken(String username,
                                 String tokenType,
                                 long expirationMillis,
                                 Map<String, Object> extraClaims) {

        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMillis);

        // JWT ID（用于防重放）
        String jti = UUID.randomUUID().toString();

        var claimsBuilder = Jwts.builder()
                .claims()
                .subject(username)
                .issuedAt(now)
                .expiration(expiration)
                .add(JwtConstants.CLAIM_TOKEN_TYPE, tokenType)
                .id(jti);

        if (extraClaims != null && !extraClaims.isEmpty()) {
            claimsBuilder.add(extraClaims);
        }

        return claimsBuilder
                .and()
                .signWith(secretKey)
                .compact();
    }

    // ===================== Token 解析 =====================

    /**
     * 解析 JWT，返回 Claims
     *
     * @param token JWT
     * @return Claims，解析失败返回 null
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("JWT 已过期: {}", e.getMessage());
            throw new JwtAuthenticationException("Token 已过期");
        } catch (io.jsonwebtoken.security.SignatureException e) {
            log.warn("JWT 签名无效: {}", e.getMessage());
            throw new JwtAuthenticationException("Token 签名无效");
        } catch (MalformedJwtException | UnsupportedJwtException e) {
            log.warn("JWT 格式错误: {}", e.getMessage());
            throw new JwtAuthenticationException("Token 格式错误");
        } catch (Exception e) {
            log.error("JWT 解析失败: {}", e.getMessage(), e);
            throw new JwtAuthenticationException("JWT解析失败");
        }
    }

    /**
     * 获取用户名
     */
    public String getUsername(String token) {
        Claims claims = parseToken(token);
        return claims == null ? null : claims.getSubject();
    }
    public String getClaimAsString(String token, String key) {
        Claims claims = parseToken(token);
        return claims == null ? null : claims.get(key, String.class);
    }

    public Map<String, Object> getAllClaims(String token) {
        Claims claims = parseToken(token);
        return claims;
    }
    /**
     * 获取 Token 类型
     */
    public String getTokenType(String token) {
        Claims claims = parseToken(token);
        return claims == null
                ? null
                : claims.get(JwtConstants.CLAIM_TOKEN_TYPE, String.class);
    }

    /**
     * 获取 JWT 的 jti（唯一标识）
     */
    public String getJti(String token) {
        Claims claims = parseToken(token);
        return claims == null ? null : claims.getId();
    }

    // ===================== Token 校验 =====================

    /**
     * 校验 AccessToken 是否有效
     */
    public boolean isAccessTokenValid(String token) {
        return isTokenValid(token, JwtConstants.TOKEN_TYPE_ACCESS);
    }

    /**
     * 校验 RefreshToken 是否有效
     */
    public boolean isRefreshTokenValid(String token) {
        return isTokenValid(token, JwtConstants.TOKEN_TYPE_REFRESH);
    }

    /**
     * 通用 Token 校验逻辑
     */

    private boolean isTokenValid(String token, String expectedType) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().after(new Date()) &&
                    expectedType.equals(claims.get(JwtConstants.CLAIM_TOKEN_TYPE, String.class));
        } catch (BusinessException e) {
            return false;
        }
    }

    /**
     * 获取 Token 剩余有效时间（毫秒）
     */
    public long getRemainingTime(String token) {
        try {
            return parseToken(token).getExpiration().getTime() - System.currentTimeMillis();
        } catch (BusinessException e) {
            return 0L;
        }
    }

    // ===================== Header 解析 =====================

    /**
     * 从 Authorization Header 提取 Bearer Token
     */
    public static String extractBearerToken(String authHeader) {
        if (authHeader != null
                && authHeader.startsWith(JwtConstants.JWT_BEARER_PREFIX)) {
            return authHeader.substring(JwtConstants.JWT_BEARER_PREFIX.length());
        }
        return "";
    }
}
