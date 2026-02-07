package com.springboot.admin.config.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 验证码配置属性类
 * 对应配置文件中的 captcha: 节点
 */
@Data
@Component
@ConfigurationProperties(prefix = "captcha")
public class CaptchaProperties {

    /**
     * 用于敏感数据加密的密钥（如 HMAC 签名）
     */
    private String hmacSecret = "default-secret-key";

    /**
     * 验证码有效期（秒），默认 300 秒（5分钟）
     */
    private long ttlSeconds = 300;

    /**
     * 同一目标（手机号/IP）获取验证码的频率限制（秒）
     */
    private long rateLimitSeconds = 60;

    /**
     * 单个验证码允许的最大尝试校验次数
     */
    private int maxAttempts = 5;

    /**
     * 图片验证码相关配置
     */
    private ImageConfig image = new ImageConfig();

    @Data
    public static class ImageConfig {
        /**
         * 验证码字符长度
         */
        private int length = 4;
        /**
         * 图片宽度
         */
        private int width = 120;
        /**
         * 图片高度
         */
        private int height = 40;
        /**
         * 干扰线数量
         */
        private int lineCount = 10;
    }
}