package com.springboot.admin.service.redis.core;

import java.util.Optional;
import java.util.Set;

/**
 * Redis ZSet 服务接口
 *
 * <p>
 * 提供对 Redis 有序集合（ZSet）的常用操作，适用于排行榜、限流、优先队列等场景。
 * </p>
 *
 * <p>
 * 设计目标：
 * </p>
 * <ul>
 *   <li>提供读写操作完整覆盖</li>
 *   <li>统一返回 Optional，避免返回 null</li>
 *   <li>支持区间统计与删除，便于数据管理</li>
 * </ul>
 */
public interface IRedisZSetService {

    /**
     * 向 ZSet 中添加一个元素及其分数
     *
     * @param key   Redis ZSet Key
     * @param value 元素值（需可序列化）
     * @param score 分数（用于排序，越小排名越靠前）
     * @return true=成功添加或更新分数，false=失败
     *
     * <p>等价于 Redis 命令：ZADD</p>
     *
     * <p>注意：若元素已存在，则会更新其分数。</p>
     */
    boolean zAdd(String key, Object value, double score);

    /**
     * 从 ZSet 中移除一个或多个元素
     *
     * @param key     Redis ZSet Key
     * @param members 要移除的元素
     * @return 成功移除的元素数量
     *
     * <p>等价于 Redis 命令：ZREM</p>
     */
    long zRemove(String key, Object... members);

    /**
     * 获取某个元素的分数
     *
     * @param key    Redis ZSet Key
     * @param member 元素值
     * @return Optional 包装的分数：
     *         - Optional.empty() 表示元素不存在
     *         - Optional.of(score) 表示成功获取分数
     *
     * <p>等价于 Redis 命令：ZSCORE</p>
     */
    Optional<Double> zScore(String key, Object member);

    /**
     * 获取某个元素的排名（升序）
     *
     * @param key    Redis ZSet Key
     * @param member 元素值
     * @return Optional 包装的排名：
     *         - Optional.empty() 表示元素不存在
     *         - Optional.of(rank) 表示成功获取排名（从 0 开始）
     *
     * <p>等价于 Redis 命令：ZRANK</p>
     */
    Optional<Long> zRank(String key, Object member);

    /**
     * 获取某个元素的排名（降序）
     *
     * @param key    Redis ZSet Key
     * @param member 元素值
     * @return Optional 包装的排名：
     *         - Optional.empty() 表示元素不存在
     *         - Optional.of(rank) 表示成功获取排名（从 0 开始）
     *
     * <p>等价于 Redis 命令：ZREVRANK</p>
     */
    Optional<Long> zRevRank(String key, Object member);

    /**
     * 获取指定区间的元素（升序）
     *
     * @param key   Redis ZSet Key
     * @param start 起始索引（0 表示第一个元素）
     * @param end   结束索引（包含该位置，-1 表示最后一个元素）
     * @return 元素集合，若 ZSet 不存在则返回空集合
     *
     * <p>等价于 Redis 命令：ZRANGE</p>
     */
    Set<Object> zRange(String key, long start, long end);

    /**
     * 获取指定区间的元素（降序）
     *
     * @param key   Redis ZSet Key
     * @param start 起始索引（0 表示第一个元素）
     * @param end   结束索引（包含该位置，-1 表示最后一个元素）
     * @return 元素集合，若 ZSet 不存在则返回空集合
     *
     * <p>等价于 Redis 命令：ZREVRANGE</p>
     */
    Set<Object> zRevRange(String key, long start, long end);

    /**
     * 统计分数区间内的元素数量
     *
     * @param key Redis ZSet Key
     * @param min 最小分数（包含）
     * @param max 最大分数（包含）
     * @return 区间内的元素数量
     *
     * <p>等价于 Redis 命令：ZCOUNT</p>
     */
    long zCount(String key, double min, double max);

    /**
     * 删除分数区间内的元素
     *
     * @param key Redis ZSet Key
     * @param min 最小分数（包含）
     * @param max 最大分数（包含）
     * @return 成功删除的元素数量
     *
     * <p>等价于 Redis 命令：ZREMRANGEBYSCORE</p>
     *
     * <p>注意：该操作会修改原 ZSet，删除符合条件的元素。</p>
     */
    long zRemoveRangeByScore(String key, double min, double max);
}
