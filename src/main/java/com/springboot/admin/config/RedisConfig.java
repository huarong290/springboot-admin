package com.springboot.admin.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.lettuce.core.api.StatefulConnection;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

/**
 * Redis 基础配置
 * <p>
 * 单机 Redis
 * Sentinel Redis
 * Object / String RedisTemplate
 * 作用：
 * 1. 配置 RedisTemplate<String, Object> Bean (用于手动操作 Object)
 * 2. 统一序列化规则 (与 CacheConfig 保持一致)
 * </p>
 */
@Configuration
public class RedisConfig {
    // ===========================
    // 公共方法
    // ===========================
    // ==================== Redis 连接池配置 ====================
    /**
     * Redis 连接池配置
     * 参数可在 application.yml 中通过 spring.data.redis.lettuce.pool.xxx 配置
     */
    private GenericObjectPoolConfig<StatefulConnection<?, ?>> getPoolConfig(RedisProperties properties) {
        GenericObjectPoolConfig<StatefulConnection<?, ?>> poolConfig = new GenericObjectPoolConfig<>();
        if (properties.getLettuce() != null && properties.getLettuce().getPool() != null) {
            RedisProperties.Pool p = properties.getLettuce().getPool();
            poolConfig.setMaxTotal(p.getMaxActive());
            poolConfig.setMaxIdle(p.getMaxIdle());
            poolConfig.setMinIdle(p.getMinIdle());
            if (p.getMaxWait() != null) {
                poolConfig.setMaxWait(Duration.ofMillis(p.getMaxWait().toMillis()));
            }else {
                poolConfig.setMaxWait(Duration.ofSeconds(2)); // 默认值
            }
            poolConfig.setMaxWait(Duration.ofMillis(p.getMaxWait().toMillis()));

        }
        return poolConfig;
    }
    // ---------------- RedisConnectionFactory ----------------
    // ==================== Sentinel 模式 ====================
    @Bean
    @ConditionalOnProperty(prefix = "spring.data.redis.sentinel", name = "master")
    public RedisConnectionFactory sentinelConnectionFactory(RedisProperties properties) {
        // Sentinel 配置
        Set<String> nodeSet = new HashSet<>(properties.getSentinel().getNodes());
        RedisSentinelConfiguration sentinelConfig =
                new RedisSentinelConfiguration(properties.getSentinel().getMaster(), nodeSet);
        if (properties.getPassword() != null) {
            sentinelConfig.setPassword(properties.getPassword());
        }

        // Lettuce 连接池配置
        LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
                .commandTimeout(properties.getTimeout() != null ? properties.getTimeout() : Duration.ofSeconds(2))
                .poolConfig(getPoolConfig(properties))
                .build();

        return new LettuceConnectionFactory(sentinelConfig, clientConfig);
    }

    // ==================== Cluster 模式 ====================
    @Bean
    @ConditionalOnProperty(prefix = "spring.data.redis.cluster", name = "nodes[0]")
    public RedisConnectionFactory clusterConnectionFactory(RedisProperties properties) {
        RedisClusterConfiguration clusterConfig = new RedisClusterConfiguration(properties.getCluster().getNodes());
        if (properties.getPassword() != null) {
            clusterConfig.setPassword(properties.getPassword());
        }

        LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
                .commandTimeout(properties.getTimeout() != null ? properties.getTimeout() : Duration.ofSeconds(2))
                .poolConfig(getPoolConfig(properties))
                .build();

        return new LettuceConnectionFactory(clusterConfig, clientConfig);
    }

    // ==================== 单机模式 ====================
    @Bean
    @ConditionalOnProperty(prefix = "spring.data.redis", name = "host")
    public RedisConnectionFactory singleConnectionFactory(RedisProperties properties) {
        RedisStandaloneConfiguration standaloneConfig = new RedisStandaloneConfiguration();
        standaloneConfig.setHostName(properties.getHost());
        standaloneConfig.setPort(properties.getPort());
        if (properties.getPassword() != null) {
            standaloneConfig.setPassword(properties.getPassword());
        }
        standaloneConfig.setDatabase(properties.getDatabase());

        LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
                .commandTimeout(properties.getTimeout() != null ? properties.getTimeout() : Duration.ofSeconds(2))
                .poolConfig(getPoolConfig(properties))
                .build();

        return new LettuceConnectionFactory(standaloneConfig, clientConfig);
    }

    /**
     * 配置 RedisTemplate
     * <p>
     * 适用于：redisTemplate.opsForValue().set("user", userObj)
     * </p>
     */
    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 1. String 序列化器
        StringRedisSerializer stringSerializer = new StringRedisSerializer();

        // 2. 获取增强版的 JSON 序列化器 (支持 Java8 时间 + 多态)
        GenericJackson2JsonRedisSerializer jsonSerializer = getJsonSerializer();


        // 3. 设置序列化规则
        // Key: String
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // Value: JSON
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * 获取统一的 JSON 序列化器
     * (逻辑与 RedisCacheConfig 中保持一致，确保数据互通)
     */
    private GenericJackson2JsonRedisSerializer getJsonSerializer() {
        ObjectMapper objectMapper = new ObjectMapper();

        // 支持 Java 8 时间 (LocalDateTime)
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // 支持多态 (将 class 类型写入 json，防止转为 LinkedHashMap)
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_CONCRETE_AND_ARRAYS, JsonTypeInfo.As.PROPERTY);
        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }
}