package com.springboot.admin.service.impl;

import com.springboot.admin.service.IRedisService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * Redis 服务实现类
 *
 * <p>
 * 基于 Spring Data Redis 的 StringRedisTemplate
 * 仅操作字符串类型，避免 JDK 序列化带来的兼容性问题
 * </p>
 *
 * <p>
 * 说明：
 * <ul>
 *     <li>所有 Redis 操作均捕获异常，避免影响主业务流程</li>
 *     <li>关键计数操作使用 Lua 脚本保证原子性</li>
 * </ul>
 * </p>
 */
@Slf4j
@Service
@AllArgsConstructor
public class RedisServiceImpl implements IRedisService {

    /**
     * Spring 提供的 Redis 操作模板（String 专用）
     */
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * Lua 脚本：原子性自增 + 首次设置过期时间
     *
     * <pre>
     * 执行逻辑：
     * 1. 对 key 执行 INCR
     * 2. 如果结果等于 1，说明是第一次创建该 key
     * 3. 设置过期时间
     * 4. 返回当前值
     * </pre>
     *
     * <p>
     * KEYS[1] = Redis Key
     * ARGV[1] = 过期时间（秒）
     * </p>
     */
    private static final String INCR_WITH_EXPIRE_LUA =
            "local current = redis.call('INCR', KEYS[1]) " +
                    "if current == 1 then " +
                    "   redis.call('EXPIRE', KEYS[1], ARGV[1]) " +
                    "end " +
                    "return current";

    /**
     * 设置字符串值（带过期时间）
     */
    @Override
    public boolean setValue(String key, String value, long timeout, TimeUnit unit) {
        try {
            stringRedisTemplate.opsForValue().set(key, value, timeout, unit);
            return true;
        } catch (Exception e) {
            log.error("Redis setValue 失败: key={}, value={}", key, value, e);
            return false;
        }
    }

    /**
     * 获取字符串值
     */
    @Override
    public String getValue(String key) {
        try {
            return stringRedisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Redis getValue 失败: key={}", key, e);
            return null;
        }
    }

    /**
     * 删除指定 key
     */
    @Override
    public boolean deleteKey(String key) {
        try {
            Boolean result = stringRedisTemplate.delete(key);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("Redis deleteKey 失败: key={}", key, e);
            return false;
        }
    }

    /**
     * 判断 key 是否存在
     */
    @Override
    public boolean hasKey(String key) {
        try {
            Boolean exists = stringRedisTemplate.hasKey(key);
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            log.error("Redis hasKey 失败: key={}", key, e);
            return false;
        }
    }

    /**
     * 设置 key 过期时间
     */
    @Override
    public boolean expire(String key, long timeout, TimeUnit unit) {
        try {
            Boolean result = stringRedisTemplate.expire(key, timeout, unit);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("Redis expire 失败: key={}", key, e);
            return false;
        }
    }

    /**
     * 自增（不设置过期时间）
     */
    @Override
    public Long increment(String key) {
        try {
            return stringRedisTemplate.opsForValue().increment(key);
        } catch (Exception e) {
            log.error("Redis increment 失败: key={}", key, e);
            return null;
        }
    }

    /**
     * 原子自增（首次设置过期时间）
     */
    @Override
    public Long increment(String key, long timeout, TimeUnit unit) {
        try {
            // 将过期时间统一转换为秒（Redis EXPIRE 使用秒）
            long expireSeconds = unit.toSeconds(timeout);

            return stringRedisTemplate.execute(
                    // Lua 脚本 + 返回值类型
                    new DefaultRedisScript<>(INCR_WITH_EXPIRE_LUA, Long.class),
                    // KEYS 参数
                    Collections.singletonList(key),
                    // ARGV 参数
                    String.valueOf(expireSeconds)
            );
        } catch (Exception e) {
            log.error("Redis increment(with expire) 失败: key={}", key, e);
            return null;
        }
    }
}
