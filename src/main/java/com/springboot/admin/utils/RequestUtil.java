package com.springboot.admin.utils;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

public class RequestUtil {

    /**
     * 从 ServerWebExchange 获取客户端真实 IP
     * 支持 X-Forwarded-For 等代理头
     */
    public static String getClientIp(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();

        // 1. 先尝试获取 Nginx / 代理转发的真实 IP
        String ip = request.getHeaders().getFirst("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            // X-Forwarded-For 可能包含多个 IP，取第一个
            if (ip.contains(",")) {
                ip = ip.split(",")[0].trim();
            }
            return ip;
        }

        // 2. 再尝试获取 Proxy-Client-IP
        ip = request.getHeaders().getFirst("Proxy-Client-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        // 3. 最后取 request 的 remoteAddress
        if (request.getRemoteAddress() != null) {
            ip = request.getRemoteAddress().getAddress().getHostAddress();
        }

        return ip != null ? ip : "unknown";
    }
}

