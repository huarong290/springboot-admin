package com.springboot.admin.service.impl;

import com.springboot.admin.service.IRedisService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * Redis 服务实现类
 * <p>
 * 基于 Spring Data Redis 的 StringRedisTemplate
 * 提供常用字符串、过期、删除、自增等操作
 */
@Slf4j
@Service
@AllArgsConstructor
public class RedisServiceImpl implements IRedisService {

    private final StringRedisTemplate stringRedisTemplate;


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
     * 删除 key
     */
    @Override
    public long deleteKey(String key) {
        try {
            Long count = stringRedisTemplate.delete(Collections.singleton(key));
            return count;
        } catch (Exception e) {
            log.error("Redis deleteKey 失败: key={}", key, e);
            return 0L;
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
     * 自增（用于限流、计数）
     */
    @Override
    public Long increment(String key) {
        try {
            return stringRedisTemplate.opsForValue().increment(key);
        } catch (Exception e) {
            log.error("Redis increment 失败: key={}", key, e);
            return 0L;
        }
    }
}
