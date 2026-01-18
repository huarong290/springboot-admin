package com.springboot.admin.service;

import java.util.concurrent.TimeUnit;

/**
 * Redis 服务接口
 * <p>
 * 封装常用 Redis 操作，供业务层调用
 * 采用同步阻塞方式，适用于传统 Spring MVC 架构
 */
public interface IRedisService {

    /**
     * 设置字符串值（带过期时间）
     *
     * @param key Redis key
     * @param value Redis value
     * @param timeout 过期时间
     * @param unit 时间单位
     * @return 是否设置成功
     */
    boolean setValue(String key, String value, long timeout, TimeUnit unit);

    /**
     * 获取字符串值
     *
     * @param key Redis key
     * @return value，不存在返回 null
     */
    String getValue(String key);

    /**
     * 删除 key
     *
     * @param key Redis key
     * @return 删除数量（0 = key 不存在或删除失败）
     */
    long deleteKey(String key);;

    /**
     * 判断 key 是否存在
     *
     * @param key Redis key
     * @return 是否存在
     */
    boolean hasKey(String key);

    /**
     * 设置 key 过期时间
     *
     * @param key Redis key
     * @param timeout 过期时间
     * @param unit 时间单位
     * @return 是否成功
     */
    boolean expire(String key, long timeout, TimeUnit unit);

    /**
     * 自增（用于限流、计数）
     *
     * @param key Redis key
     * @return 自增后的值
     */
    Long increment(String key);
}

