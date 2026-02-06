package com.springboot.admin.service.redis;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * Redis Hash 服务接口
 *
 * <p>
 * 负责 Hash 类型操作，常用于登录态、Token、权限缓存等场景。
 * </p>
 *
 * <p>
 * 设计目标：
 * <ul>
 *   <li>支持单字段和多字段操作</li>
 *   <li>Pipeline 批量删除，提升性能</li>
 *   <li>明确原子性说明：Pipeline 不保证原子性</li>
 * </ul>
 * </p>
 */
public interface IRedisHashService {

    boolean hSet(String key, String field, Object value);

    <T> Optional<T> hGet(String key, String field, Class<T> type);

    Map<Object, Object> hGetAll(String key);

    /**
     * 批量删除 Hash 字段（Pipeline）
     *
     * <p>注意：不保证原子性，部分字段可能删除失败，调用方需兜底。</p>
     *
     * @return 成功删除的字段数量
     */
    long hDeletePipeline(String key, Collection<Object> fields);
}
