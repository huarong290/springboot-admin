package com.springboot.admin.service.impl;

import com.springboot.admin.service.IRedisService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis 全能服务实现类
 *
 * <p>
 * <h3>核心特性：</h3>
 * <ol>
 * <li><b>Fail-Secure:</b> 所有 Redis 操作均包裹在 try-catch 中，Redis 宕机不会导致业务崩溃。</li>
 * <li><b>Log Optimization:</b> 智能日志降噪，Debug 模式打印堆栈，生产环境只打印错误信息，防止磁盘爆满。</li>
 * <li><b>Atomic Scripts:</b> 预加载 Lua 脚本，保证复杂操作的原子性。</li>
 * <li><b>Pipeline Support:</b> 实现了 MultiGet 等批量操作，提升高并发性能。</li>
 * </ol>
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements IRedisService {
    // 自动注入 Spring Boot 默认配置好的 StringRedisTemplate
    private final StringRedisTemplate stringRedisTemplate;
    // 注入我们自己在 RedisConfig 配置的 RedisTemplate (用于对象操作)
    private final RedisTemplate<String, Object> redisTemplate;
    // Lua 脚本：原子自增并设置过期
    private DefaultRedisScript<Long> incrWithExpireScript;
    // Lua 脚本：原子获取并删除
    private DefaultRedisScript<String> getAndDeleteScript;

    /**
     * 初始化 Lua 脚本 (利用 Script Load 缓存 SHA1)
     */
    @PostConstruct
    public void init() {
        incrWithExpireScript = new DefaultRedisScript<>();
        incrWithExpireScript.setResultType(Long.class);
        incrWithExpireScript.setScriptText(
                "local current = redis.call('INCR', KEYS[1]) " +
                        "if current == 1 then redis.call('PEXPIRE', KEYS[1], ARGV[1]) end " +
                        "return current"
        );

        getAndDeleteScript = new DefaultRedisScript<>();
        getAndDeleteScript.setResultType(String.class);
        getAndDeleteScript.setScriptText(
                "local v = redis.call('GET', KEYS[1]) " +
                        "if not v then return nil end " +
                        "redis.call('DEL', KEYS[1]) " +
                        "return v"
        );
    }
    // =================================================
    // 1. 通用操作实现
    // =================================================

    @Override
    public boolean expire(String key, long timeout, TimeUnit unit) {
        try {
            return Boolean.TRUE.equals(stringRedisTemplate.expire(key, timeout, unit));
        } catch (Exception e) {
            handleException("expire", key, e);
            return false;
        }
    }

    @Override
    public long getExpire(String key, TimeUnit unit) {
        try {
            Long expire = stringRedisTemplate.getExpire(key, unit);
            return expire != null ? expire : -2;
        } catch (Exception e) {
            handleException("getExpire", key, e);
            return -2;
        }
    }

    @Override
    public boolean hasKey(String key) {
        try {
            return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
        } catch (Exception e) {
            handleException("hasKey", key, e);
            return false;
        }
    }

    @Override
    public boolean deleteKey(String key) {
        try {
            return Boolean.TRUE.equals(stringRedisTemplate.delete(key));
        } catch (Exception e) {
            handleException("deleteKey", key, e);
            return false;
        }
    }

    @Override
    public boolean deleteKeys(Collection<String> keys) {
        if (keys == null || keys.isEmpty()) return false;
        try {
            Long count = stringRedisTemplate.delete(keys);
            return count != null && count > 0;
        } catch (Exception e) {
            handleException("deleteKeys", "size:" + keys.size(), e);
            return false;
        }
    }

    // =================================================
    // 2. String 操作实现
    // =================================================

    @Override
    public boolean setValue(String key, String value) {
        try {
            stringRedisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            handleException("setValue", key, e);
            return false;
        }
    }

    @Override
    public boolean setValue(String key, String value, long timeout, TimeUnit unit) {
        try {
            stringRedisTemplate.opsForValue().set(key, value, timeout, unit);
            return true;
        } catch (Exception e) {
            handleException("setValueWithExpire", key, e);
            return false;
        }
    }

    @Override
    public String getValue(String key) {
        try {
            return stringRedisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            handleException("getValue", key, e);
            return null;
        }
    }

    @Override
    public List<String> multiGet(Collection<String> keys) {
        if (keys == null || keys.isEmpty()) return Collections.emptyList();
        try {
            return stringRedisTemplate.opsForValue().multiGet(keys);
        } catch (Exception e) {
            handleException("multiGet", "size:" + keys.size(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public Long increment(String key) {
        try {
            return stringRedisTemplate.opsForValue().increment(key);
        } catch (Exception e) {
            handleException("increment", key, e);
            return null;
        }
    }

    @Override
    public Long increment(String key, long timeout, TimeUnit unit) {
        try {
            // Lua 参数需转为 String 传递
            return stringRedisTemplate.execute(incrWithExpireScript, Collections.singletonList(key), String.valueOf(unit.toMillis(timeout)));
        } catch (Exception e) {
            handleException("incrementWithExpire", key, e);
            return null;
        }
    }

    @Override
    public String getAndDelete(String key) {
        try {
            return stringRedisTemplate.execute(getAndDeleteScript, Collections.singletonList(key));
        } catch (Exception e) {
            handleException("getAndDelete", key, e);
            return null;
        }
    }
    // ===================== 2.1 对象缓存 =====================
    public boolean setObject(String key, Object value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
            return true;
        } catch (Exception e) {
            handleException("setObject", key, e);
            return false;
        }
    }

    public <T> T getObject(String key, Class<T> clazz) {
        try {
            Object obj = redisTemplate.opsForValue().get(key);
            if (obj == null) return null;
            return clazz.cast(obj);
        } catch (Exception e) {
            handleException("getObject", key, e);
            return null;
        }
    }
    // =================================================
    // 3. Hash 操作实现
    // =================================================

    @Override
    public boolean hSet(String key, String hashKey, String value) {
        try {
            stringRedisTemplate.opsForHash().put(key, hashKey, value);
            return true;
        } catch (Exception e) {
            handleException("hSet", key, e);
            return false;
        }
    }

    @Override
    public boolean hSet(String key, String hashKey, String value, long timeout, TimeUnit unit) {
        try {
            stringRedisTemplate.opsForHash().put(key, hashKey, value);
            return expire(key, timeout, unit);
        } catch (Exception e) {
            handleException("hSetWithExpire", key, e);
            return false;
        }
    }

    @Override
    public boolean hMSet(String key, Map<String, String> map) {
        try {
            stringRedisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            handleException("hMSet", key, e);
            return false;
        }
    }

    @Override
    public boolean hMSet(String key, Map<String, String> map, long timeout, TimeUnit unit) {
        try {
            stringRedisTemplate.opsForHash().putAll(key, map);
            return expire(key, timeout, unit);
        } catch (Exception e) {
            handleException("hMSetWithExpire", key, e);
            return false;
        }
    }

    @Override
    public String hGet(String key, String hashKey) {
        try {
            Object val = stringRedisTemplate.opsForHash().get(key, hashKey);
            return val != null ? val.toString() : null;
        } catch (Exception e) {
            handleException("hGet", key, e);
            return null;
        }
    }

    @Override
    public Map<Object, Object> hGetAll(String key) {
        try {
            return stringRedisTemplate.opsForHash().entries(key);
        } catch (Exception e) {
            handleException("hGetAll", key, e);
            return Collections.emptyMap();
        }
    }

    @Override
    public boolean hDel(String key, Object... hashKeys) {
        try {
            stringRedisTemplate.opsForHash().delete(key, hashKeys);
            return true;
        } catch (Exception e) {
            handleException("hDel", key, e);
            return false;
        }
    }

    @Override
    public boolean hHasKey(String key, String hashKey) {
        try {
            return stringRedisTemplate.opsForHash().hasKey(key, hashKey);
        } catch (Exception e) {
            handleException("hHasKey", key, e);
            return false;
        }
    }

    @Override
    public Long hIncr(String key, String hashKey, long delta) {
        try {
            return stringRedisTemplate.opsForHash().increment(key, hashKey, delta);
        } catch (Exception e) {
            handleException("hIncr", key, e);
            return null;
        }
    }

    // =================================================
    // 4. List 操作实现
    // =================================================

    @Override
    public boolean lPush(String key, String value) {
        try {
            stringRedisTemplate.opsForList().leftPush(key, value);
            return true;
        } catch (Exception e) {
            handleException("lPush", key, e);
            return false;
        }
    }

    @Override
    public boolean lPushAll(String key, List<String> values) {
        try {
            stringRedisTemplate.opsForList().leftPushAll(key, values);
            return true;
        } catch (Exception e) {
            handleException("lPushAll", key, e);
            return false;
        }
    }

    @Override
    public boolean rPush(String key, String value) {
        try {
            stringRedisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            handleException("rPush", key, e);
            return false;
        }
    }

    @Override
    public boolean rPushAll(String key, List<String> values) {
        try {
            stringRedisTemplate.opsForList().rightPushAll(key, values);
            return true;
        } catch (Exception e) {
            handleException("rPushAll", key, e);
            return false;
        }
    }

    @Override
    public String lPop(String key) {
        try {
            return stringRedisTemplate.opsForList().leftPop(key);
        } catch (Exception e) {
            handleException("lPop", key, e);
            return null;
        }
    }

    @Override
    public String rPop(String key) {
        try {
            return stringRedisTemplate.opsForList().rightPop(key);
        } catch (Exception e) {
            handleException("rPop", key, e);
            return null;
        }
    }

    @Override
    public List<String> lRange(String key, long start, long end) {
        try {
            return stringRedisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            handleException("lRange", key, e);
            return Collections.emptyList();
        }
    }

    @Override
    public long lLen(String key) {
        try {
            Long size = stringRedisTemplate.opsForList().size(key);
            return size != null ? size : 0;
        } catch (Exception e) {
            handleException("lLen", key, e);
            return 0;
        }
    }

    // =================================================
    // 5. Set 操作实现
    // =================================================

    @Override
    public boolean sAdd(String key, String... values) {
        try {
            Long count = stringRedisTemplate.opsForSet().add(key, values);
            return count != null && count > 0;
        } catch (Exception e) {
            handleException("sAdd", key, e);
            return false;
        }
    }

    @Override
    public boolean sAddWithExpire(String key, long timeout, TimeUnit unit, String... values) {
        try {
            Long count = stringRedisTemplate.opsForSet().add(key, values);
            expire(key, timeout, unit);
            return count != null && count > 0;
        } catch (Exception e) {
            handleException("sAddWithExpire", key, e);
            return false;
        }
    }

    @Override
    public boolean sIsMember(String key, String value) {
        try {
            return Boolean.TRUE.equals(stringRedisTemplate.opsForSet().isMember(key, value));
        } catch (Exception e) {
            handleException("sIsMember", key, e);
            return false;
        }
    }

    @Override
    public long sSize(String key) {
        try {
            Long size = stringRedisTemplate.opsForSet().size(key);
            return size != null ? size : 0;
        } catch (Exception e) {
            handleException("sSize", key, e);
            return 0;
        }
    }

    @Override
    public long sRemove(String key, Object... values) {
        try {
            Long count = stringRedisTemplate.opsForSet().remove(key, values);
            return count != null ? count : 0;
        } catch (Exception e) {
            handleException("sRemove", key, e);
            return 0;
        }
    }

    @Override
    public Set<String> sMembers(String key) {
        try {
            return stringRedisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            handleException("sMembers", key, e);
            return Collections.emptySet();
        }
    }

    // =================================================
    // 6. ZSet 操作实现
    // =================================================

    @Override
    public boolean zAdd(String key, String value, double score) {
        try {
            return Boolean.TRUE.equals(stringRedisTemplate.opsForZSet().add(key, value, score));
        } catch (Exception e) {
            handleException("zAdd", key, e);
            return false;
        }
    }

    @Override
    public Double zIncrScore(String key, String value, double delta) {
        try {
            return stringRedisTemplate.opsForZSet().incrementScore(key, value, delta);
        } catch (Exception e) {
            handleException("zIncrScore", key, e);
            return null;
        }
    }

    @Override
    public Long zRank(String key, String value) {
        try {
            return stringRedisTemplate.opsForZSet().rank(key, value);
        } catch (Exception e) {
            handleException("zRank", key, e);
            return null;
        }
    }

    @Override
    public Long zReverseRank(String key, String value) {
        try {
            return stringRedisTemplate.opsForZSet().reverseRank(key, value);
        } catch (Exception e) {
            handleException("zReverseRank", key, e);
            return null;
        }
    }

    @Override
    public Set<String> zReverseRange(String key, long start, long end) {
        try {
            return stringRedisTemplate.opsForZSet().reverseRange(key, start, end);
        } catch (Exception e) {
            handleException("zReverseRange", key, e);
            return Collections.emptySet();
        }
    }

    @Override
    public Set<ZSetOperations.TypedTuple<String>> zReverseRangeWithScores(String key, long start, long end) {
        try {
            return stringRedisTemplate.opsForZSet().reverseRangeWithScores(key, start, end);
        } catch (Exception e) {
            handleException("zReverseRangeWithScores", key, e);
            return Collections.emptySet();
        }
    }

    @Override
    public Double zScore(String key, String value) {
        try {
            return stringRedisTemplate.opsForZSet().score(key, value);
        } catch (Exception e) {
            handleException("zScore", key, e);
            return null;
        }
    }

    @Override
    public long zRemove(String key, Object... values) {
        try {
            Long count = stringRedisTemplate.opsForZSet().remove(key, values);
            return count != null ? count : 0;
        } catch (Exception e) {
            handleException("zRemove", key, e);
            return 0;
        }
    }

    // =================================================
    // 7. 高级与扫描操作实现
    // =================================================

    @Override
    public Set<String> scan(String pattern) {
        Set<String> keys = new HashSet<>();
        try {
            stringRedisTemplate.execute((org.springframework.data.redis.connection.RedisConnection connection) -> {

                ScanOptions options = ScanOptions.scanOptions().match(pattern).count(1000).build();
                try (Cursor<byte[]> cursor = connection.scan(options)) {
                    while (cursor.hasNext()) {
                        keys.add(new String(cursor.next()));
                    }
                }
                return null;
            });
            return keys;
        } catch (Exception e) {
            handleException("scan", pattern, e);
            return Collections.emptySet();
        }
    }

    // =================================================
    // 私有: 统一日志处理
    // =================================================

    /**
     * 智能日志处理
     * <p>策略：生产环境只打印简短错误消息，开发环境(Debug开启)打印完整堆栈。</p>
     */
    private void handleException(String op, String key, Exception e) {
        if (log.isDebugEnabled()) {
            log.error("Redis [{}] 失败. Key: {}", op, key, e);
        } else {
            // 避免生产环境磁盘被堆栈日志写满
            log.error("Redis [{}] 失败. Key: {}. Error: {}", op, key, e.getMessage());
        }
    }
}