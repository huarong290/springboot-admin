package com.springboot.admin.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.springboot.admin.annotation.Loggable;
import com.springboot.admin.constants.log.TraceConstants;
import com.springboot.admin.utils.TraceUtil;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.InputStreamSource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {

    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher eventPublisher;

    /** 全局慢方法阈值 */
    @Value("${log.slow.threshold:1000}")
    private long globalSlowThreshold;

    /** 普通 / 安全 Writer */
    private ObjectWriter logWriter;
    private ObjectWriter safeWriter;

    /** 参数名级别脱敏 */
    private static final Set<String> SENSITIVE_KEYS =
            Set.of("password", "pwd", "secret", "token", "credential");

    @PostConstruct
    public void init() {
        this.logWriter = objectMapper.writer();

        this.safeWriter = objectMapper.writer()
                .without(SerializationFeature.FAIL_ON_SELF_REFERENCES);
    }

    @Pointcut("@annotation(com.springboot.admin.annotation.Loggable)")
    public void loggableMethods() {}

    @Around("loggableMethods()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Loggable loggable = signature.getMethod().getAnnotation(Loggable.class);

        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();

        /* ---------- TraceId ---------- */
        boolean traceIdGenerated = false;
        String traceId = MDC.get(TraceConstants.TRACE_ID);
        if (StringUtils.isBlank(traceId)) {
            traceId = Optional.ofNullable(TraceUtil.getTraceId())
                    .orElse(UUID.randomUUID().toString().replace("-", ""));
            MDC.put(TraceConstants.TRACE_ID, traceId);
            traceIdGenerated = true;
        }

        boolean needAudit = loggable.saveLog();
        boolean needReqLog = loggable.logRequest();
        boolean needRespLog = loggable.logResponse();

        long start = System.currentTimeMillis();

        Map<String, Object> paramMap = Collections.emptyMap();
        String requestJson = null;

        if (needReqLog || needAudit) {
            paramMap = parseParams(signature, joinPoint.getArgs());
            requestJson = toJson(paramMap, loggable.safeSerialize());
        }

        logByLevel(loggable.level(), "请求: [{}].[{}]() | 参数: [{}]", className, methodName, requestJson);

        Object result = null;
        Throwable exception = null;

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable ex) {
            exception = ex;
            log.error("❌ [{}] {}.{}() 异常", traceId, className, methodName, ex);
            throw ex;
        } finally {
            long costMs = System.currentTimeMillis() - start;

            String responseJson = null;
            if ((needRespLog || needAudit) && exception == null) {
                responseJson = formatResult(result, loggable.safeSerialize());
            } else if (exception != null) {
                responseJson = "Exception: " + exception.getMessage();
            }

            if (needRespLog) {
                logByLevel(loggable.level(), "📤 [{}] 响应: {} | 耗时: {}ms", traceId, responseJson, costMs);
            }

            long threshold = loggable.slowThresholdMs() > 0 ? loggable.slowThresholdMs() : globalSlowThreshold;

            if (costMs >= threshold) {
                log.warn("⏱ [{}] 慢方法: {}.{}() | {}ms", traceId, className, methodName, costMs);
            }

            if (needAudit) {
                publishAuditLog(traceId, className, methodName, requestJson, responseJson, exception, costMs);
            }

            if (traceIdGenerated) {
                MDC.remove(TraceConstants.TRACE_ID);
            }
        }
    }

    /* ================= 参数解析 ================= */

    private Map<String, Object> parseParams(MethodSignature signature, Object[] args) {
        if (args == null || args.length == 0) {
            return Collections.emptyMap();
        }

        String[] paramNames = signature.getParameterNames();
        if (paramNames == null) {
            paramNames = new String[args.length];
            for (int i = 0; i < args.length; i++) {
                paramNames[i] = "arg" + i;
            }
        }

        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            String name = paramNames[i];

            if (arg instanceof ServletRequest
                    || arg instanceof ServletResponse) {
                continue;
            }

            if (arg instanceof MultipartFile file) {
                map.put(name, String.format("[File: %s (%d bytes)]", file.getOriginalFilename(), file.getSize()));
            } else if (arg instanceof MultipartFile[] files) {
                map.put(name, "[File List: " + files.length + "]");
            } else if (arg instanceof InputStreamSource) {
                map.put(name, "[InputStream]");
            }
        }
        return map;
    }

    private Object maskSensitive(String name, Object value) {
        if (value == null || name == null) return null;
        String lower = name.toLowerCase();
        if (SENSITIVE_KEYS.contains(lower)) return "***";
        return value;
    }

    /* ================= JSON ================= */

    private String toJson(Object obj, boolean safe) {
        if (obj == null) return "null";
        try {
            return (safe ? safeWriter : logWriter)
                    .writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return "[SerializeFailed]";
        }
    }

    private String formatResult(Object result, boolean safe) {
        if (result == null) return "null";
        if (result instanceof InputStreamSource) return "[Stream]";

        String json = toJson(result, safe);
        int maxLen = TraceConstants.DEFAULT_MAX_LOG_LENGTH;
        if (json.length() > maxLen) {
            return json.substring(0, maxLen) + "...(truncated)";
        }
        return json;
    }

    /* ================= 审计 ================= */

    private void publishAuditLog(String traceId,
                                 String className,
                                 String methodName,
                                 String requestJson,
                                 String responseJson,
                                 Throwable ex,
                                 long costMs) {

        // AuditLogContext ctx = new AuditLogContext(
        //     traceId,
        //     className + "." + methodName,
        //     requestJson,
        //     responseJson,
        //     ex == null ? null : ex.getMessage(),
        //     costMs
        // );
        // eventPublisher.publishEvent(new AuditLogEvent(ctx));
    }

    /* ================= 日志级别 ================= */

    private void logByLevel(Loggable.Level level, String pattern, Object... args) {
        if (level == Loggable.Level.DEBUG) {
            if (log.isDebugEnabled()) {
                log.debug(pattern, args);
            }
        } else {
            log.info(pattern, args);
        }
    }
}
