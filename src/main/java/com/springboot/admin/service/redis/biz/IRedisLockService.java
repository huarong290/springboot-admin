package com.springboot.admin.service.redis.biz;

import com.springboot.admin.model.dto.redis.LockResultDTO;
import java.time.Duration;

/**
 * Redis 分布式锁服务接口
 *
 * <p>
 * 提供基于 Redis 的分布式锁操作，适用于多节点环境下的并发控制。
 * </p>
 *
 * <p>
 * 设计目标：
 * </p>
 * <ul>
 *   <li>保证锁的互斥性：同一时间只有一个客户端持有锁</li>
 *   <li>支持锁的自动过期，避免死锁</li>
 *   <li>通过 Lua 脚本实现释放与续期的原子性，避免竞态条件</li>
 *   <li>调用方需保证 value 唯一性（通常使用 UUID），以区分不同客户端的锁</li>
 * </ul>
 */
public interface IRedisLockService {

    /**
     * 尝试获取分布式锁
     *
     * @param key    Redis Key，表示锁的唯一标识
     * @param value  锁的值（必须唯一，用于标识持有锁的客户端，一般使用 UUID）
     * @param expire 锁的过期时间，避免死锁；若为 null 或小于等于 0，则表示无过期（不推荐）
     * @return LockResultDTO，包含成功/失败状态、持有者、失败原因、剩余 TTL
     *
     * <p>ttlRemainingMillis 语义：</p>
     * <ul>
     *   <li>成功获取锁 → 返回锁的过期时间（毫秒）</li>
     *   <li>锁已存在 → 返回当前锁剩余 TTL</li>
     *   <li>锁不存在或无过期 → 返回 null</li>
     * </ul>
     */
    LockResultDTO tryLock(String key, String value, Duration expire);

    /**
     * 释放分布式锁
     *
     * @param key   Redis Key，表示锁的唯一标识
     * @param value 锁的值（必须与持有锁时设置的值一致）
     * @return LockResultDTO，包含成功/失败状态、持有者、失败原因
     */
    LockResultDTO releaseLock(String key, String value);

    /**
     * 续期分布式锁
     *
     * @param key    Redis Key，表示锁的唯一标识
     * @param value  锁的值（必须与持有锁时设置的值一致）
     * @param expire 新的过期时间
     * @return LockResultDTO，包含成功/失败状态、持有者、失败原因、剩余 TTL
     */
    LockResultDTO renewLock(String key, String value, Duration expire);

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
    LockResultDTO tryLock(
            String key,
            String value,
            Duration expire,
            Duration waitTime,
            Duration retryInterval
    );

    /**
     * 强制释放锁（管理员或运维使用）
     *
     * <p>不会校验 value，直接删除 key。</p>
     *
     * <p> 注意：如果锁正在被其他客户端使用，强制释放可能导致并发安全问题。</p>
     *
     * @param key Redis Key
     * @return LockResultDTO，包含成功/失败状态、持有者、失败原因
     */
    LockResultDTO forceReleaseLock(String key);

    /**
     * 判断锁是否被占用
     *
     * @param key Redis Key
     * @return LockResultDTO，包含锁状态、持有者、剩余 TTL
     */
    LockResultDTO isLocked(String key);

    /**
     * 获取当前锁的持有者（value）
     *
     * @param key Redis Key
     * @return LockResultDTO，包含持有者信息；若锁不存在则 success=false
     */
    LockResultDTO getLockOwner(String key);
}
