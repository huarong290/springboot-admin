package com.springboot.admin.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisConfig {

    // Sentinel 集群模式
    @Bean
    @Profile("mac")
    public LettuceConnectionFactory sentinelConnectionFactory() {
        RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration()
                .master("mymaster")
                .sentinel("127.0.0.1", 26379)
                .sentinel("127.0.0.1", 26380)
                .sentinel("127.0.0.1", 26381);
        sentinelConfig.setPassword(RedisPassword.of("123456"));
        return new LettuceConnectionFactory(sentinelConfig);
    }

    // 单机模式
    @Bean
    @Profile("windows")
    public LettuceConnectionFactory singleConnectionFactory() {
        return new LettuceConnectionFactory("127.0.0.1", 6379);
    }

    // RedisTemplate 通用
    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        // 可加序列化器
        return template;
    }
}
