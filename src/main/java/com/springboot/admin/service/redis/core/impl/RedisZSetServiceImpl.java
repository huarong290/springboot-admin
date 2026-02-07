package com.springboot.admin.service.redis.core.impl;


import com.springboot.admin.service.redis.core.IRedisZSetService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

/**
 * Redis ZSet 服务实现类
 *
 * <p>
 * 基于 Spring Data Redis 提供的 RedisTemplate 封装，实现了对 Redis 有序集合（ZSet）的常用操作。
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
 *
 * <p>
 * 适用场景：
 * </p>
 * <ul>
 *   <li>排行榜</li>
 *   <li>限流统计</li>
 *   <li>优先队列</li>
 * </ul>
 */
@Service
public class RedisZSetServiceImpl implements IRedisZSetService {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisZSetServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 向 ZSet 中添加一个元素及其分数
     *
     * 等价于 Redis 命令：ZADD
     *
     * @param key   Redis ZSet Key
     * @param value 元素值（需可序列化）
     * @param score 分数（用于排序，越小排名越靠前）
     * @return true=成功添加或更新分数，false=失败
     */
    @Override
    public boolean zAdd(String key, Object value, double score) {
        Boolean result = redisTemplate.opsForZSet().add(key, value, score);
        return result != null && result;
    }

    /**
     * 从 ZSet 中移除一个或多个元素
     *
     * 等价于 Redis 命令：ZREM
     *
     * @param key     Redis ZSet Key
     * @param members 要移除的元素
     * @return 成功移除的元素数量
     */
    @Override
    public long zRemove(String key, Object... members) {
        Long result = redisTemplate.opsForZSet().remove(key, members);
        return result == null ? 0 : result;
    }

    /**
     * 获取某个元素的分数
     *
     * 等价于 Redis 命令：ZSCORE
     *
     * @param key    Redis ZSet Key
     * @param member 元素值
     * @return Optional 包装的分数：
     *         - Optional.empty() 表示元素不存在
     *         - Optional.of(score) 表示成功获取分数
     */
    @Override
    public Optional<Double> zScore(String key, Object member) {
        Double score = redisTemplate.opsForZSet().score(key, member);
        return Optional.ofNullable(score);
    }

    /**
     * 获取某个元素的排名（升序）
     *
     * 等价于 Redis 命令：ZRANK
     *
     * @param key    Redis ZSet Key
     * @param member 元素值
     * @return Optional 包装的排名：
     *         - Optional.empty() 表示元素不存在
     *         - Optional.of(rank) 表示成功获取排名（从 0 开始）
     */
    @Override
    public Optional<Long> zRank(String key, Object member) {
        Long rank = redisTemplate.opsForZSet().rank(key, member);
        return Optional.ofNullable(rank);
    }

    /**
     * 获取某个元素的排名（降序）
     *
     * 等价于 Redis 命令：ZREVRANK
     *
     * @param key    Redis ZSet Key
     * @param member 元素值
     * @return Optional 包装的排名：
     *         - Optional.empty() 表示元素不存在
     *         - Optional.of(rank) 表示成功获取排名（从 0 开始）
     */
    @Override
    public Optional<Long> zRevRank(String key, Object member) {
        Long rank = redisTemplate.opsForZSet().reverseRank(key, member);
        return Optional.ofNullable(rank);
    }

    /**
     * 获取指定区间的元素（升序）
     *
     * 等价于 Redis 命令：ZRANGE
     *
     * @param key   Redis ZSet Key
     * @param start 起始索引（0 表示第一个元素）
     * @param end   结束索引（包含该位置，-1 表示最后一个元素）
     * @return 元素集合，若 ZSet 不存在则返回空集合
     */
    @Override
    public Set<Object> zRange(String key, long start, long end) {
        Set<Object> result = redisTemplate.opsForZSet().range(key, start, end);
        return result == null ? Collections.emptySet() : result;
    }

    /**
     * 获取指定区间的元素（降序）
     *
     * 等价于 Redis 命令：ZREVRANGE
     *
     * @param key   Redis ZSet Key
     * @param start 起始索引（0 表示第一个元素）
     * @param end   结束索引（包含该位置，-1 表示最后一个元素）
     * @return 元素集合，若 ZSet 不存在则返回空集合
     */
    @Override
    public Set<Object> zRevRange(String key, long start, long end) {
        Set<Object> result = redisTemplate.opsForZSet().reverseRange(key, start, end);
        return result == null ? Collections.emptySet() : result;
    }

    /**
     * 统计分数区间内的元素数量
     *
     * 等价于 Redis 命令：ZCOUNT
     *
     * @param key Redis ZSet Key
     * @param min 最小分数（包含）
     * @param max 最大分数（包含）
     * @return 区间内的元素数量
     */
    @Override
    public long zCount(String key, double min, double max) {
        Long count = redisTemplate.opsForZSet().count(key, min, max);
        return count == null ? 0 : count;
    }

    /**
     * 删除分数区间内的元素
     *
     * 等价于 Redis 命令：ZREMRANGEBYSCORE
     *
     * @param key Redis ZSet Key
     * @param min 最小分数（包含）
     * @param max 最大分数（包含）
     * @return 成功删除的元素数量
     *
     * <p>注意：该操作会修改原 ZSet，删除符合条件的元素。</p>
     */
    @Override
    public long zRemoveRangeByScore(String key, double min, double max) {
        Long removed = redisTemplate.opsForZSet().removeRangeByScore(key, min, max);
        return removed == null ? 0 : removed;
    }
}

