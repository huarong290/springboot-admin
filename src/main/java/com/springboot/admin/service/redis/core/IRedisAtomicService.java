package com.springboot.admin.service.redis.core;

import java.time.Duration;

/**
 * Redis 原子操作服务接口
 *
 * <p>
 * 封装 Redis 的原子性操作，常用于分布式锁、并发控制等场景。
 * </p>
 *
 * <p>
 * 设计目标：
 * <ul>
 *   <li>提供 SETNX（仅当 Key 不存在时设置）操作</li>
 *   <li>支持过期时间，避免死锁</li>
 *   <li>统一返回值语义，避免返回 null</li>
 *   <li>支持自增、自减、按增量调整</li>
 *   <li>支持初始化计数器并设置 TTL，避免无限增长</li>
 *   <li>保证操作的原子性，避免并发冲突</li>
 * </ul>
 * </p>
 */
public interface IRedisAtomicService {

    /**
     * 仅当 Key 不存在时设置值（SETNX）
     *
     * @param key   Redis Key
     * @param value 要存储的值（需可序列化）
     * @return true=成功设置（Key 原本不存在），false=失败（Key 已存在或操作异常）
     *
     * <p>等价于 Redis 命令：SET key value NX</p>
     */
    boolean setIfAbsent(String key, Object value);

    /**
     * 仅当 Key 不存在时设置值，并指定过期时间（SETNX + EXPIRE）
     *
     * @param key   Redis Key
     * @param value 要存储的值（需可序列化）
     * @param ttl   过期时间，若为 null 或小于等于 0，则表示无过期
     * @return true=成功设置（Key 原本不存在），false=失败（Key 已存在或操作异常）
     *
     * <p>等价于 Redis 命令：SET key value NX PX ttl</p>
     *
     * <p>注意事项：</p>
     * <ul>
     *   <li>分布式锁场景下必须设置合理的过期时间，避免死锁</li>
     *   <li>调用方需保证 value 唯一性（通常使用 UUID），以便后续释放锁时校验</li>
     * </ul>
     */
    boolean setIfAbsent(String key, Object value, Duration ttl);

    /**
     * 自增（不存在则初始化为 0 再 +1）
     *
     * @param key Redis Key
     * @return 自增后的值
     *
     * <p>等价于 Redis 命令：INCR</p>
     *
     * <p>注意：若 Key 不存在，会先初始化为 0，再执行 +1。</p>
     */
    long increment(String key);

    /**
     * 按指定增量增加计数器
     *
     * @param key   Redis Key
     * @param delta 增量（可为负数，表示减少）
     * @return 增加后的值
     *
     * <p>等价于 Redis 命令：INCRBY</p>
     *
     * <p>注意：若 Key 不存在，会先初始化为 0，再执行增量操作。</p>
     */
    long incrementBy(String key, long delta);

    /**
     * 自减（不存在则初始化为 0 再 -1）
     *
     * @param key Redis Key
     * @return 自减后的值
     *
     * <p>等价于 Redis 命令：DECR</p>
     *
     * <p>注意：若 Key 不存在，会先初始化为 0，再执行 -1。</p>
     */
    long decrement(String key);

    /**
     * 初始化计数器并设置 TTL（常用于限流场景）
     *
     * @param key   Redis Key
     * @param ttl   过期时间，若为 null 或小于等于 0，则表示无过期
     * @return 自增后的值
     *
     * <p>等价于 Redis 命令：INCR + EXPIRE</p>
     *
     * <p>注意事项：</p>
     * <ul>
     *   <li>若 Key 不存在，会先初始化为 0，再执行 +1，并设置 TTL。</li>
     *   <li>若 Key 已存在，则只执行 +1，不会重置 TTL。</li>
     *   <li>适用于限流场景，例如统计某个时间窗口内的请求次数。</li>
     * </ul>
     */
    long increment(String key, Duration ttl);
}


