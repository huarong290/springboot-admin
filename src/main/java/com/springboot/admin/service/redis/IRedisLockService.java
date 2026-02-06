package com.springboot.admin.service.redis;


import java.time.Duration;

/**
 * Redis 分布式锁服务接口
 *
 * <p>负责分布式锁操作：</p>
 * <ul>
 *   <li>tryLock：尝试获取锁</li>
 *   <li>releaseLock：释放锁（Lua 原子操作，value 必须唯一）</li>
 *   <li>renewLock：续期锁（Lua 原子操作，避免竞态）</li>
 * </ul>
 */
public interface IRedisLockService {

    boolean tryLock(String key, String value, Duration expire);

    boolean releaseLock(String key, String value);

    boolean renewLock(String key, String value, Duration expire);
}
