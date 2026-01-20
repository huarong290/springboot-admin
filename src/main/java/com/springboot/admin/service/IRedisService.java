package com.springboot.admin.service;

import java.util.concurrent.TimeUnit;

/**
 * Redis 服务接口
 *
 * <p>
 * 封装常用 Redis 操作，供业务层统一调用
 * 基于 Spring Data Redis（StringRedisTemplate）
 * 采用同步阻塞方式，适用于 Spring MVC / Spring Boot 项目
 * </p>
 *
 * <p>
 * 设计原则：
 * <ul>
 *     <li>不暴露 Redis API 到业务层</li>
 *     <li>屏蔽异常，统一返回安全结果</li>
 *     <li>支持限流 / 计数 / JWT 黑名单等典型场景</li>
 * </ul>
 * </p>
 */
public interface IRedisService {

    /**
     * 设置字符串值（带过期时间）
     *
     * @param key Redis Key
     * @param value Redis Value（字符串）
     * @param timeout 过期时间
     * @param unit 时间单位
     * @return 是否设置成功
     */
    boolean setValue(String key, String value, long timeout, TimeUnit unit);

    /**
     * 获取字符串值
     *
     * @param key Redis Key
     * @return value，不存在或异常返回 null
     */
    String getValue(String key);

    /**
     * 删除指定 key
     *
     * @param key Redis Key
     * @return 是否删除成功
     */
    boolean deleteKey(String key);

    /**
     * 判断 key 是否存在
     *
     * @param key Redis Key
     * @return true = 存在，false = 不存在或异常
     */
    boolean hasKey(String key);

    /**
     * 设置 key 的过期时间
     *
     * @param key Redis Key
     * @param timeout 过期时间
     * @param unit 时间单位
     * @return 是否设置成功
     */
    boolean expire(String key, long timeout, TimeUnit unit);

    /**
     * 自增（不设置过期时间）
     *
     * <p>
     * 适用于：
     * <ul>
     *     <li>全局计数</li>
     *     <li>永久统计数据</li>
     * </ul>
     * </p>
     *
     * @param key Redis Key
     * @return 自增后的值，失败返回 null
     */
    Long increment(String key);

    /**
     * 原子自增（首次自增时设置过期时间）
     *
     * <p>
     * 典型使用场景：
     * <ul>
     *     <li>接口限流</li>
     *     <li>登录失败次数限制</li>
     *     <li>验证码发送频率控制</li>
     * </ul>
     * </p>
     *
     * <p>
     * 说明：
     * <ul>
     *     <li>Redis INCR + EXPIRE 通过 Lua 脚本保证原子性</li>
     *     <li>只有第一次自增（value=1）时才会设置过期时间</li>
     * </ul>
     * </p>
     *
     * @param key Redis Key
     * @param timeout 过期时间
     * @param unit 时间单位
     * @return 自增后的值，失败返回 null
     */
    Long increment(String key, long timeout, TimeUnit unit);
}
