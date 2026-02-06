package com.springboot.admin.service.redis.biz.impl;

import com.springboot.admin.enums.LockFailReasonEnum;
import com.springboot.admin.model.dto.redis.LockResultDTO;
import com.springboot.admin.service.redis.biz.IRedisLockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * Redis 分布式锁服务实现类
 *
 * <p>
 * 提供基于 Redis 的分布式锁操作，适用于多节点环境下的并发控制。
 * 通过 Redis 的 SETNX、EXPIRE 以及 Lua 脚本保证锁的互斥性和原子性。
 * </p>
 *
 * <p>
 * 特性：
 * </p>
 * <ul>
 *   <li>保证锁的互斥性：同一时间只有一个客户端持有锁</li>
 *   <li>支持锁的自动过期，避免死锁</li>
 *   <li>通过 Lua 脚本实现释放与续期的原子性，避免竞态条件</li>
 *   <li>返回 {@link LockResultDTO}，包含成功/失败状态、持有者、失败原因、剩余 TTL</li>
 *   <li>优化点：
 *     <ul>
 *       <li>forceReleaseLock 返回原锁持有者信息</li>
 *       <li>TTL 处理优化，区分 Redis 返回的 -1/-2</li>
 *       <li>tryLock 带重试使用 LockSupport.parkNanos，减少阻塞开销</li>
 *       <li>Lua 脚本缓存，避免重复创建对象</li>
 *     </ul>
 *   </li>
 * </ul>
 */
@Service
@Slf4j
public class RedisLockServiceImpl implements IRedisLockService {

    private final RedisTemplate<String, String> redisTemplate;

    // 缓存 Lua 脚本，避免重复创建对象
    private static final DefaultRedisScript<Long> RELEASE_SCRIPT;
    private static final DefaultRedisScript<Long> RENEW_SCRIPT;

    static {
        RELEASE_SCRIPT = new DefaultRedisScript<>(
                "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                        "return redis.call('del', KEYS[1]) else return 0 end", Long.class);

        RENEW_SCRIPT = new DefaultRedisScript<>(
                "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                        "return redis.call('pexpire', KEYS[1], ARGV[2]) else return 0 end", Long.class);
    }

    public RedisLockServiceImpl(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 尝试获取分布式锁（SETNX + EXPIRE）
     *
     * @param key    锁的唯一标识
     * @param value  锁的值（必须唯一，用于标识持有锁的客户端，一般使用 UUID）
     * @param expire 锁的过期时间；若为 null 或 <=0，则表示无过期（不推荐）
     * @return LockResultDTO，包含成功/失败状态、持有者、失败原因、剩余 TTL
     */
    @Override
    public LockResultDTO tryLock(String key, String value, Duration expire) {
        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(key, value, expire);

        if (Boolean.TRUE.equals(success)) {
            return LockResultDTO.success(value, expire.toMillis());
        } else {
            String owner = redisTemplate.opsForValue().get(key);
            Long ttl = safeGetTtl(key);
            return new LockResultDTO(false, owner, LockFailReasonEnum.LOCKED_BY_OTHER, ttl);
        }
    }

    /**
     * 释放分布式锁（Lua 脚本保证原子性）
     *
     * @param key   锁的唯一标识
     * @param value 锁的值（必须与持有锁时设置的值一致）
     * @return LockResultDTO，包含成功/失败状态、持有者、失败原因
     */
    @Override
    public LockResultDTO releaseLock(String key, String value) {
        Long result = redisTemplate.execute(RELEASE_SCRIPT, Collections.singletonList(key), value);

        if (result != null && result > 0) {
            log.info("成功释放锁 key={}, value={}", key, value);
            return LockResultDTO.success(value, null);
        } else {
            String owner = redisTemplate.opsForValue().get(key);
            Long ttl = safeGetTtl(key);
            log.warn("释放锁失败 key={}, value={}，当前持有者={}，剩余TTL={}", key, value, owner, ttl);
            return new LockResultDTO(false, owner, LockFailReasonEnum.VALUE_MISMATCH, ttl);
        }
    }

    /**
     * 续期分布式锁（Lua 脚本保证原子性）
     *
     * @param key    锁的唯一标识
     * @param value  锁的值（必须与持有锁时设置的值一致）
     * @param expire 新的过期时间
     * @return LockResultDTO，包含成功/失败状态、持有者、失败原因、剩余 TTL
     */
    @Override
    public LockResultDTO renewLock(String key, String value, Duration expire) {
        Long result = redisTemplate.execute(RENEW_SCRIPT, Collections.singletonList(key), value, String.valueOf(expire.toMillis()));

        if (result != null && result > 0) {
            return LockResultDTO.success(value, expire.toMillis());
        } else {
            String owner = redisTemplate.opsForValue().get(key);
            Long ttl = safeGetTtl(key);
            return new LockResultDTO(false, owner, LockFailReasonEnum.VALUE_MISMATCH, ttl);
        }
    }

    /**
     * 在指定时间内尝试获取锁（带重试）
     *
     * @param key           锁 key
     * @param value         锁值（UUID）
     * @param expire        锁过期时间
     * @param waitTime      最长等待时间
     * @param retryInterval 重试间隔
     * @return LockResultDTO，包含成功/失败状态、持有者、失败原因、剩余 TTL
     */
    @Override
    public LockResultDTO tryLock(String key, String value, Duration expire, Duration waitTime, Duration retryInterval) {
        long deadline = System.currentTimeMillis() + waitTime.toMillis();
        long currentInterval = retryInterval.toMillis();
        long maxInterval = retryInterval.toMillis() * 8; // 最大退避间隔，可配置

        while (System.currentTimeMillis() < deadline) {
            LockResultDTO result = tryLock(key, value, expire);
            if (result.isSuccess()) {
                log.info("成功获取锁 key={}, value={}, expire={}", key, value, expire);
                return result;
            }

            // 指数退避
            LockSupport.parkNanos(Duration.ofMillis(currentInterval).toNanos());
            currentInterval = Math.min(currentInterval * 2, maxInterval);
        }

        log.error("获取锁超时 key={}, value={}, waitTime={}, 初始重试间隔={}", key, value, waitTime, retryInterval);
        return new LockResultDTO(false, null, LockFailReasonEnum.TIMEOUT, null);
    }


    /**
     * 强制释放锁（不校验 value，直接删除）
     *
     * @param key 锁 key
     * @return LockResultDTO，包含成功状态和原锁持有者信息
     */
    @Override
    public LockResultDTO forceReleaseLock(String key) {
        String owner = redisTemplate.opsForValue().get(key);
        redisTemplate.delete(key);
        log.warn("强制释放锁 key={}，原持有者={}", key, owner);
        return new LockResultDTO(true, owner, null, null);
    }

    /**
     * 判断锁是否被占用
     *
     * @param key 锁 key
     * @return LockResultDTO，包含锁状态、持有者、剩余 TTL
     */
    @Override
    public LockResultDTO isLocked(String key) {
        String owner = redisTemplate.opsForValue().get(key);
        if (owner != null) {
            Long ttl = safeGetTtl(key);
            return new LockResultDTO(true, owner, null, ttl);
        }
        return new LockResultDTO(false, null, LockFailReasonEnum.NOT_FOUND, null);
    }

    /**
     * 获取当前锁的持有者
     *
     * @param key 锁 key
     * @return LockResultDTO，包含持有者信息；若锁不存在则 success=false
     */
    @Override
    public LockResultDTO getLockOwner(String key) {
        String owner = redisTemplate.opsForValue().get(key);
        if (owner != null) {
            Long ttl = safeGetTtl(key);
            return new LockResultDTO(true, owner, null, ttl);
        }
        return new LockResultDTO(false, null, LockFailReasonEnum.NOT_FOUND, null);
    }
    /**
     * 安全获取 TTL，处理 Redis 返回的 -1/-2
     *
     * @param key 锁 key
     * @return TTL 毫秒数；null 表示无过期或不存在
     */
    private Long safeGetTtl(String key) {
        Long ttl = redisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
        if (ttl == null) {
            return null;
        }
        if (ttl == -1) {
            // -1 表示 key 存在但没有设置过期时间
            return null;
        }
        if (ttl == -2) {
            // -2 表示 key 不存在
            return null;
        }
        return ttl;
    }
}

