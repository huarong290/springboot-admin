package com.springboot.admin.service.redis.core;

import java.time.Duration;
import java.util.Optional;

/**
 * Redis KV 服务接口
 *
 * <p>
 * 提供对 Redis Key-Value 类型的常用操作，适用于缓存、配置存储等场景。
 * </p>
 *
 * <p>
 * 设计目标：
 * </p>
 * <ul>
 *   <li>提供最常用的 KV 操作（增、查、删、存在性判断、TTL）</li>
 *   <li>统一返回值语义，避免返回 null</li>
 *   <li>支持过期时间管理，便于缓存控制</li>
 * </ul>
 */
public interface IRedisKVService {

    /**
     * 设置 Key-Value（无过期时间）
     *
     * @param key   Redis Key
     * @param value 要存储的值（需可序列化）
     * @return true=成功，false=失败（如 Redis 不可用、序列化错误）
     *
     * <p>注意：若 Key 已存在，则会覆盖原值。</p>
     */
    boolean set(String key, Object value);

    /**
     * 设置 Key-Value（带过期时间）
     *
     * @param key   Redis Key
     * @param value 要存储的值（需可序列化）
     * @param ttl   过期时间，若为 null 或小于等于 0，则表示无过期
     * @return true=成功，false=失败（如 Redis 不可用、序列化错误）
     *
     * <p>注意：若 Key 已存在，则会覆盖原值。</p>
     */
    boolean set(String key, Object value, Duration ttl);

    /**
     * 获取 Key 对应的值
     *
     * @param key  Redis Key
     * @param type 目标类型（用于反序列化）
     * @param <T>  泛型类型
     * @return Optional 包装的值：
     *         - Optional.empty() 表示 Key 不存在或反序列化失败
     *         - Optional.of(value) 表示成功获取到值
     */
    <T> Optional<T> get(String key, Class<T> type);

    /**
     * 删除 Key
     *
     * @param key Redis Key
     * @return true=成功删除，false=Key 不存在或删除失败
     *
     * <p>注意：删除不存在的 Key 返回 false。</p>
     */
    boolean delete(String key);

    /**
     * 判断 Key 是否存在
     *
     * @param key Redis Key
     * @return true=存在，false=不存在
     */
    boolean exists(String key);

    /**
     * 获取 Key 剩余 TTL（过期时间）
     *
     * @param key Redis Key
     * @return Optional 包装的 Duration：
     *         - Optional.empty() 表示 Key 不存在或无过期时间
     *         - Optional.of(Duration) 表示剩余 TTL
     *
     * <p>注意：Redis 区分三种情况：
     * <ul>
     *   <li>Key 不存在 → 返回 Optional.empty()</li>
     *   <li>Key 存在但无过期时间 → 返回 Optional.empty()</li>
     *   <li>Key 存在且有剩余 TTL → 返回 Optional.of(Duration)</li>
     * </ul>
     * </p>
     */
    Optional<Duration> ttl(String key);
}
