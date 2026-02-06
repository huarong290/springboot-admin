package com.springboot.admin.service.redis.biz;

import java.util.Map;

/**
 * Redis 健康探测服务接口
 *
 * <p>
 * 用于检测 Redis 的可用性与健康状态，并返回详细的健康信息。
 * 适用于以下场景：
 * </p>
 * <ul>
 *   <li><b>单机模式</b>：检测单个 Redis 实例是否可用</li>
 *   <li><b>Sentinel 模式</b>：检测主节点与从节点的状态</li>
 *   <li><b>Cluster 模式</b>：检测集群中所有节点的状态</li>
 * </ul>
 *
 * <p>
 * 设计目标：
 * </p>
 * <ul>
 *   <li>提供统一的健康探测方法，支持熔断与降级</li>
 *   <li>返回详细的健康信息，便于监控与运维分析</li>
 *   <li>调用方可根据健康状态决定是否启用备用方案（如切换到本地缓存或备用 Redis 集群）</li>
 * </ul>
 */
public interface IRedisHealthService {

    /**
     * Redis 健康状态对象
     *
     * <p>
     * 封装 Redis 健康探测的结果，包含整体状态与节点级别的详细信息。
     * </p>
     *
     * <p>字段说明：</p>
     * <ul>
     *   <li><b>healthy</b>：整体健康状态，true=健康，false=不可用</li>
     *   <li><b>lastResponseTimeMillis</b>：最近一次探测的响应时间（毫秒），用于性能监控</li>
     *   <li><b>node</b>：当前连接的 Redis 节点信息（格式：host:port）</li>
     *   <li><b>clusterNodes</b>：集群或 Sentinel 模式下各节点的健康状态映射，Key=节点地址，Value=true/false</li>
     * </ul>
     *
     * <p>
     * 注意事项：
     * </p>
     * <ul>
     *   <li>在单机模式下，clusterNodes 可能为 null 或空 Map</li>
     *   <li>在 Sentinel/Cluster 模式下，clusterNodes 会包含所有节点的状态</li>
     *   <li>调用方可结合 healthy 与 clusterNodes 判断是否需要降级或切换</li>
     * </ul>
     */
    class RedisHealth {
        /** 整体健康状态（true=健康，false=不可用） */
        public final boolean healthy;
        /** 最近一次响应时间（毫秒） */
        public final long lastResponseTimeMillis;
        /** 当前节点信息（格式：host:port） */
        public final String node;
        /** 集群或 Sentinel 模式下各节点健康状态映射 */
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
     * <p>
     * 执行一次健康探测，返回 Redis 当前的可用性与性能信息。
     * </p>
     *
     * <p>返回值说明：</p>
     * <ul>
     *   <li><b>单机模式</b>：返回当前节点的健康状态</li>
     *   <li><b>Sentinel 模式</b>：返回主节点和从节点的健康状态</li>
     *   <li><b>Cluster 模式</b>：返回集群中所有节点的健康状态</li>
     * </ul>
     *
     * <p>
     * 调用方可根据返回的 {@link RedisHealth} 对象：
     * </p>
     * <ul>
     *   <li>判断整体是否健康（healthy 字段）</li>
     *   <li>分析响应时间（lastResponseTimeMillis）</li>
     *   <li>查看具体节点的健康情况（clusterNodes）</li>
     *   <li>决定是否启用熔断、降级或切换到备用方案</li>
     * </ul>
     *
     * @return RedisHealth 对象，包含整体健康状态和节点信息
     */
    RedisHealth health();
}
