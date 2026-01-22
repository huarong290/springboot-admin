package com.springboot.admin.config.cache;


import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * 缓存配置类
 * 作用：注册自定义的 CacheManager，使 @Cacheable 支持 #过期时间 语法
 */
@Configuration
@EnableCaching
public class RedisCacheConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        // 1. 获取默认配置
        RedisCacheConfiguration defaultCacheConfig = getDefaultCacheConfiguration();

        // 2. 🚀 关键：使用自定义的 FlexibleRedisCacheManager
        // 使用 nonLockingRedisCacheWriter 提升性能
        return new FlexibleRedisCacheManager(
                RedisCacheWriter.nonLockingRedisCacheWriter(factory),
                defaultCacheConfig
        );
    }

    /**
     * 获取默认配置 (序列化规则与 RedisConfig 保持一致)
     */
    private RedisCacheConfiguration getDefaultCacheConfiguration() {
        GenericJackson2JsonRedisSerializer jsonSerializer = getJsonSerializer();

        return RedisCacheConfiguration.defaultCacheConfig()
                // Key 使用 String
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                // Value 使用 JSON
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer))
                // 默认 1 小时过期
                .entryTtl(Duration.ofHours(1))
                // 单冒号前缀
                .computePrefixWith(name -> name + ":");
    }

    private GenericJackson2JsonRedisSerializer getJsonSerializer() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );
        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }
}