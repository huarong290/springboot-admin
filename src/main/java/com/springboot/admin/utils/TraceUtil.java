package com.springboot.admin.utils;

import org.slf4j.MDC;

import java.util.UUID;

/**
 * TraceId 工具类
 *
 * 基于 MDC（Mapped Diagnostic Context）
 * 能让日志自动携带 traceId
 */
public class TraceUtil {

    /**
     * 日志上下文中的 key
     */
    public static final String TRACE_ID = "traceId";

    /**
     * 生成 traceId
     * 使用 UUID 去掉横线，适合日志和链路传递
     */
    public static String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 设置 traceId 到 MDC
     */
    public static void setTraceId(String traceId) {
        MDC.put(TRACE_ID, traceId);
    }

    /**
     * 获取当前 traceId
     */
    public static String getTraceId() {
        return MDC.get(TRACE_ID);
    }

    /**
     * 清理（非常重要，防止线程复用污染）
     */
    public static void clear() {
        MDC.clear();
    }
}
