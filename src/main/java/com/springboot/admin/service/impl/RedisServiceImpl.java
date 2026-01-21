package com.springboot.admin.service.impl;

import com.springboot.admin.service.IRedisService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * Redis 服务实现类 (企业级优化版)
 *
 * <p>
 * <h3>核心设计：</h3>
 * <ul>
 * <li><b>基于 StringRedisTemplate：</b> 仅操作 String 格式，避免 JDK 序列化导致的跨语言/版本兼容性问题。</li>
 * <li><b>Lua 脚本原子性：</b> 关键业务（如计数器初始化、令牌销毁）使用 Lua 保证原子性。</li>
 * <li><b>Fail-Secure 策略：</b> 捕获底层 Redis 异常，防止因缓存服务抖动导致主业务崩溃（返回 null/false 让业务层决定降级）。</li>
 * </ul>
 * </p>
 *
 * <p>
 * <h3>⭐ 优化记录：</h3>
 * <ol>
 * <li><b>脚本预加载：</b> 使用 {@code @PostConstruct} 初始化 Lua 脚本对象，利用 Redis EVALSHA 特性大幅减少网络传输开销。</li>
 * <li><b>毫秒级精度：</b> 计数器过期时间由 {@code EXPIRE} (秒) 升级为 {@code PEXPIRE} (毫秒)，防止短时间限流（如 500ms）失效。</li>
 * <li><b>规范注入：</b> 采用 {@code @RequiredArgsConstructor} 构造器注入，确保依赖不可变。</li>
 * </ol>
 * </p>
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements IRedisService {

    private final StringRedisTemplate stringRedisTemplate;

    // ==========================================
    // Lua 脚本对象 (预加载以提升性能)
    // ==========================================
    private DefaultRedisScript<Long> incrWithExpireScript;
    private DefaultRedisScript<String> getAndDeleteScript;

    /**
     * 初始化 Lua 脚本
     * <p>
     *  优化点：
     * 避免在方法内部重复 new DefaultRedisScript。
     * Spring Data Redis 会自动计算脚本 SHA1 摘要并缓存，后续请求只发送摘要不发送脚本全文。
     * </p>
     */
    @PostConstruct
    public void init() {
        // 脚本 1: 原子自增并首次设置过期时间
        incrWithExpireScript = new DefaultRedisScript<>();
        incrWithExpireScript.setResultType(Long.class);
        incrWithExpireScript.setScriptText(
                "local current = redis.call('INCR', KEYS[1]) " +
                        "if current == 1 then " +
                        "   redis.call('PEXPIRE', KEYS[1], ARGV[1]) " + // ⭐ 改为 PEXPIRE 支持毫秒
                        "end " +
                        "return current"
        );

        // 脚本 2: 原子获取并删除 (Get and Delete)
        getAndDeleteScript = new DefaultRedisScript<>();
        getAndDeleteScript.setResultType(String.class);
        getAndDeleteScript.setScriptText(
                "local v = redis.call('GET', KEYS[1]) " +
                        "if not v then return nil end " +
                        "redis.call('DEL', KEYS[1]) " +
                        "return v"
        );
    }

    // ==========================================
    // 接口实现
    // ==========================================

    /**
     * 设置 Key-Value 对（带过期时间）
     *
     * @param key     Redis Key
     * @param value   存储的值
     * @param timeout 过期时间
     * @param unit    时间单位
     * @return true=设置成功, false=发生异常
     */
    @Override
    public boolean setValue(String key, String value, long timeout, TimeUnit unit) {
        try {
            stringRedisTemplate.opsForValue().set(key, value, timeout, unit);
            return true;
        } catch (Exception e) {
            log.error("Redis setValue 异常: key={}, value={}", key, value, e);
            return false;
        }
    }

    /**
     * 获取字符串值
     *
     * @param key Redis Key
     * @return 值，如果 Key 不存在或发生异常返回 null
     */
    @Override
    public String getValue(String key) {
        try {
            return stringRedisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Redis getValue 异常: key={}", key, e);
            return null;
        }
    }

    /**
     * 删除 Key
     *
     * @param key Redis Key
     * @return true=删除成功(或Key原就不存在), false=发生异常
     */
    @Override
    public boolean deleteKey(String key) {
        try {
            return stringRedisTemplate.delete(key);
        } catch (Exception e) {
            log.error("Redis deleteKey 异常: key={}", key, e);
            return false;
        }
    }

    /**
     * 检查 Key 是否存在
     *
     * @param key Redis Key
     * @return true=存在, false=不存在或异常
     */
    @Override
    public boolean hasKey(String key) {
        try {
            return stringRedisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("Redis hasKey 异常: key={}", key, e);
            return false;
        }
    }

    /**
     * 设置过期时间
     *
     * @param key     Redis Key
     * @param timeout 过期时间
     * @param unit    时间单位
     * @return true=设置成功, false=Key不存在或异常
     */
    @Override
    public boolean expire(String key, long timeout, TimeUnit unit) {
        try {
            return stringRedisTemplate.expire(key, timeout, unit);
        } catch (Exception e) {
            log.error("Redis expire 异常: key={}", key, e);
            return false;
        }
    }

    /**
     * 简单自增
     *
     * @param key Redis Key
     * @return 自增后的值
     */
    @Override
    public Long increment(String key) {
        try {
            return stringRedisTemplate.opsForValue().increment(key);
        } catch (Exception e) {
            log.error("Redis increment 异常: key={}", key, e);
            return null;
        }
    }

    /**
     * 原子自增并设置过期时间（首次创建时设置）
     * <p>
     * 场景：限流计数器（如：限制 1 分钟内访问 10 次）
     * </p>
     *
     * @param key     Redis Key
     * @param timeout 过期时间
     * @param unit    时间单位
     * @return 自增后的值
     */
    @Override
    public Long increment(String key, long timeout, TimeUnit unit) {
        try {
            // ⭐ 优化点：统一转换为毫秒，配合 Lua 中的 PEXPIRE
            // 避免 unit.toSeconds(500ms) 结果为 0 导致 Key 立即被删
            long expireMillis = unit.toMillis(timeout);

            return stringRedisTemplate.execute(
                    incrWithExpireScript,
                    Collections.singletonList(key),
                    String.valueOf(expireMillis)
            );
        } catch (Exception e) {
            log.error("Redis increment(with expire) 异常: key={}", key, e);
            return null;
        }
    }

    /**
     * 原子获取并删除 Key
     * <p>
     * 场景：Refresh Token 一次性使用、验证码一次性校验
     * </p>
     *
     * @param key Redis Key
     * @return Key 对应的值，如果 Key 不存在返回 null
     */
    @Override
    public String getAndDelete(String key) {
        try {
            // ⭐ 提示：Redis 6.2+ 原生支持 GETDEL 命令，但在 Spring Boot 旧版本中兼容性不一
            // 使用 Lua 脚本可兼容 Redis 2.6+ 所有版本
            return stringRedisTemplate.execute(
                    getAndDeleteScript,
                    Collections.singletonList(key)
            );
        } catch (Exception e) {
            log.error("Redis getAndDelete 异常: key={}", key, e);
            return null;
        }
    }
}