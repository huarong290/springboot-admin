package com.springboot.admin.filter;


import com.springboot.admin.utils.TraceUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.UUID;

import static com.springboot.admin.constants.CommonConstants.TRACE_HEADER;

/**
 * TraceId 过滤器
 *
 * 执行时机：请求刚进入系统
 *
 * 作用：
 * 1. 从请求头读取 traceId（微服务调用）
 * 2. 如果没有则生成新的
 * 3. 放入 MDC，供日志使用
 */
@Slf4j
public class TraceIdFilter implements Filter {



    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        try {
            HttpServletRequest httpRequest = (HttpServletRequest) request;

            // 优先使用上游系统传递的 traceId
            String traceId = httpRequest.getHeader(TRACE_HEADER);
            if (traceId == null || traceId.isEmpty()) {
                // 如果没有，生成新的 UUID
                traceId = UUID.randomUUID().toString();
            }
            TraceUtil.setTraceId(traceId);
            // 可选：打印请求开始信息
            log.info("[{}] 请求开始: {} {}", traceId, httpRequest.getMethod(), httpRequest.getRequestURI());

            chain.doFilter(request, response);
        } finally {
            // 必须清理，避免线程复用导致 traceId 串号
            TraceUtil.clear();
        }
    }
}
