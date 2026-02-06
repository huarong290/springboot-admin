package com.springboot.admin.service.redis;


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

    CompletableFuture<Boolean> asyncSet(String key, Object value, Duration ttl);

    <T> CompletableFuture<Optional<T>> asyncGet(String key, Class<T> type);

    CompletableFuture<Boolean> asyncDelete(String key);

    CompletableFuture<Boolean> asyncExists(String key);

    CompletableFuture<Optional<Duration>> asyncTtl(String key);

    CompletableFuture<Long> asyncMultiSet(Map<String, Object> values, Duration ttl);

    CompletableFuture<List<Object>> asyncMultiGet(Collection<String> keys);

    CompletableFuture<Map<Object, Object>> asyncHMultiGet(String key, Collection<Object> fields);

    CompletableFuture<Long> asyncHMultiSet(String key, Map<String, Object> values);

    CompletableFuture<Long> asyncLPushAll(String key, Collection<Object> values);

    CompletableFuture<Long> asyncRPushAll(String key, Collection<Object> values);
}
