package com.springboot.admin.service.redis.core.impl;

import com.springboot.admin.service.redis.core.IRedisListService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Redis List 服务实现类
 *
 * <p>
 * 基于 Spring Data Redis 提供的 RedisTemplate 封装，实现了对 Redis List 类型的常用操作。
 * </p>
 *
 * <p>
 * 设计目标：
 * </p>
 * <ul>
 *   <li>统一返回值语义，避免返回 null</li>
 *   <li>提供 Optional 封装，提升调用安全性</li>
 *   <li>支持阻塞式弹出，满足生产者-消费者模型</li>
 * </ul>
 *
 * <p>
 * 适用场景：
 * </p>
 * <ul>
 *   <li>消息队列</li>
 *   <li>任务调度</li>
 *   <li>缓存列表数据</li>
 * </ul>
 */
@Service
public class RedisListServiceImpl implements IRedisListService {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisListServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 从左侧插入一个元素到 List
     *
     * 等价于 Redis 命令：LPUSH
     *
     * @param key   Redis List Key
     * @param value 要插入的值（需可序列化）
     * @return 插入后 List 的长度，若失败则返回 0
     */
    @Override
    public long lPush(String key, Object value) {
        Long result = redisTemplate.opsForList().leftPush(key, value);
        return result == null ? 0 : result;
    }

    /**
     * 从右侧插入一个元素到 List
     *
     * 等价于 Redis 命令：RPUSH
     *
     * @param key   Redis List Key
     * @param value 要插入的值（需可序列化）
     * @return 插入后 List 的长度，若失败则返回 0
     */
    @Override
    public long rPush(String key, Object value) {
        Long result = redisTemplate.opsForList().rightPush(key, value);
        return result == null ? 0 : result;
    }

    /**
     * 从左侧弹出一个元素
     *
     * 等价于 Redis 命令：LPOP
     *
     * @param key Redis List Key
     * @return Optional 包装的值：
     *         - Optional.empty() 表示 List 不存在或为空
     *         - Optional.of(value) 表示成功弹出元素
     */
    @Override
    public Optional<Object> lPop(String key) {
        Object value = redisTemplate.opsForList().leftPop(key);
        return Optional.ofNullable(value);
    }

    /**
     * 从右侧弹出一个元素
     *
     * 等价于 Redis 命令：RPOP
     *
     * @param key Redis List Key
     * @return Optional 包装的值：
     *         - Optional.empty() 表示 List 不存在或为空
     *         - Optional.of(value) 表示成功弹出元素
     */
    @Override
    public Optional<Object> rPop(String key) {
        Object value = redisTemplate.opsForList().rightPop(key);
        return Optional.ofNullable(value);
    }

    /**
     * 获取指定区间的元素列表
     *
     * 等价于 Redis 命令：LRANGE
     *
     * @param key   Redis List Key
     * @param start 起始索引（0 表示第一个元素，-1 表示最后一个元素）
     * @param end   结束索引（包含该位置，-1 表示最后一个元素）
     * @return List 对象，若 List 不存在则返回空列表
     */
    @Override
    public List<Object> lRange(String key, long start, long end) {
        List<Object> list = redisTemplate.opsForList().range(key, start, end);
        return list == null ? Collections.emptyList() : list;
    }

    /**
     * 获取 List 的长度
     *
     * 等价于 Redis 命令：LLEN
     *
     * @param key Redis List Key
     * @return 长度，若 List 不存在则返回 0
     */
    @Override
    public long lLen(String key) {
        Long size = redisTemplate.opsForList().size(key);
        return size == null ? 0 : size;
    }

    /**
     * 获取指定索引位置的元素
     *
     * 等价于 Redis 命令：LINDEX
     *
     * @param key   Redis List Key
     * @param index 索引（0 表示第一个元素，-1 表示最后一个元素）
     * @return Optional 包装的值：
     *         - Optional.empty() 表示索引超出范围或 List 不存在
     *         - Optional.of(value) 表示成功获取到元素
     */
    @Override
    public Optional<Object> lIndex(String key, long index) {
        Object value = redisTemplate.opsForList().index(key, index);
        return Optional.ofNullable(value);
    }

    /**
     * 裁剪 List，只保留指定区间的元素
     *
     * 等价于 Redis 命令：LTRIM
     *
     * @param key   Redis List Key
     * @param start 起始索引
     * @param end   结束索引（包含该位置）
     *
     * <p>注意：该操作会修改原 List，删除区间外的元素。</p>
     */
    @Override
    public void lTrim(String key, long start, long end) {
        redisTemplate.opsForList().trim(key, start, end);
    }

    /**
     * 从左侧阻塞式弹出一个元素
     *
     * 等价于 Redis 命令：BLPOP
     *
     * @param key     Redis List Key
     * @param timeout 阻塞等待时间，若超时仍为空则返回 Optional.empty()
     * @return Optional 包装的值：
     *         - Optional.empty() 表示超时或 List 不存在
     *         - Optional.of(value) 表示成功弹出元素
     */
    @Override
    public Optional<Object> blPop(String key, Duration timeout) {
        Object value = redisTemplate.opsForList()
                .leftPop(key, timeout.toMillis(), TimeUnit.MILLISECONDS);
        return Optional.ofNullable(value);
    }

    /**
     * 从右侧阻塞式弹出一个元素
     *
     * 等价于 Redis 命令：BRPOP
     *
     * @param key     Redis List Key
     * @param timeout 阻塞等待时间，若超时仍为空则返回 Optional.empty()
     * @return Optional 包装的值：
     *         - Optional.empty() 表示超时或 List 不存在
     *         - Optional.of(value) 表示成功弹出元素
     */
    @Override
    public Optional<Object> brPop(String key, Duration timeout) {
        Object value = redisTemplate.opsForList()
                .rightPop(key, timeout.toMillis(), TimeUnit.MILLISECONDS);
        return Optional.ofNullable(value);
    }
}
