package com.springboot.admin.service.redis.core.impl;

import com.springboot.admin.service.redis.core.IRedisSetService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

/**
 * Redis Set 服务实现类
 *
 * <p>
 * 基于 Spring Data Redis 提供的 RedisTemplate 封装，实现了对 Redis Set 类型的常用操作。
 * </p>
 *
 * <p>
 * 设计目标：
 * </p>
 * <ul>
 *   <li>统一返回数量，便于监控与统计</li>
 *   <li>使用 Optional 包装返回值，避免返回 null</li>
 *   <li>保证调用安全性和可读性</li>
 * </ul>
 *
 * <p>
 * 适用场景：
 * </p>
 * <ul>
 *   <li>去重集合</li>
 *   <li>黑名单存储</li>
 *   <li>缓存集合数据</li>
 * </ul>
 */
@Service
public class RedisSetServiceImpl implements IRedisSetService {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisSetServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 向 Set 中添加一个或多个元素
     *
     * 等价于 Redis 命令：SADD
     *
     * @param key    Redis Set Key
     * @param values 要添加的元素（可变参数）
     * @return 成功添加的元素数量（不包含已存在的元素）
     *
     * <p>注意：若元素已存在，则不会重复添加。</p>
     */
    @Override
    public long sAdd(String key, Object... values) {
        Long result = redisTemplate.opsForSet().add(key, values);
        return result == null ? 0 : result;
    }

    /**
     * 从 Set 中移除一个或多个元素
     *
     * 等价于 Redis 命令：SREM
     *
     * @param key    Redis Set Key
     * @param values 要移除的元素（可变参数）
     * @return 成功移除的元素数量
     *
     * <p>注意：若元素不存在，则不会计入移除数量。</p>
     */
    @Override
    public long sRemove(String key, Object... values) {
        Long result = redisTemplate.opsForSet().remove(key, values);
        return result == null ? 0 : result;
    }

    /**
     * 判断某个元素是否存在于 Set 中
     *
     * 等价于 Redis 命令：SISMEMBER
     *
     * @param key   Redis Set Key
     * @param value 要判断的元素
     * @return true=存在，false=不存在
     */
    @Override
    public boolean sIsMember(String key, Object value) {
        Boolean result = redisTemplate.opsForSet().isMember(key, value);
        return result != null && result;
    }

    /**
     * 获取 Set 中的所有元素
     *
     * 等价于 Redis 命令：SMEMBERS
     *
     * @param key Redis Set Key
     * @return Set 对象，若 Set 不存在则返回空集合
     *
     * <p>注意：返回的集合不保证顺序。</p>
     */
    @Override
    public Set<Object> sMembers(String key) {
        Set<Object> members = redisTemplate.opsForSet().members(key);
        return members == null ? Collections.emptySet() : members;
    }

    /**
     * 获取 Set 的大小（元素数量）
     *
     * 等价于 Redis 命令：SCARD
     *
     * @param key Redis Set Key
     * @return 元素数量，若 Set 不存在则返回 0
     */
    @Override
    public long sSize(String key) {
        Long size = redisTemplate.opsForSet().size(key);
        return size == null ? 0 : size;
    }

    /**
     * 随机弹出一个元素并返回
     *
     * 等价于 Redis 命令：SPOP
     *
     * @param key Redis Set Key
     * @return Optional 包装的值：
     *         - Optional.empty() 表示 Set 不存在或为空
     *         - Optional.of(value) 表示成功弹出元素
     *
     * <p>注意：该操作会修改原 Set，删除被弹出的元素。</p>
     */
    @Override
    public Optional<Object> sPop(String key) {
        Object value = redisTemplate.opsForSet().pop(key);
        return Optional.ofNullable(value);
    }
}
