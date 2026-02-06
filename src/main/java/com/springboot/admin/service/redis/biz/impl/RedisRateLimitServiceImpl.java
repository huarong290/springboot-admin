package com.springboot.admin.service.redis.biz.impl;

import com.springboot.admin.service.redis.biz.IRedisRateLimitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

/**
 * Redis 限流服务实现类（最终优化版）
 *
 * <p>
 * 提供滑动窗口、固定窗口、令牌桶、漏桶四种限流算法的实现。
 * 使用 Redis + Lua 脚本保证分布式场景下的原子性，并通过 TTL 避免长期未访问的 key 占用内存。
 * </p>
 *
 * <p>
 * 优化点：
 * </p>
 * <ul>
 *   <li>滑动窗口：ZSET 存储时间戳，Lua 脚本设置 TTL，避免无限增长</li>
 *   <li>固定窗口：Lua 脚本保证 INCR + EXPIRE 原子性，避免高并发下重复设置 TTL</li>
 *   <li>令牌桶：Lua 脚本保证补充 + 消耗原子性，并设置 TTL</li>
 *   <li>漏桶：Hash 存储 last + count，使用毫秒级时间戳，漏水更平滑，并设置 TTL</li>
 *   <li>异常处理：所有 Redis 调用加 try/catch，日志记录并返回安全失败结果</li>
 *   <li>脚本缓存：所有 Lua 脚本静态缓存，避免 GC 压力</li>
 * </ul>
 */
@Service
public class RedisRateLimitServiceImpl implements IRedisRateLimitService {

    private static final Logger log = LoggerFactory.getLogger(RedisRateLimitServiceImpl.class);

    private final RedisTemplate<String, String> redisTemplate;

    // ==================== Lua 脚本缓存 ====================
    private static final DefaultRedisScript<Long> SLIDING_WINDOW_SCRIPT;
    private static final DefaultRedisScript<Long> FIXED_WINDOW_SCRIPT;
    private static final DefaultRedisScript<List> TOKEN_BUCKET_SCRIPT;
    private static final DefaultRedisScript<List> LEAKY_BUCKET_SCRIPT;

    static {
        // 滑动窗口：移除过期数据 + 添加当前请求 + 设置 TTL + 返回数量
        SLIDING_WINDOW_SCRIPT = new DefaultRedisScript<>(
                "redis.call('ZREMRANGEBYSCORE', KEYS[1], 0, ARGV[1]); " +
                        "redis.call('ZADD', KEYS[1], ARGV[2], ARGV[2]); " +
                        "redis.call('PEXPIRE', KEYS[1], ARGV[3]); " +
                        "local count = redis.call('ZCARD', KEYS[1]); " +
                        "return count;", Long.class);

        // 固定窗口：INCR + PEXPIRE 原子性（只有第一次设置 TTL）
        FIXED_WINDOW_SCRIPT = new DefaultRedisScript<>(
                "local current = redis.call('INCR', KEYS[1]); " +
                        "if current == 1 then redis.call('PEXPIRE', KEYS[1], ARGV[1]); end; " +
                        "return current;", Long.class);

        // 令牌桶：补充令牌 + 消耗令牌，保证原子性，并设置 TTL
        TOKEN_BUCKET_SCRIPT = new DefaultRedisScript<>(
                "local capacity = tonumber(ARGV[1]); " +
                        "local refillTokens = tonumber(ARGV[2]); " +
                        "local refillPeriod = tonumber(ARGV[3]); " +
                        "local now = tonumber(ARGV[4]); " +
                        "local ttl = tonumber(ARGV[5]); " +
                        "local tokens = tonumber(redis.call('GET', KEYS[1]) or capacity); " +
                        "local lastRefill = tonumber(redis.call('GET', KEYS[2]) or now); " +
                        "local elapsed = now - lastRefill; " +
                        "local refillCount = math.floor(elapsed / refillPeriod) * refillTokens; " +
                        "tokens = math.min(capacity, tokens + refillCount); " +
                        "local allowed = 0; " +
                        "if tokens > 0 then tokens = tokens - 1; allowed = 1; end; " +
                        "redis.call('SET', KEYS[1], tokens); " +
                        "redis.call('SET', KEYS[2], now); " +
                        "redis.call('PEXPIRE', KEYS[1], ttl); " +
                        "redis.call('PEXPIRE', KEYS[2], ttl); " +
                        "return {allowed, tokens};", List.class);

        // 漏桶：毫秒级时间戳，按速率漏水，并设置 TTL
        LEAKY_BUCKET_SCRIPT = new DefaultRedisScript<>(
                "local now = tonumber(ARGV[1]); " +
                        "local leakRate = tonumber(ARGV[2]) / 1000; " + // 每毫秒漏水速率
                        "local capacity = tonumber(ARGV[3]); " +
                        "local ttl = tonumber(ARGV[4]); " +
                        "local last = redis.call('HGET', KEYS[1], 'last'); " +
                        "local count = redis.call('HGET', KEYS[1], 'count'); " +
                        "if not last then last = now end; " +
                        "if not count then count = 0 end; " +
                        "local elapsed = now - tonumber(last); " +
                        "local leaked = math.floor(elapsed * leakRate); " +
                        "count = math.max(0, tonumber(count) - leaked); " +
                        "local allowed = 0; " +
                        "if count < capacity then count = count + 1; allowed = 1; end; " +
                        "redis.call('HSET', KEYS[1], 'last', now); " +
                        "redis.call('HSET', KEYS[1], 'count', count); " +
                        "redis.call('PEXPIRE', KEYS[1], ttl); " +
                        "return {allowed, count};", List.class);
    }

    public RedisRateLimitServiceImpl(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // ==================== 滑动窗口 ====================
    /**
     * 滑动窗口限流
     *
     * @param key       限流资源标识
     * @param memberId  请求方标识（如用户 ID）
     * @param maxCount  窗口内最大请求数
     * @param window    窗口大小
     * @return 限流结果
     */
    @Override
    public RateLimitResult allowRequestSlidingWindow(String key, String memberId, long maxCount, Duration window) {
        long now = System.currentTimeMillis();
        long windowStart = now - window.toMillis();
        String redisKey = "rate:sliding:" + key + ":" + memberId;

        try {
            Long count = redisTemplate.execute(SLIDING_WINDOW_SCRIPT,
                    Collections.singletonList(redisKey),
                    String.valueOf(windowStart),
                    String.valueOf(now),
                    String.valueOf(window.toMillis()));

            boolean allowed = count != null && count <= maxCount;
            Map<String, Object> metrics = new HashMap<>();
            metrics.put("remaining", Math.max(0, maxCount - (count == null ? 0 : count)));
            metrics.put("resetAfterMillis", window.toMillis());
            return new RateLimitResult(allowed, metrics);
        } catch (Exception e) {
            log.error("Redis 执行滑动窗口限流脚本异常 key={}", key, e);
            return new RateLimitResult(false, Map.of("error", "redis-execute-failed"));
        }
    }

    // ==================== 固定窗口 ====================
    /**
     * 固定窗口限流
     *
     * @param key      限流资源标识
     * @param maxCount 窗口内最大请求数
     * @param window   窗口大小
     * @return 限流结果
     */
    @Override
    public RateLimitResult allowRequestFixedWindow(String key, long maxCount, Duration window) {
        String redisKey = "rate:fixed:" + key + ":" + (System.currentTimeMillis() / window.toMillis());
        try {
            Long count = redisTemplate.execute(FIXED_WINDOW_SCRIPT,
                    Collections.singletonList(redisKey),
                    String.valueOf(window.toMillis()));

            boolean allowed = count != null && count <= maxCount;
            Map<String, Object> metrics = new HashMap<>();
            metrics.put("remaining", Math.max(0, maxCount - (count == null ? 0 : count)));
            metrics.put("resetAfterMillis", window.toMillis());
            return new RateLimitResult(allowed, metrics);
        } catch (Exception e) {
            log.error("Redis 执行固定窗口限流脚本异常 key={}", key, e);
            return new RateLimitResult(false, Map.of("error", "redis-execute-failed"));
        }
    }

    // ==================== 令牌桶 ====================
    /**
     * 令牌桶限流
     *
     * <p>
     * 使用 Lua 脚本保证补充令牌与消耗令牌的原子性。
     * 同时为令牌数与时间戳 key 设置 TTL，避免长期未访问时占用内存。
     * </p>
     *
     * @param key          限流资源标识
     * @param capacity     桶容量（最大令牌数）
     * @param refillTokens 每次补充的令牌数
     * @param refillPeriod 补充周期
     * @return RateLimitResult，包含是否允许请求及剩余令牌数
     */
    @Override
    public RateLimitResult allowRequestTokenBucket(String key, long capacity, long refillTokens, Duration refillPeriod) {
        String tokensKey = "rate:token:" + key + ":tokens";
        String timestampKey = "rate:token:" + key + ":ts";
        long now = System.currentTimeMillis();

        // TTL 设置为桶容量对应的最大 refill 时间的几倍，避免过早过期
        long ttlMillis = (capacity / refillTokens) * refillPeriod.toMillis() * 2;

        try {
            List<Object> result = redisTemplate.execute(TOKEN_BUCKET_SCRIPT,
                    Arrays.asList(tokensKey, timestampKey),
                    String.valueOf(capacity),
                    String.valueOf(refillTokens),
                    String.valueOf(refillPeriod.toMillis()),
                    String.valueOf(now),
                    String.valueOf(ttlMillis));

            boolean allowed = result != null && Long.valueOf(result.get(0).toString()) == 1;
            long tokensLeft = result != null ? Long.valueOf(result.get(1).toString()) : 0;

            Map<String, Object> metrics = new HashMap<>();
            metrics.put("tokensLeft", tokensLeft);
            metrics.put("capacity", capacity);
            metrics.put("refillPeriodMillis", refillPeriod.toMillis());
            return new RateLimitResult(allowed, metrics);
        } catch (Exception e) {
            log.error("Redis 执行令牌桶限流脚本异常 key={}", key, e);
            return new RateLimitResult(false, Map.of("error", "redis-execute-failed"));
        }
    }

    /**
     * 漏桶限流
     *
     * <p>
     * 使用 Hash 存储 last（上次时间戳）和 count（当前请求数）。
     * 每次请求时根据漏水速率计算减少的数量，再决定是否允许请求。
     * 使用毫秒级时间戳，漏水更平滑。
     * 同时为 Hash 设置 TTL，避免长期未访问时占用内存。
     * </p>
     *
     * @param key               限流资源标识
     * @param capacity          桶容量（最大排队长度）
     * @param leakRatePerSecond 漏桶速率（每秒处理请求数）
     * @return RateLimitResult，包含是否允许请求及当前排队长度
     */
    @Override
    public RateLimitResult allowRequestLeakyBucket(String key, long capacity, long leakRatePerSecond) {
        String redisKey = "rate:leaky:" + key;
        long now = System.currentTimeMillis(); // 毫秒级时间戳

        // TTL 设置为容量 / 漏水速率的几倍，避免过早过期
        long ttlMillis = (capacity / leakRatePerSecond) * 1000 * 2;

        try {
            List<Object> result = redisTemplate.execute(LEAKY_BUCKET_SCRIPT,
                    Collections.singletonList(redisKey),
                    String.valueOf(now),
                    String.valueOf(leakRatePerSecond),
                    String.valueOf(capacity),
                    String.valueOf(ttlMillis));

            boolean allowed = result != null && Long.valueOf(result.get(0).toString()) == 1;
            long queueLength = result != null ? Long.valueOf(result.get(1).toString()) : 0;

            Map<String, Object> metrics = new HashMap<>();
            metrics.put("queueLength", queueLength);
            metrics.put("capacity", capacity);
            metrics.put("leakRatePerSecond", leakRatePerSecond);
            return new RateLimitResult(allowed, metrics);
        } catch (Exception e) {
            log.error("Redis 执行漏桶限流脚本异常 key={}", key, e);
            return new RateLimitResult(false, Map.of("error", "redis-execute-failed"));
        }
    }
}
