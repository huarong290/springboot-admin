package com.springboot.admin.service.redis.biz;

import java.time.Duration;
import java.util.Map;

/**
 * Redis 限流服务接口
 *
 * <p>
 * 提供基于 Redis 的分布式限流算法，适用于接口防刷、API 网关、微服务调用保护等场景。
 * </p>
 *
 * <p>
 * 支持的限流算法：
 * </p>
 * <ul>
 *   <li><b>滑动窗口</b>：精确统计窗口内请求数，避免固定窗口的临界问题</li>
 *   <li><b>固定窗口</b>：简单高效，适合低精度场景</li>
 *   <li><b>令牌桶</b>：支持突发流量，按速率发放令牌</li>
 *   <li><b>漏桶</b>：平滑流量，按固定速率处理请求</li>
 * </ul>
 *
 * <p>
 * 设计目标：
 * </p>
 * <ul>
 *   <li>提供统一的限流接口，支持多种算法</li>
 *   <li>返回详细的限流结果，便于监控与动态调整</li>
 *   <li>支持分布式场景下的高并发限流</li>
 * </ul>
 */
public interface IRedisRateLimitService {

    /**
     * 限流结果对象
     *
     * <p>
     * 封装限流判断的结果，包含是否允许请求以及动态指标。
     * </p>
     *
     * <p>字段说明：</p>
     * <ul>
     *   <li><b>allowed</b>：是否允许请求，true=允许，false=拒绝</li>
     *   <li><b>metrics</b>：动态指标 Map，例如：
     *     <ul>
     *       <li>remaining：剩余可用请求数或令牌数</li>
     *       <li>resetAfterMillis：窗口重置剩余时间（毫秒）</li>
     *       <li>queueLength：当前排队长度（漏桶场景）</li>
     *       <li>tokensLeft：剩余令牌数（令牌桶场景）</li>
     *     </ul>
     *   </li>
     * </ul>
     *
     * <p>调用方可根据 metrics 做监控或动态限流策略调整。</p>
     */
    class RateLimitResult {
        /** 是否允许请求 */
        public final boolean allowed;
        /** 动态指标（remaining、resetAfterMillis、queueLength、tokensLeft 等） */
        public final Map<String, Object> metrics;

        public RateLimitResult(boolean allowed, Map<String, Object> metrics) {
            this.allowed = allowed;
            this.metrics = metrics;
        }
    }

    /**
     * 滑动窗口限流
     *
     * @param key       Redis Key，用于标识限流资源（如 API 路径）
     * @param memberId  请求方标识（如用户 ID、IP），用于区分不同请求来源
     * @param maxCount  窗口内允许的最大请求数
     * @param window    窗口大小（时间范围）
     * @return RateLimitResult，包含是否允许请求及相关指标
     *
     * <p>特点：精确统计窗口内请求数，避免固定窗口的临界问题。</p>
     */
    RateLimitResult allowRequestSlidingWindow(String key, String memberId, long maxCount, Duration window);

    /**
     * 固定窗口限流
     *
     * @param key      Redis Key，用于标识限流资源
     * @param maxCount 窗口内允许的最大请求数
     * @param window   窗口大小（时间范围）
     * @return RateLimitResult，包含是否允许请求及相关指标
     *
     * <p>特点：实现简单，性能高，但可能存在临界点突发问题。</p>
     */
    RateLimitResult allowRequestFixedWindow(String key, long maxCount, Duration window);

    /**
     * 令牌桶限流
     *
     * @param key          Redis Key，用于标识限流资源
     * @param capacity     桶容量（最大令牌数）
     * @param refillTokens 每次补充的令牌数
     * @param refillPeriod 补充周期
     * @return RateLimitResult，包含是否允许请求及相关指标
     *
     * <p>特点：支持突发流量，按速率发放令牌，允许短时间内的高并发。</p>
     */
    RateLimitResult allowRequestTokenBucket(String key, long capacity, long refillTokens, Duration refillPeriod);

    /**
     * 漏桶限流
     *
     * @param key              Redis Key，用于标识限流资源
     * @param capacity         桶容量（最大排队长度）
     * @param leakRatePerSecond 漏桶速率（每秒处理请求数）
     * @return RateLimitResult，包含是否允许请求及相关指标
     *
     * <p>特点：平滑流量，按固定速率处理请求，避免突发流量冲击。</p>
     */
    RateLimitResult allowRequestLeakyBucket(String key, long capacity, long leakRatePerSecond);
}
