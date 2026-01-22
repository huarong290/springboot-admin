package com.springboot.admin.service.impl;

import com.springboot.admin.service.IRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * RedisServiceImpl - 企业级 Redis 全能服务实现
 *
 * <p>核心特性：
 * 1. 支持 Redis 五大数据结构操作：String、Object、Hash、List、Set、ZSet
 * 2. 支持 Scan 非阻塞扫描
 * 3. Fail-Secure 异常处理：Redis 异常不会影响业务逻辑
 * 4. Lua 脚本集中管理，实现原子操作：自增+过期、getAndDelete
 * 5. 泛型对象存取，保持序列化规则一致
 *
 * <p>设计原则：
 * - 高内聚：Lua 脚本和异常处理封装在 Service 内部
 * - DRY：对象操作统一通过 RedisTemplate，序列化规则全局一致
 * - 安全可靠：生产环境日志降噪，异常可控
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements IRedisService {

    // ==========================
    // 注入 Redis 模板
    // ==========================
    private final StringRedisTemplate stringRedisTemplate; // 用于字符串操作
    private final RedisTemplate<String, Object> redisTemplate; // 用于对象操作 (JSON 序列化)

    // ==========================
    // Lua 脚本集中管理
    // ==========================
    private static final DefaultRedisScript<Long> INCR_WITH_EXPIRE_SCRIPT;
    private static final DefaultRedisScript<String> GET_AND_DELETE_SCRIPT;

    static {
        // Lua 脚本：原子自增并设置过期时间
        INCR_WITH_EXPIRE_SCRIPT = new DefaultRedisScript<>(
                "local current = redis.call('INCR', KEYS[1]) " +
                        "if current == 1 then redis.call('PEXPIRE', KEYS[1], ARGV[1]) end " +
                        "return current", Long.class
        );

        // Lua 脚本：原子获取并删除
        GET_AND_DELETE_SCRIPT = new DefaultRedisScript<>(
                "local v = redis.call('GET', KEYS[1]) " +
                        "if not v then return nil end " +
                        "redis.call('DEL', KEYS[1]) " +
                        "return v", String.class
        );
    }

    // ==========================
    // 1. 通用 Key 操作
    // ==========================

    /**
     * 设置 Key 的过期时间
     *
     * @param key Redis 键
     * @param timeout 过期时间
     * @param unit 时间单位
     * @return true 设置成功，false 失败
     */
    @Override
    public boolean expire(String key, long timeout, TimeUnit unit) {
        return executeSafe(() -> stringRedisTemplate.expire(key, timeout, unit), key);
    }

    /**
     * 获取 Key 的剩余过期时间
     *
     * @param key Redis 键
     * @param unit 时间单位
     * @return 剩余过期时间，单位由 unit 指定，-2 表示 key 不存在
     */
    @Override
    public long getExpire(String key, TimeUnit unit) {
        return executeSafe(() -> {
            Long expire = stringRedisTemplate.getExpire(key, unit);
            return expire != null ? expire : -2;
        }, key, -2L);
    }

    /**
     * 检查 Key 是否存在
     *
     * @param key Redis 键
     * @return true 存在，false 不存在
     */
    @Override
    public boolean hasKey(String key) {
        return executeSafe(() -> stringRedisTemplate.hasKey(key), key);
    }

    /**
     * 删除单个 Key
     *
     * @param key Redis 键
     * @return true 删除成功，false 失败或 key 不存在
     */
    @Override
    public boolean deleteKey(String key) {
        return executeSafe(() -> stringRedisTemplate.delete(key), key);
    }

    /**
     * 删除多个 Key
     *
     * @param keys Redis 键集合
     * @return true 删除成功，false 失败
     */
    @Override
    public boolean deleteKeys(Collection<String> keys) {
        if (keys == null || keys.isEmpty()) return false;
        return executeSafe(() -> {
            Long count = stringRedisTemplate.delete(keys);
            return count != null && count > 0;
        }, "size:" + keys.size());
    }

    // ==========================
    // 2. String 操作
    // ==========================

    /**
     * 设置 String 值
     *
     * @param key Redis 键
     * @param value 字符串值
     * @return true 成功，false 失败
     */
    @Override
    public boolean setValue(String key, String value) {
        return executeSafe(() -> { stringRedisTemplate.opsForValue().set(key, value); return true; }, key);
    }

    /**
     * 设置 String 值，并指定过期时间
     *
     * @param key Redis 键
     * @param value 字符串值
     * @param timeout 过期时间
     * @param unit 时间单位
     * @return true 成功，false 失败
     */
    @Override
    public boolean setValue(String key, String value, long timeout, TimeUnit unit) {
        return executeSafe(() -> { stringRedisTemplate.opsForValue().set(key, value, timeout, unit); return true; }, key);
    }

    /**
     * 获取 String 值
     *
     * @param key Redis 键
     * @return String 值，key 不存在返回 null
     */
    @Override
    public String getValue(String key) {
        return executeSafe(() -> stringRedisTemplate.opsForValue().get(key), key, null);
    }

    /**
     * 批量获取 String 值
     *
     * @param keys Redis 键集合
     * @return 值列表，不存在的 key 返回 null
     */
    @Override
    public List<String> multiGet(Collection<String> keys) {
        if (keys == null || keys.isEmpty()) return Collections.emptyList();
        return executeSafe(() -> stringRedisTemplate.opsForValue().multiGet(keys), "size:" + keys.size(), Collections.emptyList());
    }

    /**
     * 自增 Key 的数值
     *
     * @param key Redis 键
     * @return 增量后的值，失败返回 null
     */
    @Override
    public Long increment(String key) {
        return executeSafe(() -> stringRedisTemplate.opsForValue().increment(key), key, null);
    }

    /**
     * 自增 Key 的数值，并设置过期时间
     *
     * @param key Redis 键
     * @param timeout 过期时间
     * @param unit 时间单位
     * @return 增量后的值，失败返回 null
     */
    @Override
    public Long increment(String key, long timeout, TimeUnit unit) {
        return executeSafe(() ->
                        stringRedisTemplate.execute(INCR_WITH_EXPIRE_SCRIPT, Collections.singletonList(key), String.valueOf(unit.toMillis(timeout))),
                key, null
        );
    }

    /**
     * 原子获取并删除 Key
     *
     * @param key Redis 键
     * @return 删除前的值，不存在返回 null
     */
    @Override
    public String getAndDelete(String key) {
        return executeSafe(() -> stringRedisTemplate.execute(GET_AND_DELETE_SCRIPT, Collections.singletonList(key)), key, null);
    }

    // ==========================
    // 3. Object / 泛型操作
    // ==========================

    /**
     * 设置对象到 Redis，并指定过期时间
     *
     * @param key Redis 键
     * @param value 对象值
     * @param timeout 过期时间
     * @param unit 时间单位
     * @return true 成功，false 失败
     */
    @Override
    public boolean setObject(String key, Object value, long timeout, TimeUnit unit) {
        return executeSafe(() -> { redisTemplate.opsForValue().set(key, value, timeout, unit); return true; }, key);
    }

    /**
     * 获取对象
     *
     * @param key Redis 键
     * @param clazz 对象类型
     * @param <T> 泛型类型
     * @return 对象实例，key 不存在返回 null
     */
    @Override
    public <T> T getObject(String key, Class<T> clazz) {
        return executeSafe(() -> {
            Object obj = redisTemplate.opsForValue().get(key);
            return obj != null ? clazz.cast(obj) : null;
        }, key, null);
    }

    // ==========================
    // 4. Hash 操作
    // ==========================

    /**
     * Hash 单个字段设置
     *
     * @param key Redis 键
     * @param hashKey Hash 字段
     * @param value 值
     * @return true 成功，false 失败
     */
    @Override
    public boolean hSet(String key, String hashKey, String value) {
        return executeSafe(() -> { stringRedisTemplate.opsForHash().put(key, hashKey, value); return true; }, key);
    }

    /**
     * Hash 单个字段设置，并指定过期时间
     *
     * @param key Redis 键
     * @param hashKey Hash 字段
     * @param value 值
     * @param timeout 过期时间
     * @param unit 时间单位
     * @return true 成功，false 失败
     */
    @Override
    public boolean hSet(String key, String hashKey, String value, long timeout, TimeUnit unit) {
        return executeSafe(() -> {
            stringRedisTemplate.opsForHash().put(key, hashKey, value);
            return expire(key, timeout, unit);
        }, key);
    }

    /**
     * Hash 多字段设置
     *
     * @param key Redis 键
     * @param map 字段和值集合
     * @return true 成功，false 失败
     */
    @Override
    public boolean hMSet(String key, Map<String, String> map) {
        return executeSafe(() -> { stringRedisTemplate.opsForHash().putAll(key, map); return true; }, key);
    }

    /**
     * Hash 多字段设置，并指定过期时间
     *
     * @param key Redis 键
     * @param map 字段和值集合
     * @param timeout 过期时间
     * @param unit 时间单位
     * @return true 成功，false 失败
     */
    @Override
    public boolean hMSet(String key, Map<String, String> map, long timeout, TimeUnit unit) {
        return executeSafe(() -> {
            stringRedisTemplate.opsForHash().putAll(key, map);
            return expire(key, timeout, unit);
        }, key);
    }

    /**
     * Hash 获取单字段
     *
     * @param key Redis 键
     * @param hashKey Hash 字段
     * @return 字段值，不存在返回 null
     */
    @Override
    public String hGet(String key, String hashKey) {
        return executeSafe(() -> {
            Object val = stringRedisTemplate.opsForHash().get(key, hashKey);
            return val != null ? val.toString() : null;
        }, key, null);
    }

    /**
     * Hash 获取所有字段和值
     *
     * @param key Redis 键
     * @return Map，key 不存在返回空 Map
     */
    @Override
    public Map<Object, Object> hGetAll(String key) {
        return executeSafe(() -> stringRedisTemplate.opsForHash().entries(key), key, Collections.emptyMap());
    }

    /**
     * 删除 Hash 字段
     *
     * @param key Redis 键
     * @param hashKeys Hash 字段
     * @return true 成功，false 失败
     */
    @Override
    public boolean hDel(String key, Object... hashKeys) {
        return executeSafe(() -> { stringRedisTemplate.opsForHash().delete(key, hashKeys); return true; }, key);
    }

    /**
     * 判断 Hash 字段是否存在
     *
     * @param key Redis 键
     * @param hashKey Hash 字段
     * @return true 存在，false 不存在
     */
    @Override
    public boolean hHasKey(String key, String hashKey) {
        return executeSafe(() -> stringRedisTemplate.opsForHash().hasKey(key, hashKey), key);
    }

    /**
     * Hash 数值自增
     *
     * @param key Redis 键
     * @param hashKey Hash 字段
     * @param delta 增量
     * @return 增量后的值，失败返回 null
     */
    @Override
    public Long hIncr(String key, String hashKey, long delta) {
        return executeSafe(() -> stringRedisTemplate.opsForHash().increment(key, hashKey, delta), key, null);
    }

    // ==========================
    // 5. List 操作
    // ==========================

    /**
     * List 左插入
     *
     * @param key Redis 键
     * @param value 值
     * @return true 成功，false 失败
     */
    @Override
    public boolean lPush(String key, String value) {
        return executeSafe(() -> { stringRedisTemplate.opsForList().leftPush(key, value); return true; }, key);
    }

    /**
     * List 左插入多值
     *
     * @param key Redis 键
     * @param values 值集合
     * @return true 成功，false 失败
     */
    @Override
    public boolean lPushAll(String key, List<String> values) {
        return executeSafe(() -> { stringRedisTemplate.opsForList().leftPushAll(key, values); return true; }, key);
    }

    /**
     * List 右插入
     *
     * @param key Redis 键
     * @param value 值
     * @return true 成功，false 失败
     */
    @Override
    public boolean rPush(String key, String value) {
        return executeSafe(() -> { stringRedisTemplate.opsForList().rightPush(key, value); return true; }, key);
    }

    /**
     * List 右插入多值
     *
     * @param key Redis 键
     * @param values 值集合
     * @return true 成功，false 失败
     */
    @Override
    public boolean rPushAll(String key, List<String> values) {
        return executeSafe(() -> { stringRedisTemplate.opsForList().rightPushAll(key, values); return true; }, key);
    }

    /**
     * List 左弹出
     *
     * @param key Redis 键
     * @return 弹出的值，key 不存在返回 null
     */
    @Override
    public String lPop(String key) {
        return executeSafe(() -> stringRedisTemplate.opsForList().leftPop(key), key, null);
    }

    /**
     * List 右弹出
     *
     * @param key Redis 键
     * @return 弹出的值，key 不存在返回 null
     */
    @Override
    public String rPop(String key) {
        return executeSafe(() -> stringRedisTemplate.opsForList().rightPop(key), key, null);
    }

    /**
     * 获取 List 指定范围
     *
     * @param key Redis 键
     * @param start 起始索引
     * @param end 结束索引
     * @return List 范围值列表
     */
    @Override
    public List<String> lRange(String key, long start, long end) {
        return executeSafe(() -> stringRedisTemplate.opsForList().range(key, start, end), key, Collections.emptyList());
    }

    /**
     * 获取 List 长度
     *
     * @param key Redis 键
     * @return 长度，key 不存在返回 0
     */
    @Override
    public long lLen(String key) {
        return executeSafe(() -> {
            Long size = stringRedisTemplate.opsForList().size(key);
            return size != null ? size : 0;
        }, key, 0L);
    }

    // ==========================
    // 6. Set 操作
    // ==========================

    /**
     * 添加 Set 成员
     *
     * @param key Redis 键
     * @param values 值集合
     * @return true 成功，false 失败
     */
    @Override
    public boolean sAdd(String key, String... values) {
        return executeSafe(() -> {
            Long count = stringRedisTemplate.opsForSet().add(key, values);
            return count != null && count > 0;
        }, key);
    }

    /**
     * 添加 Set 成员并设置过期时间
     *
     * @param key Redis 键
     * @param timeout 过期时间
     * @param unit 时间单位
     * @param values 值集合
     * @return true 成功，false 失败
     */
    @Override
    public boolean sAddWithExpire(String key, long timeout, TimeUnit unit, String... values) {
        return executeSafe(() -> {
            Long count = stringRedisTemplate.opsForSet().add(key, values);
            expire(key, timeout, unit);
            return count != null && count > 0;
        }, key);
    }

    /**
     * 判断 Set 成员是否存在
     *
     * @param key Redis 键
     * @param value 值
     * @return true 存在，false 不存在
     */
    @Override
    public boolean sIsMember(String key, String value) {
        return executeSafe(() -> Boolean.TRUE.equals(stringRedisTemplate.opsForSet().isMember(key, value)), key);
    }

    /**
     * 获取 Set 成员数量
     *
     * @param key Redis 键
     * @return 成员数量，key 不存在返回 0
     */
    @Override
    public long sSize(String key) {
        return executeSafe(() -> {
            Long size = stringRedisTemplate.opsForSet().size(key);
            return size != null ? size : 0;
        }, key, 0L);
    }

    /**
     * 删除 Set 成员
     *
     * @param key Redis 键
     * @param values 成员集合
     * @return 删除数量
     */
    @Override
    public long sRemove(String key, Object... values) {
        return executeSafe(() -> {
            Long count = stringRedisTemplate.opsForSet().remove(key, values);
            return count != null ? count : 0;
        }, key, 0L);
    }

    /**
     * 获取 Set 所有成员
     *
     * @param key Redis 键
     * @return Set 成员集合
     */
    @Override
    public Set<String> sMembers(String key) {
        return executeSafe(() -> stringRedisTemplate.opsForSet().members(key), key, Collections.emptySet());
    }

    // ==========================
    // 7. ZSet 操作
    // ==========================

    /**
     * 添加 ZSet 成员
     *
     * @param key Redis 键
     * @param value 值
     * @param score 分数
     * @return true 成功，false 失败
     */
    @Override
    public boolean zAdd(String key, String value, double score) {
        return executeSafe(() -> Boolean.TRUE.equals(stringRedisTemplate.opsForZSet().add(key, value, score)), key);
    }

    /**
     * ZSet 成员分数自增
     *
     * @param key Redis 键
     * @param value 值
     * @param delta 增量
     * @return 增量后的分数
     */
    @Override
    public Double zIncrScore(String key, String value, double delta) {
        return executeSafe(() -> stringRedisTemplate.opsForZSet().incrementScore(key, value, delta), key, null);
    }

    /**
     * 获取 ZSet 排名 (从小到大)
     *
     * @param key Redis 键
     * @param value 值
     * @return 排名 (0-based)，不存在返回 null
     */
    @Override
    public Long zRank(String key, String value) {
        return executeSafe(() -> stringRedisTemplate.opsForZSet().rank(key, value), key, null);
    }

    /**
     * 获取 ZSet 排名 (从大到小)
     *
     * @param key Redis 键
     * @param value 值
     * @return 排名 (0-based)，不存在返回 null
     */
    @Override
    public Long zReverseRank(String key, String value) {
        return executeSafe(() -> stringRedisTemplate.opsForZSet().reverseRank(key, value), key, null);
    }

    /**
     * 获取 ZSet 指定范围成员 (从大到小)
     *
     * @param key Redis 键
     * @param start 起始索引
     * @param end 结束索引
     * @return 值集合
     */
    @Override
    public Set<String> zReverseRange(String key, long start, long end) {
        return executeSafe(() -> stringRedisTemplate.opsForZSet().reverseRange(key, start, end), key, Collections.emptySet());
    }

    /**
     * 获取 ZSet 指定范围成员及分数 (从大到小)
     *
     * @param key Redis 键
     * @param start 起始索引
     * @param end 结束索引
     * @return 值及分数集合
     */
    @Override
    public Set<ZSetOperations.TypedTuple<String>> zReverseRangeWithScores(String key, long start, long end) {
        return executeSafe(() -> stringRedisTemplate.opsForZSet().reverseRangeWithScores(key, start, end), key, Collections.emptySet());
    }

    /**
     * 获取 ZSet 成员分数
     *
     * @param key Redis 键
     * @param value 值
     * @return 分数，不存在返回 null
     */
    @Override
    public Double zScore(String key, String value) {
        return executeSafe(() -> stringRedisTemplate.opsForZSet().score(key, value), key, null);
    }

    /**
     * 删除 ZSet 成员
     *
     * @param key Redis 键
     * @param values 值集合
     * @return 删除数量
     */
    @Override
    public long zRemove(String key, Object... values) {
        return executeSafe(() -> {
            Long count = stringRedisTemplate.opsForZSet().remove(key, values);
            return count != null ? count : 0;
        }, key, 0L);
    }

    // ==========================
    // 8. Scan 高级操作
    // ==========================

    /**
     * Scan 扫描匹配 Key
     *
     * @param pattern 匹配模式，例如 user:*
     * @return 匹配的 Key 集合
     */
    @Override
    public Set<String> scan(String pattern) {
        Set<String> keys = new HashSet<>();
        return executeSafe(() -> {
            stringRedisTemplate.execute((RedisConnection connection) -> {
                ScanOptions options = ScanOptions.scanOptions().match(pattern).count(1000).build();
                try (Cursor<byte[]> cursor = connection.scan(options)) {
                    while (cursor.hasNext()) {
                        keys.add(new String(cursor.next()));
                    }
                }
                return null;
            });
            return keys;
        }, pattern, Collections.emptySet());
    }

    // ==========================
    // Fail-Secure 异常封装
    // ==========================

    /**
     * 安全执行 Redis 操作，异常返回默认值
     *
     * @param supplier 执行方法
     * @param key 日志上下文 Key
     * @param defaultValue 异常时返回的默认值
     * @param <T> 返回类型
     * @return 返回执行结果或默认值
     */
    private <T> T executeSafe(SupplierWithException<T> supplier, String key, T defaultValue) {
        try {
            return supplier.get();
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.error("Redis 操作失败. Key: {}", key, e);
            } else {
                log.error("Redis 操作失败. Key: {}. Error: {}", key, e.getMessage());
            }
            return defaultValue;
        }
    }

    private <T> T executeSafe(SupplierWithException<T> supplier, String key) {
        return executeSafe(supplier, key, null);
    }

    @FunctionalInterface
    private interface SupplierWithException<T> {
        T get() throws Exception;
    }
}
