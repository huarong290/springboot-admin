package com.springboot.admin.service.redis.biz.impl;

import com.springboot.admin.service.redis.biz.IRedisHealthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisClusterNode;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisSentinelConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RedisHealthServiceImpl implements IRedisHealthService {

    private static final Logger log = LoggerFactory.getLogger(RedisHealthServiceImpl.class);

    private final RedisTemplate<String, String> redisTemplate;

    public RedisHealthServiceImpl(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public RedisHealth health() {
        long start = System.currentTimeMillis();
        boolean healthy = false;
        String mode = "Unknown";
        Map<String, Boolean> nodeStatusMap = new HashMap<>();

        // 使用 try-with-resources 自动管理连接释放
        try (RedisConnection connection = redisTemplate.getConnectionFactory().getConnection()) {

            // 1. Cluster 模式处理
            if (connection instanceof RedisClusterConnection) {
                mode = "Cluster";
                RedisClusterConnection clusterConn = (RedisClusterConnection) connection;
                Iterable<RedisClusterNode> nodes = clusterConn.clusterGetNodes();

                for (RedisClusterNode node : nodes) {
                    String nodeKey = node.getHost() + ":" + node.getPort();
                    try {
                        // 使用 ping(node) 探测特定节点
                        String pong = clusterConn.ping(node);
                        boolean isAlive = "PONG".equalsIgnoreCase(pong);
                        nodeStatusMap.put(nodeKey, isAlive);
                        log.info("Cluster 节点 [{}] 探测结果: {}", nodeKey, isAlive);
                    } catch (Exception e) {
                        nodeStatusMap.put(nodeKey, false);
                        log.warn("Redis Cluster 节点 [{}] 探测异常", nodeKey, e);
                    }
                }
                healthy = !nodeStatusMap.isEmpty() && !nodeStatusMap.containsValue(false);

                // 2. Sentinel 模式处理
            } else if (connection instanceof RedisSentinelConnection) {
                mode = "Sentinel";
                RedisSentinelConnection sentinelConn = (RedisSentinelConnection) connection;
                // 探测 Sentinel 监控的所有主节点
                sentinelConn.masters().forEach(master -> {
                    String masterKey = "Master:" + master.getName();
                    try {
                        String pong = connection.ping(); // 探测当前活跃连接
                        nodeStatusMap.put(masterKey, "PONG".equalsIgnoreCase(pong));
                        log.info("Sentinel 主节点 [{}] 探测结果: true", masterKey);
                    } catch (Exception e) {
                        nodeStatusMap.put(masterKey, false);
                    }
                });
                healthy = !nodeStatusMap.containsValue(false);

                // 3. 单机模式处理
            } else {
                mode = "Standalone";
                String pong = connection.ping();
                healthy = "PONG".equalsIgnoreCase(pong);
                nodeStatusMap.put("default", healthy);
                log.info("Standalone 探测结果: {}", healthy);
            }

        } catch (Exception e) {
            log.error("Redis 健康探测发生严重异常", e);
            healthy = false;
        }

        long responseTime = System.currentTimeMillis() - start;
        return new RedisHealth(healthy, responseTime, mode, nodeStatusMap);
    }
}