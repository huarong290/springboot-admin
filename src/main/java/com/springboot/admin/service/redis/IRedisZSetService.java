package com.springboot.admin.service.redis;


import java.util.Optional;
import java.util.Set;

/**
 * Redis ZSet 服务接口
 *
 * <p>
 * 负责有序集合操作，常用于排行榜、限流、优先队列等场景。
 * </p>
 *
 * <p>
 * 设计目标：
 * <ul>
 *   <li>提供读写操作完整覆盖</li>
 *   <li>统一返回 Optional，避免 null</li>
 *   <li>支持区间统计与删除</li>
 * </ul>
 * </p>
 */
public interface IRedisZSetService {

    boolean zAdd(String key, Object value, double score);

    long zRemove(String key, Object... members);

    Optional<Double> zScore(String key, Object member);

    Optional<Long> zRank(String key, Object member);

    Optional<Long> zRevRank(String key, Object member);

    Set<Object> zRange(String key, long start, long end);

    Set<Object> zRevRange(String key, long start, long end);

    long zCount(String key, double min, double max);

    long zRemoveRangeByScore(String key, double min, double max);
}
