package com.springboot.admin.config.cache;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;

import java.time.Duration;

/**
 * 自定义 Redis 缓存管理器
 * <p>
 * 支持在 CacheName 中通过 # 号指定过期时间
 * 格式：cacheName#ttl
 * 示例：@Cacheable(value = "userInfo#30m") -> 30分钟过期
 * </p>
 */
public class FlexibleRedisCacheManager extends RedisCacheManager {

    public FlexibleRedisCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration) {
        super(cacheWriter, defaultCacheConfiguration);
    }

    /**
     * 核心重写方法：在创建缓存时解析 Name
     */
    @Override
    protected RedisCache createRedisCache(String name, RedisCacheConfiguration cacheConfig) {
        // 1. 如果名字里没有 #，直接走默认逻辑
        if (!name.contains("#")) {
            return super.createRedisCache(name, cacheConfig);
        }

        // 2. 解析名字和过期时间
        // 例如：name = "userInfo#30m"
        String[] parts = name.split("#");
        String realName = parts[0];   // "userInfo"
        String ttlStr = parts[1];     // "30m"

        // 3. 解析时间字符串为 Duration
        Duration duration = parseDuration(ttlStr);

        // 4. 基于默认配置，创建一个新的配置（仅修改 TTL）
        RedisCacheConfiguration modifiedConfig = cacheConfig.entryTtl(duration);

        // 5. 调用父类方法创建缓存，注意要用 realName (去掉 # 后面的部分)，否则 Redis Key 会带上 #30m
        return super.createRedisCache(realName, modifiedConfig);
    }

    /**
     * 简单的时长解析器
     * 支持：s(秒), m(分), h(时), d(天)
     */
    private Duration parseDuration(String ttlStr) {
        if (StringUtils.isBlank(ttlStr)) {
            return Duration.ZERO;
        }
        String timeValue = ttlStr.substring(0, ttlStr.length() - 1);
        String timeUnit = ttlStr.substring(ttlStr.length() - 1).toLowerCase();

        long value = Long.parseLong(timeValue);

        return switch (timeUnit) {
            case "d" -> Duration.ofDays(value);
            case "h" -> Duration.ofHours(value);
            case "m" -> Duration.ofMinutes(value);
            case "s" -> Duration.ofSeconds(value);
            default -> Duration.ofSeconds(Long.parseLong(ttlStr)); // 纯数字默认秒
        };
    }
}