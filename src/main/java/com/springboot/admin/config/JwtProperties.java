package com.springboot.admin.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * JWT 密钥（建议使用 Base64 编码的 32 位以上字符串）
     */
    private String secret;

    /**
     * Access Token 有效期（毫秒）
     */
    private long accessTokenExpiration;

    /**
     * Refresh Token 有效期（毫秒）
     */
    private long refreshTokenExpiration;

    /**
     * 签名算法（默认 HS256，可配置 HS512）
     */
    private String algorithm = "HS256";
}
