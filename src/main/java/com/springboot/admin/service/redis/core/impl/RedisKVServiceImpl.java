package com.springboot.admin.service.redis.core.impl;

import com.springboot.admin.service.redis.core.IRedisKVService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Redis KV 服务实现类（最终版）
 *
 * <p>
 * 提供对 Redis Key-Value 类型的常用操作，适用于缓存、配置存储等场景。
 * </p>
 *
 * <p>
 * 特性：
 * <ul>
 *   <li>所有方法均带异常处理，避免业务抛出异常</li>
 *   <li>返回值语义统一：失败时返回 false / Optional.empty()</li>
 *   <li>TTL 处理区分 Redis 的 -1/-2 返回值</li>
 * </ul>
 * </p>
 */
@Service
public class RedisKVServiceImpl implements IRedisKVService {

    private static final Logger log = LoggerFactory.getLogger(RedisKVServiceImpl.class);

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisKVServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // ==================== SET ====================

    /**
     * 设置 Key-Value（无过期时间）
     */
    @Override
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            log.error("Redis SET 操作异常 key={}", key, e);
            return false;
        }
    }

    /**
     * 设置 Key-Value（带过期时间）
     */
    @Override
    public boolean set(String key, Object value, Duration ttl) {
        try {
            if (ttl != null && !ttl.isZero() && !ttl.isNegative()) {
                redisTemplate.opsForValue().set(key, value, ttl);
            } else {
                redisTemplate.opsForValue().set(key, value);
            }
            return true;
        } catch (Exception e) {
            log.error("Redis SET+EXPIRE 操作异常 key={} ttl={}", key, ttl, e);
            return false;
        }
    }

    // ==================== GET ====================

    /**
     * 获取 Key 对应的值
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(String key, Class<T> type) {
        try {
            Object val = redisTemplate.opsForValue().get(key);
            if (val == null) {
                return Optional.empty();
            }
            try {
                return Optional.of((T) val);
            } catch (ClassCastException e) {
                log.error("Redis GET 类型转换失败 key={} targetType={}", key, type, e);
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Redis GET 操作异常 key={}", key, e);
            return Optional.empty();
        }
    }

    // ==================== DELETE ====================

    /**
     * 删除 Key
     */
    @Override
    public boolean delete(String key) {
        try {
            Boolean result = redisTemplate.delete(key);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("Redis DEL 操作异常 key={}", key, e);
            return false;
        }
    }

    // ==================== EXISTS ====================

    /**
     * 判断 Key 是否存在
     */
    @Override
    public boolean exists(String key) {
        try {
            Boolean result = redisTemplate.hasKey(key);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("Redis EXISTS 操作异常 key={}", key, e);
            return false;
        }
    }

    // ==================== TTL ====================

    /**
     * 获取 Key 剩余 TTL（过期时间）
     */
    @Override
    public Optional<Duration> ttl(String key) {
        try {
            Long ttlMillis = redisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
            if (ttlMillis == null) {
                return Optional.empty();
            }
            if (ttlMillis == -1) {
                // -1 表示 key 存在但没有设置过期时间
                return Optional.empty();
            }
            if (ttlMillis == -2) {
                // -2 表示 key 不存在
                return Optional.empty();
            }
            return Optional.of(Duration.ofMillis(ttlMillis));
        } catch (Exception e) {
            log.error("Redis TTL 操作异常 key={}", key, e);
            return Optional.empty();
        }
    }
}
