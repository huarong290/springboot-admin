package com.springboot.admin.service.redis.core;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

/**
 * Redis List 服务接口
 *
 * <p>
 * 提供对 Redis List 类型的常用操作，适用于队列、消息缓存、任务调度等场景。
 * </p>
 *
 * <p>
 * 设计目标：
 * </p>
 * <ul>
 *   <li>支持常见的队列操作（插入、弹出、查询、裁剪）</li>
 *   <li>统一返回值语义，避免返回 null</li>
 *   <li>支持阻塞式弹出，满足生产者-消费者模型</li>
 * </ul>
 */
public interface IRedisListService {

    /**
     * 从左侧插入一个元素到 List
     *
     * @param key   Redis List Key
     * @param value 要插入的值（需可序列化）
     * @return 插入后 List 的长度
     *
     * <p>等价于 Redis 命令：LPUSH</p>
     */
    long lPush(String key, Object value);

    /**
     * 从右侧插入一个元素到 List
     *
     * @param key   Redis List Key
     * @param value 要插入的值（需可序列化）
     * @return 插入后 List 的长度
     *
     * <p>等价于 Redis 命令：RPUSH</p>
     */
    long rPush(String key, Object value);

    /**
     * 从左侧弹出一个元素
     *
     * @param key Redis List Key
     * @return Optional 包装的值：
     *         - Optional.empty() 表示 List 不存在或为空
     *         - Optional.of(value) 表示成功弹出元素
     *
     * <p>等价于 Redis 命令：LPOP</p>
     */
    Optional<Object> lPop(String key);

    /**
     * 从右侧弹出一个元素
     *
     * @param key Redis List Key
     * @return Optional 包装的值：
     *         - Optional.empty() 表示 List 不存在或为空
     *         - Optional.of(value) 表示成功弹出元素
     *
     * <p>等价于 Redis 命令：RPOP</p>
     */
    Optional<Object> rPop(String key);

    /**
     * 获取指定区间的元素列表
     *
     * @param key   Redis List Key
     * @param start 起始索引（0 表示第一个元素，-1 表示最后一个元素）
     * @param end   结束索引（包含该位置，-1 表示最后一个元素）
     * @return List 对象，若 List 不存在则返回空列表
     *
     * <p>等价于 Redis 命令：LRANGE</p>
     */
    List<Object> lRange(String key, long start, long end);

    /**
     * 获取 List 的长度
     *
     * @param key Redis List Key
     * @return 长度，若 List 不存在则返回 0
     *
     * <p>等价于 Redis 命令：LLEN</p>
     */
    long lLen(String key);

    /**
     * 获取指定索引位置的元素
     *
     * @param key   Redis List Key
     * @param index 索引（0 表示第一个元素，-1 表示最后一个元素）
     * @return Optional 包装的值：
     *         - Optional.empty() 表示索引超出范围或 List 不存在
     *         - Optional.of(value) 表示成功获取到元素
     *
     * <p>等价于 Redis 命令：LINDEX</p>
     */
    Optional<Object> lIndex(String key, long index);

    /**
     * 裁剪 List，只保留指定区间的元素
     *
     * @param key   Redis List Key
     * @param start 起始索引
     * @param end   结束索引（包含该位置）
     *
     * <p>等价于 Redis 命令：LTRIM</p>
     *
     * <p>注意：该操作会修改原 List，删除区间外的元素。</p>
     */
    void lTrim(String key, long start, long end);

    /**
     * 从左侧阻塞式弹出一个元素
     *
     * @param key     Redis List Key
     * @param timeout 阻塞等待时间，若超时仍为空则返回 Optional.empty()
     * @return Optional 包装的值：
     *         - Optional.empty() 表示超时或 List 不存在
     *         - Optional.of(value) 表示成功弹出元素
     *
     * <p>等价于 Redis 命令：BLPOP</p>
     */
    Optional<Object> blPop(String key, Duration timeout);

    /**
     * 从右侧阻塞式弹出一个元素
     *
     * @param key     Redis List Key
     * @param timeout 阻塞等待时间，若超时仍为空则返回 Optional.empty()
     * @return Optional 包装的值：
     *         - Optional.empty() 表示超时或 List 不存在
     *         - Optional.of(value) 表示成功弹出元素
     *
     * <p>等价于 Redis 命令：BRPOP</p>
     */
    Optional<Object> brPop(String key, Duration timeout);
}
