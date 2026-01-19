package com.springboot.admin.constants.log;

/**
 * 链路追踪 / 日志相关常量
 */
public final class TraceConstants {

    private TraceConstants() {}

    /**
     * TraceId 在 MDC 中的 key
     */
    public static final String TRACE_ID = "traceId";

    /**
     * TraceId 请求头
     */
    public static final String TRACE_HEADER = "X-Trace-Id";
}
