package com.springboot.admin.service.redis.core.impl;

import com.springboot.admin.service.redis.core.IRedisAtomicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Redis 原子操作服务实现类（最终版）
 *
 * <p>
 * 封装 Redis 的原子性操作，常用于分布式锁、并发控制等场景。
 * 提供以下功能：
 * 1. setIfAbsent / setIfAbsent+TTL （SETNX）
 * 2. increment / incrementBy / decrement / increment+TTL （INCR/INCRBY/DECR）
 *
 * <p>
 * 特性：
 * - 使用 RedisTemplate + RedisCallback 保证原子性
 * - 所有方法均带异常处理，避免业务抛出异常
 * - TTL 设置仅在第一次创建时生效，避免覆盖已有 TTL
 * - 返回值语义统一：失败时返回 false 或 0L
 * </p>
 */
@Service
public class RedisAtomicServiceImpl implements IRedisAtomicService {

    private static final Logger log = LoggerFactory.getLogger(RedisAtomicServiceImpl.class);

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisAtomicServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // ==================== 公共工具方法 ====================

    /**
     * 判断 TTL 是否有效
     * @param ttl 过期时间
     * @return true=有效，false=无效
     */
    private boolean validTtl(Duration ttl) {
        return ttl != null && !ttl.isZero() && !ttl.isNegative();
    }

    /**
     * 序列化 Key
     * @param key Redis Key
     * @return 序列化后的字节数组
     */
    private byte[] serializeKey(String key) {
        return redisTemplate.getStringSerializer().serialize(key);
    }

    /**
     * 原子 INCR/INCRBY + TTL
     *
     * <p>
     * 使用 RedisCallback 保证在同一个连接中执行 INCRBY 和 TTL 设置，避免并发覆盖。
     * 仅在第一次创建 Key 时设置 TTL（val==1），避免覆盖已有 TTL。
     * </p>
     *
     * @param key Redis Key
     * @param delta 增量（可为负数，表示减少）
     * @param ttl 过期时间（可为 null，表示不设置）
     * @return 增加后的值；异常时返回 0
     */
    private long safeIncrement(String key, long delta, Duration ttl) {
        try {
            return redisTemplate.execute((RedisCallback<Long>) connection -> {
                byte[] rawKey = serializeKey(key);
                if (rawKey == null) return 0L;

                // 执行 INCRBY
                Long val = connection.stringCommands().incrBy(rawKey, delta);

                // 只有第一次创建时才设置 TTL
                if (val != null && val == 1 && validTtl(ttl)) {
                    connection.keyCommands().pExpire(rawKey, ttl.toMillis());
                }
                return val != null ? val : 0L;
            });
        } catch (Exception e) {
            log.error("Redis INCRBY+EXPIRE 操作异常 key={} delta={} ttl={}", key, delta, ttl, e);
            return 0L;
        }
    }

    // ==================== SETNX 操作 ====================

    /**
     * 仅当 Key 不存在时设置值（SETNX）
     *
     * @param key Redis Key
     * @param value 要存储的值
     * @return true=成功设置，false=失败或异常
     */
    @Override
    public boolean setIfAbsent(String key, Object value) {
        try {
            Boolean result = redisTemplate.opsForValue().setIfAbsent(key, value);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("Redis SETNX 操作异常 key={}", key, e);
            return false;
        }
    }

    /**
     * 仅当 Key 不存在时设置值，并指定过期时间（SETNX + EXPIRE）
     *
     * @param key Redis Key
     * @param value 要存储的值
     * @param ttl 过期时间
     * @return true=成功设置，false=失败或异常
     */
    @Override
    public boolean setIfAbsent(String key, Object value, Duration ttl) {
        try {
            if (validTtl(ttl)) {
                Boolean result = redisTemplate.opsForValue().setIfAbsent(key, value, ttl);
                return Boolean.TRUE.equals(result);
            } else {
                return setIfAbsent(key, value);
            }
        } catch (Exception e) {
            log.error("Redis SETNX+EXPIRE 操作异常 key={} ttl={}", key, ttl, e);
            return false;
        }
    }

    // ==================== INCR/DECR 操作 ====================

    /**
     * 自增（INCR）
     *
     * @param key Redis Key
     * @return 自增后的值；异常时返回 0
     */
    @Override
    public long increment(String key) {
        return safeIncrement(key, 1, null);
    }

    /**
     * 按指定增量增加计数器（INCRBY）
     *
     * @param key Redis Key
     * @param delta 增量（可为负数）
     * @return 增加后的值；异常时返回 0
     */
    @Override
    public long incrementBy(String key, long delta) {
        return safeIncrement(key, delta, null);
    }

    /**
     * 自减（DECR）
     *
     * @param key Redis Key
     * @return 自减后的值；异常时返回 0
     */
    @Override
    public long decrement(String key) {
        return safeIncrement(key, -1, null);
    }

    /**
     * 初始化计数器并设置 TTL（INCR + EXPIRE）
     *
     * @param key Redis Key
     * @param ttl 过期时间
     * @return 自增后的值；异常时返回 0
     */
    @Override
    public long increment(String key, Duration ttl) {
        return safeIncrement(key, 1, ttl);
    }
}
