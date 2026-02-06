package com.springboot.admin.service.redis.core;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * Redis Hash 服务接口
 *
 * <p>
 * 提供对 Redis Hash 类型的常用操作，适用于存储结构化数据（如对象属性、配置项）。
 * </p>
 *
 * <p>
 * 设计目标：
 * <ul>
 *   <li>统一返回值语义，避免返回 null</li>
 *   <li>支持单字段操作与批量操作</li>
 *   <li>明确异常与非原子性场景的处理方式</li>
 * </ul>
 * </p>
 */
public interface IRedisHashService {

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
    boolean hSet(String key, String field, Object value);

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
    <T> Optional<T> hGet(String key, String field, Class<T> type);

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
    Map<Object, Object> hGetAll(String key);

    /**
     * 批量删除 Hash 中的多个字段（Pipeline）
     *
     * @param key    Redis Hash Key
     * @param fields 要删除的字段集合
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
    long hDeletePipeline(String key, Collection<String> fields);
}
