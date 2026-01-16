package com.springboot.admin.aop;

import com.springboot.admin.annotation.Logable;
import com.springboot.admin.utils.TraceUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * 日志切面
 *
 * 功能：
 * 1️⃣ 打印方法入参、出参
 * 2️⃣ 处理文件上传参数，只记录文件名、大小、类型
 * 3️⃣ 捕获异常并打印堆栈
 * 4️⃣ 记录方法耗时
 * 5️⃣ 生成链路 traceId
 * 6️⃣ 可扩展记录审计信息（用户、IP、URL）
 */
@Slf4j
@Aspect
@Component
public class LogAspect {

    /**
     * 切点：标注 @Logable 注解的方法
     */
    @Pointcut("@annotation(com.springboot.admin.annotation.Logable)")
    public void logableMethods() {}

    @Around("logableMethods()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Logable logable = method.getAnnotation(Logable.class);

        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();
        // 从 MDC 中获取 traceId（和 Filter 保持一致）
        String traceId = TraceUtil.getTraceId();
        if (traceId == null) {
            traceId = UUID.randomUUID().toString(); // 防护：非HTTP请求也能生成
        }
        Object[] args = joinPoint.getArgs();
        String[] paramNames = signature.getParameterNames();

        Map<String, Object> filteredParams = new LinkedHashMap<>();
        if (logable.logRequest()) {
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (arg == null || isSkippableType(arg)) continue;
                filteredParams.put(paramNames[i], handleFileArg(arg));
            }
        }

        Instant start = Instant.now(); // 记录开始时间
        Object result;
        try {
            if (logable.logRequest()) {
                log.info("➡️ [{}] 调用方法：{}.{}()", traceId, className, methodName);
                log.info("📥 入参：{}", filteredParams);
            }

            result = joinPoint.proceed(); // 执行方法

            // 出参日志
            if (logable.logResponse() && !isSkippableType(result)) {
                log.info("📤 [{}] 方法返回：{}", traceId, result);
            }

            return result;
        } catch (Throwable ex) {
            log.error("❌ [{}] 方法异常：{}.{}() 异常信息：{}", traceId, className, methodName, ex.getMessage(), ex);
            throw ex;
        } finally {
            // 方法耗时
            Instant end = Instant.now();
            long duration = Duration.between(start, end).toMillis();
            log.info("⏱ [{}] 方法耗时：{} ms", traceId, duration);

            // 可扩展：记录用户、IP、URL到审计日志数据库
            // saveAuditLog(traceId, className, methodName, filteredParams, result, duration);
        }
    }

    /**
     * 判断对象是否需要跳过日志
     * - 文件上传
     * - 数组形式的文件
     */
    private boolean isSkippableType(Object obj) {
        return obj instanceof HttpServletRequest ||
                obj instanceof HttpServletResponse ||
                obj instanceof MultipartFile ||
                obj instanceof MultipartFile[] ||
                obj instanceof Collection<?> && ((Collection<?>) obj).stream().anyMatch(e -> e instanceof MultipartFile);
    }

    /**
     * 处理文件参数，只记录文件名、大小、类型
     */
    private Object handleFileArg(Object arg) {
        if (arg instanceof MultipartFile file) {
            return Map.of(
                    "filename", file.getOriginalFilename(),
                    "size", file.getSize(),
                    "contentType", file.getContentType()
            );
        }

        if (arg instanceof MultipartFile[] files) {
            List<Map<String, Object>> list = new ArrayList<>();
            for (MultipartFile f : files) {
                list.add(Map.of(
                        "filename", f.getOriginalFilename(),
                        "size", f.getSize(),
                        "contentType", f.getContentType()
                ));
            }
            return list;
        }

        if (arg instanceof Collection<?> coll) {
            List<Object> list = new ArrayList<>();
            for (Object item : coll) {
                if (item instanceof MultipartFile fileItem) {
                    list.add(Map.of(
                            "filename", fileItem.getOriginalFilename(),
                            "size", fileItem.getSize(),
                            "contentType", fileItem.getContentType()
                    ));
                } else {
                    list.add(item);
                }
            }
            return list;
        }

        return arg;
    }

    /**
     * 可扩展方法：写审计日志到数据库
     *
     * @param traceId      唯一调用ID
     * @param className    类名
     * @param methodName   方法名
     * @param params       入参
     * @param result       返回值
     * @param durationMs   方法耗时
     */
    private void saveAuditLog(String traceId, String className, String methodName,
                              Map<String, Object> params, Object result, long durationMs) {
        // TODO: 实现审计日志存储，可写到数据库表 sys_log 或 sys_audit_log
    }
}
