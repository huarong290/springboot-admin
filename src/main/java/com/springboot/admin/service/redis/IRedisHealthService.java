package com.springboot.admin.service.redis;


import java.util.Map;

/**
 * Redis 健康探测服务接口
 *
 * <p>
 * 负责 Redis 可用性检测与健康状态上报，适用于：
 * <ul>
 *   <li>单机模式：检测单个 Redis 实例是否可用</li>
 *   <li>Sentinel 模式：检测主节点和从节点状态</li>
 *   <li>Cluster 模式：检测集群中各节点状态</li>
 * </ul>
 * </p>
 *
 * <p>
 * 设计目标：
 * <ul>
 *   <li>提供统一的健康探测方法，支持熔断与降级</li>
 *   <li>返回详细的健康信息，便于监控与运维分析</li>
 *   <li>调用方可根据健康状态决定是否启用备用方案</li>
 * </ul>
 * </p>
 */
public interface IRedisHealthService {

    /**
     * Redis 健康状态对象
     *
     * <p>包含以下信息：</p>
     * <ul>
     *   <li>healthy：整体是否健康</li>
     *   <li>lastResponseTimeMillis：最近一次探测的响应时间（毫秒）</li>
     *   <li>node：当前连接的 Redis 节点信息（host:port）</li>
     *   <li>clusterNodes：集群或 Sentinel 模式下各节点的健康状态</li>
     * </ul>
     */
    class RedisHealth {
        /** 整体健康状态 */
        public final boolean healthy;
        /** 最近一次响应时间（毫秒） */
        public final long lastResponseTimeMillis;
        /** 当前节点信息（host:port） */
        public final String node;
        /** 集群或 Sentinel 模式下各节点健康状态 */
        public final Map<String, Boolean> clusterNodes;

        public RedisHealth(boolean healthy,
                           long lastResponseTimeMillis,
                           String node,
                           Map<String, Boolean> clusterNodes) {
            this.healthy = healthy;
            this.lastResponseTimeMillis = lastResponseTimeMillis;
            this.node = node;
            this.clusterNodes = clusterNodes;
        }
    }

    /**
     * 获取 Redis 健康状态
     *
     * <p>说明：</p>
     * <ul>
     *   <li>单机模式：返回当前节点状态</li>
     *   <li>Sentinel 模式：返回主节点和从节点状态</li>
     *   <li>Cluster 模式：返回集群中所有节点状态</li>
     * </ul>
     *
     * @return RedisHealth 对象，包含整体健康状态和节点信息
     */
    RedisHealth health();
}
