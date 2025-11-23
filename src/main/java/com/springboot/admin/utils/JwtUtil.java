package com.springboot.admin.utils;

import com.springboot.admin.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
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

@Slf4j
@Component
public class JwtUtil {

    private final JwtProperties jwtProperties;
    private SecretKey key;

    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }

    /**
     * 生成 Access Token
     */
    public Mono<String> generateAccessToken(String username, Map<String, Object> extraClaims) {
        return Mono.fromSupplier(() -> generateToken(username, "access", jwtProperties.getAccessTokenExpiration(), extraClaims));
    }

    /**
     * 生成 Refresh Token
     */
    public Mono<String> generateRefreshToken(String username) {
        return Mono.fromSupplier(() -> generateToken(username, "refresh", jwtProperties.getRefreshTokenExpiration(), null));
    }

    /**
     * 通用生成 Token 方法
     */
    private String generateToken(String username, String type, long expirationMillis, Map<String, Object> extraClaims) {
        JwtBuilder builder = Jwts.builder()
                .subject(username)   // 新版用 subject()
                .claim("type", type)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(key, SignatureAlgorithm.HS256);

        if (extraClaims != null && !extraClaims.isEmpty()) {
            builder.claims(extraClaims);
        }

        return builder.compact();
    }

    /**
     * 解析 Token
     */
    public Mono<Claims> parseToken(String token) {
        return Mono.fromCallable(() -> {
            try {
                return Jwts.parser()   // 0.13.0 用 parser()
                        .verifyWith(key)   // 用 verifyWith 替代 setSigningKey
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();     // 新版用 getPayload() 获取 Claims
            } catch (Exception e) {
                log.error("解析 Token 失败: {}", e.getMessage(), e);
                throw e;
            }
        });
    }

    public Mono<String> getUsername(String token) {
        return parseToken(token).map(Claims::getSubject);
    }

    public Mono<String> getTokenType(String token) {
        return parseToken(token).map(c -> c.get("type", String.class));
    }

    public Mono<Boolean> isAccessTokenValid(String token) {
        return isTokenValid(token, "access");
    }

    public Mono<Boolean> isRefreshTokenValid(String token) {
        return isTokenValid(token, "refresh");
    }

    private Mono<Boolean> isTokenValid(String token, String expectedType) {
        return parseToken(token)
                .map(claims -> claims.getExpiration().after(new Date())
                        && expectedType.equals(claims.get("type", String.class)))
                .onErrorReturn(false);
    }

    public Mono<Long> getRemainingTime(String token) {
        return parseToken(token)
                .map(claims -> claims.getExpiration().getTime() - System.currentTimeMillis());
    }
}
