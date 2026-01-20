package com.springboot.admin.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 客户端 IP 工具类（生产级）
 *
 * 支持：
 * 1. 多级代理 X-Forwarded-For
 * 2. IPv6 / IPv4 自动识别
 * 3. IPv6 localhost 归一化为 127.0.0.1
 * 4. ::ffff:IPv4 转换
 */
@Component
public class IpUtil {

    private static final String UNKNOWN = "unknown";

    /**
     * 获取客户端真实 IP
     */
    public String getClientIp(HttpServletRequest request) {
        String ip = getIpFromHeaders(request);

        if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return normalizeIp(ip);
    }

    /**
     * 从常见代理头中解析 IP
     */
    private String getIpFromHeaders(HttpServletRequest request) {
        String[] headers = {
                "X-Forwarded-For",
                "X-Real-IP",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_CLIENT_IP",
                "HTTP_X_FORWARDED_FOR"
        };

        for (String header : headers) {
            String value = request.getHeader(header);
            if (value != null && value.length() > 0 && !UNKNOWN.equalsIgnoreCase(value)) {
                // 可能多个 IP，取第一个
                for (String ip : value.split(",")) {
                    ip = ip.trim();
                    if (!UNKNOWN.equalsIgnoreCase(ip)) {
                        return ip;
                    }
                }
            }
        }
        return null;
    }

    /**
     * IP 归一化处理
     */
    private String normalizeIp(String ip) {
        // IPv6 localhost
        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
            return "127.0.0.1";
        }

        // IPv6 mapped IPv4  ::ffff:192.168.1.10
        if (ip.startsWith("::ffff:")) {
            return ip.substring(7);
        }

        // 其它 IPv6，尝试解析
        try {
            InetAddress inetAddress = InetAddress.getByName(ip);
            return inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            return ip;
        }
    }
}
