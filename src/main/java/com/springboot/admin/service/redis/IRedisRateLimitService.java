package com.springboot.admin.service.redis;

import java.time.Duration;
import java.util.Map;

/**
 * Redis 限流服务接口
 *
 * <p>
 * 负责限流算法，适用于接口防刷、API 网关等场景。
 * </p>
 *
 * <p>
 * 支持算法：
 * <ul>
 *   <li>滑动窗口</li>
 *   <li>固定窗口</li>
 *   <li>令牌桶</li>
 *   <li>漏桶</li>
 * </ul>
 * </p>
 */
public interface IRedisRateLimitService {

    /**
     * 限流结果对象
     *
     * <p>包含是否允许和动态指标（remaining、resetAfterMillis、queueLength、tokensLeft 等）。</p>
     */
    class RateLimitResult {
        public final boolean allowed;
        public final Map<String, Object> metrics;

        public RateLimitResult(boolean allowed, Map<String, Object> metrics) {
            this.allowed = allowed;
            this.metrics = metrics;
        }
    }

    RateLimitResult allowRequestSlidingWindow(String key, String memberId, long maxCount, Duration window);

    RateLimitResult allowRequestFixedWindow(String key, long maxCount, Duration window);

    RateLimitResult allowRequestTokenBucket(String key, long capacity, long refillTokens, Duration refillPeriod);

    RateLimitResult allowRequestLeakyBucket(String key, long capacity, long leakRatePerSecond);
}
