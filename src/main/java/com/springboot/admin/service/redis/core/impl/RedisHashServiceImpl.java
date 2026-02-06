package com.springboot.admin.service.redis.core.impl;

import com.springboot.admin.service.redis.core.IRedisHashService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Redis Hash 服务实现类（最终版）
 *
 * <p>
 * 提供对 Redis Hash 类型的常用操作，适用于存储结构化数据（如对象属性、配置项）。
 * </p>
 *
 * <p>
 * 特性：
 * <ul>
 *   <li>所有方法均带异常处理，避免业务抛出异常</li>
 *   <li>返回值语义统一：失败时返回 false / Optional.empty() / 空 Map / 0</li>
 *   <li>hDeletePipeline 使用 Pipeline 提升性能，但不保证原子性</li>
 *   <li>序列化器强制转换，避免泛型推断错误（capture of ?）</li>
 * </ul>
 * </p>
 */
@Service
public class RedisHashServiceImpl implements IRedisHashService {

    private static final Logger log = LoggerFactory.getLogger(RedisHashServiceImpl.class);

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisHashServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // ==================== HSET ====================

    /**
     * 设置 Hash 中某个字段的值
     *
     * @param key   Redis Hash Key
     * @param field 字段名
     * @param value 要存储的值（需可序列化）
     * @return true=成功，false=失败（如 Redis 不可用、序列化错误）
     *
     * <p>注意：若字段已存在，则会覆盖原值。</p>
     */
    @Override
    public boolean hSet(String key, String field, Object value) {
        try {
            redisTemplate.opsForHash().put(key, field, value);
            return true;
        } catch (Exception e) {
            log.error("Redis HSET 操作异常 key={} field={}", key, field, e);
            return false;
        }
    }

    // ==================== HGET ====================

    /**
     * 获取 Hash 中某个字段的值
     *
     * @param key   Redis Hash Key
     * @param field 字段名
     * @param type  目标类型（用于反序列化）
     * @param <T>   泛型类型
     * @return Optional 包装的值：
     *         - Optional.empty() 表示字段不存在或反序列化失败
     *         - Optional.of(value) 表示成功获取到值
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> hGet(String key, String field, Class<T> type) {
        try {
            Object val = redisTemplate.opsForHash().get(key, field);
            if (val == null) {
                return Optional.empty();
            }
            try {
                return Optional.of((T) val);
            } catch (ClassCastException e) {
                log.error("Redis HGET 类型转换失败 key={} field={} targetType={}", key, field, type, e);
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Redis HGET 操作异常 key={} field={}", key, field, e);
            return Optional.empty();
        }
    }

    // ==================== HGETALL ====================

    /**
     * 获取整个 Hash 的所有字段和值
     *
     * @param key Redis Hash Key
     * @return Map 映射：
     *         - Key=字段名，Value=对应的值
     *         - 若 Hash 不存在，则返回空 Map
     *
     * <p>注意：返回的 Map 不保证顺序。</p>
     */
    @Override
    public Map<Object, Object> hGetAll(String key) {
        try {
            Map<Object, Object> result = redisTemplate.opsForHash().entries(key);
            return result != null ? result : Collections.emptyMap();
        } catch (Exception e) {
            log.error("Redis HGETALL 操作异常 key={}", key, e);
            return Collections.emptyMap();
        }
    }

    // ==================== HDEL (Pipeline) ====================

    /**
     * 批量删除 Hash 中的多个字段（Pipeline）
     *
     * @param key    Redis Hash Key
     * @param fields 要删除的字段集合（字符串类型）
     * @return 成功删除的字段数量
     *
     * <p>
     * 注意事项：
     * <ul>
     *   <li>使用 Pipeline 提升性能，但不保证原子性。</li>
     *   <li>部分字段可能删除失败（如字段不存在、Redis 异常）。</li>
     *   <li>调用方需自行兜底处理失败场景。</li>
     * </ul>
     * </p>
     */
    @Override
    public long hDeletePipeline(String key, Collection<String> fields) {
        if (fields == null || fields.isEmpty()) {
            return 0L;
        }
        try {
            Long deletedCount = redisTemplate.execute((RedisCallback<Long>) connection -> {
                byte[] rawKey = redisTemplate.getStringSerializer().serialize(key);
                if (rawKey == null) return 0L;

                RedisSerializer<Object> serializer =
                        (RedisSerializer<Object>) redisTemplate.getHashKeySerializer();

                byte[][] rawFields = fields.stream()
                        .map(serializer::serialize)
                        .toArray(byte[][]::new);

                Long result = connection.hashCommands().hDel(rawKey, rawFields);
                return result == null ? 0L : result;
            });
            return deletedCount == null ? 0L : deletedCount;
        } catch (Exception e) {
            log.error("Redis HDEL Pipeline 操作异常 key={} fields={}", key, fields, e);
            return 0L;
        }
    }

}
