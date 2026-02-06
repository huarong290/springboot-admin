package com.springboot.admin.service.redis.core;


import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Redis 异步服务接口
 *
 * <p>负责异步操作，返回 CompletableFuture：</p>
 * <ul>
 *   <li>asyncSet / asyncGet / asyncDelete / asyncExists / asyncTtl</li>
 *   <li>asyncMultiSet / asyncMultiGet</li>
 *   <li>asyncHMultiGet / asyncHMultiSet</li>
 *   <li>asyncLPushAll / asyncRPushAll</li>
 * </ul>
 */
public interface IRedisAsyncService {

    /**
     * 异步设置 Key-Value（带过期时间）
     *
     * @param key   Redis Key
     * @param value 要存储的值（需可序列化）
     * @param ttl   过期时间，若为 null 或小于等于 0，则表示无过期
     * @return CompletableFuture 包装的布尔值：
     *         true=成功，false=失败（如序列化错误、Redis 不可用）
     */
    CompletableFuture<Boolean> asyncSet(String key, Object value, Duration ttl);

    /**
     * 异步获取 Key 对应的值
     *
     * @param key  Redis Key
     * @param type 目标类型（用于反序列化）
     * @param <T>  泛型类型
     * @return CompletableFuture 包装的 Optional：
     *         - Optional.empty() 表示 Key 不存在或反序列化失败
     *         - Optional.of(value) 表示成功获取到值
     */
    <T> CompletableFuture<Optional<T>> asyncGet(String key, Class<T> type);

    /**
     * 异步删除 Key
     *
     * @param key Redis Key
     * @return CompletableFuture 包装的布尔值：
     *         true=成功删除，false=Key 不存在或删除失败
     */
    CompletableFuture<Boolean> asyncDelete(String key);

    /**
     * 异步判断 Key 是否存在
     *
     * @param key Redis Key
     * @return CompletableFuture 包装的布尔值：
     *         true=存在，false=不存在
     */
    CompletableFuture<Boolean> asyncExists(String key);

    /**
     * 异步获取 Key 剩余 TTL（过期时间）
     *
     * @param key Redis Key
     * @return CompletableFuture 包装的 Optional：
     *         - Optional.empty() 表示 Key 不存在或无过期时间
     *         - Optional.of(Duration) 表示剩余 TTL
     */
    CompletableFuture<Optional<Duration>> asyncTtl(String key);

    /**
     * 异步批量设置多个 Key-Value（统一过期时间）
     *
     * @param values Key-Value 映射
     * @param ttl    过期时间，若为 null 或小于等于 0，则表示无过期
     * @return CompletableFuture 包装的 Long：
     *         成功写入的 Key 数量
     */
    CompletableFuture<Long> asyncMultiSet(Map<String, Object> values, Duration ttl);

    /**
     * 异步批量获取多个 Key 的值
     *
     * @param keys Key 集合
     * @return CompletableFuture 包装的 List：
     *         按照输入顺序返回对应的值，若某个 Key 不存在则返回 null 占位
     */
    CompletableFuture<List<Object>> asyncMultiGet(Collection<String> keys);

    /**
     * 异步批量获取 Hash 中多个字段的值
     *
     * @param key    Redis Hash Key
     * @param fields 字段集合
     * @return CompletableFuture 包装的 Map：
     *         字段与对应的值映射，若字段不存在则不返回该字段
     */
    CompletableFuture<Map<Object, Object>> asyncHMultiGet(String key, Collection<Object> fields);

    /**
     * 异步批量设置 Hash 中多个字段的值
     *
     * @param key    Redis Hash Key
     * @param values 字段-值映射
     * @return CompletableFuture 包装的 Long：
     *         成功写入的字段数量
     */
    CompletableFuture<Long> asyncHMultiSet(String key, Map<String, Object> values);

    /**
     * 异步批量左插入 List
     *
     * @param key    Redis List Key
     * @param values 要插入的值集合
     * @return CompletableFuture 包装的 Long：
     *         插入后 List 的长度
     */
    CompletableFuture<Long> asyncLPushAll(String key, Collection<Object> values);

    /**
     * 异步批量右插入 List
     *
     * @param key    Redis List Key
     * @param values 要插入的值集合
     * @return CompletableFuture 包装的 Long：
     *         插入后 List 的长度
     */
    CompletableFuture<Long> asyncRPushAll(String key, Collection<Object> values);
}

