package com.springboot.admin.service.redis;


import java.time.Duration;
import java.util.Optional;

/**
 * Redis KV 服务接口
 *
 * <p>
 * 负责基础的 Key-Value 操作，适用于缓存、配置存储等场景。
 * </p>
 *
 * <p>
 * 设计目标：
 * <ul>
 *   <li>提供最常用的 KV 操作</li>
 *   <li>统一返回值语义，避免 null</li>
 *   <li>支持过期时间管理</li>
 * </ul>
 * </p>
 */
public interface IRedisKVService {

    /**
     * 设置 Key-Value（无过期时间）
     *
     * @return true=成功，false=失败
     */
    boolean set(String key, Object value);

    /**
     * 设置 Key-Value（带过期时间）
     *
     * @return true=成功，false=失败
     */
    boolean set(String key, Object value, Duration ttl);

    /**
     * 获取 Key 对应的值
     *
     * @return Optional 包装的值，不存在时返回 Optional.empty()
     */
    <T> Optional<T> get(String key, Class<T> type);

    /**
     * 删除 Key
     *
     * @return true=成功，false=失败或不存在
     */
    boolean delete(String key);

    /**
     * 判断 Key 是否存在
     */
    boolean exists(String key);

    /**
     * 获取 Key 剩余 TTL
     *
     * @return Optional 包装的 Duration，不存在或无过期时返回 Optional.empty()
     */
    Optional<Duration> ttl(String key);
}

