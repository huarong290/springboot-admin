package com.springboot.admin.service.redis.core;

import java.util.Optional;
import java.util.Set;

/**
 * Redis Set 服务接口
 *
 * <p>
 * 提供对 Redis Set 类型的常用操作，适用于去重、黑名单、集合缓存等场景。
 * </p>
 *
 * <p>
 * 设计目标：
 * </p>
 * <ul>
 *   <li>统一返回数量，便于监控与统计</li>
 *   <li>使用 Optional 包装返回值，避免返回 null</li>
 * </ul>
 */
public interface IRedisSetService {

    /**
     * 向 Set 中添加一个或多个元素
     *
     * @param key    Redis Set Key
     * @param values 要添加的元素（可变参数）
     * @return 成功添加的元素数量（不包含已存在的元素）
     *
     * <p>等价于 Redis 命令：SADD</p>
     *
     * <p>注意：若元素已存在，则不会重复添加。</p>
     */
    long sAdd(String key, Object... values);

    /**
     * 从 Set 中移除一个或多个元素
     *
     * @param key    Redis Set Key
     * @param values 要移除的元素（可变参数）
     * @return 成功移除的元素数量
     *
     * <p>等价于 Redis 命令：SREM</p>
     *
     * <p>注意：若元素不存在，则不会计入移除数量。</p>
     */
    long sRemove(String key, Object... values);

    /**
     * 判断某个元素是否存在于 Set 中
     *
     * @param key   Redis Set Key
     * @param value 要判断的元素
     * @return true=存在，false=不存在
     *
     * <p>等价于 Redis 命令：SISMEMBER</p>
     */
    boolean sIsMember(String key, Object value);

    /**
     * 获取 Set 中的所有元素
     *
     * @param key Redis Set Key
     * @return Set 对象，若 Set 不存在则返回空集合
     *
     * <p>等价于 Redis 命令：SMEMBERS</p>
     *
     * <p>注意：返回的集合不保证顺序。</p>
     */
    Set<Object> sMembers(String key);

    /**
     * 获取 Set 的大小（元素数量）
     *
     * @param key Redis Set Key
     * @return 元素数量，若 Set 不存在则返回 0
     *
     * <p>等价于 Redis 命令：SCARD</p>
     */
    long sSize(String key);

    /**
     * 随机弹出一个元素并返回
     *
     * @param key Redis Set Key
     * @return Optional 包装的值：
     *         - Optional.empty() 表示 Set 不存在或为空
     *         - Optional.of(value) 表示成功弹出元素
     *
     * <p>等价于 Redis 命令：SPOP</p>
     *
     * <p>注意：该操作会修改原 Set，删除被弹出的元素。</p>
     */
    Optional<Object> sPop(String key);
}
