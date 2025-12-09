package com.springboot.admin.aop;

import com.alibaba.fastjson2.JSON;
import com.springboot.admin.annotation.Logable;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Aspect
@Component
public class LogAspect {

    // 可以通过配置文件或环境变量控制调试模式
    private static final boolean DEBUG_MODE = Boolean.parseBoolean(
            System.getProperty("log.debugMode", "false")
    );

    @Pointcut("@annotation(com.springboot.admin.annotation.Logable)")
    public void logableMethods() {}

    @Around("logableMethods()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Logable loggable = method.getAnnotation(Logable.class);

        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();

        Object[] args = joinPoint.getArgs();
        String[] paramNames = signature.getParameterNames();

        // 入参日志
        if (loggable.logRequest()) {
            Map<String, Object> filteredParams = new LinkedHashMap<>();
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (isSkippableType(arg)) continue;

                if (arg instanceof Mono || arg instanceof Flux) {
                    filteredParams.put(paramNames[i], arg.getClass().getSimpleName());
                } else {
                    filteredParams.put(paramNames[i], arg);
                }
            }
            log.info("➡️ [{}] 调用方法：{}.{}()", UUID.randomUUID(), className, methodName);
            log.info("📥 入参：{}", JSON.toJSONString(filteredParams));
        }

        Object result;
        try {
            result = joinPoint.proceed();

            // 出参日志
            if (loggable.logResponse() && !isSkippableType(result)) {
                if (result instanceof Mono) {
                    return ((Mono<?>) result)
                            .doOnNext(r -> log.info("📤 出参数据：{}", JSON.toJSONString(r)))
                            .doOnError(e -> log.error("❌ 异常：{}.{}() 出参错误：{}", className, methodName, e.getMessage(), e));
                } else if (result instanceof Flux) {
                    if (DEBUG_MODE) {
                        // 调试模式：收集成 List 打印完整数据
                        return ((Flux<?>) result)
                                .collectList()
                                .doOnNext(list -> log.info("📤 出参数据：{}", JSON.toJSONString(list)))
                                .doOnError(e -> log.error("❌ 异常：{}.{}() 出参错误：{}", className, methodName, e.getMessage(), e));
                    } else {
                        // 生产模式：只打印类型信息
                        log.info("📤 出参：Flux<{}>", result.getClass().getSimpleName());
                        return result;
                    }
                } else {
                    // 非响应式对象
                    log.info("📤 出参：{}", JSON.toJSONString(result));
                }
            }
        } catch (Throwable ex) {
            log.error("❌ 异常：{}.{}() 抛出异常：{}", className, methodName, ex.getMessage(), ex);
            throw ex;
        }

        return result;
    }

    private boolean isSkippableType(Object obj) {
        return obj instanceof MultipartFile ||
                obj instanceof MultipartFile[];
    }

    private boolean isDebugMode() {
        return DEBUG_MODE;
    }
}
