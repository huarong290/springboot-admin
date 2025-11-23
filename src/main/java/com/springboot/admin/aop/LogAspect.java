package com.springboot.admin.aop;

import com.springboot.admin.annotation.Logable;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.aspectj.lang.reflect.MethodSignature;
@Slf4j
@Aspect
@Component
public class LogAspect {

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

        if (loggable.logRequest()) {
            Map<String, Object> filteredParams = new LinkedHashMap<>();
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (isSkippableType(arg)) continue;
                filteredParams.put(paramNames[i], arg);
            }
            log.info("➡️ [{}] 调用方法：{}.{}()", UUID.randomUUID(), className, methodName);
            log.info("📥 入参：{}", filteredParams);
        }

        Object result;
        try {
            result = joinPoint.proceed();
            if (loggable.logResponse() && !isSkippableType(result)) {
                log.info("📤 出参：{}", result);
            }
        } catch (Throwable ex) {
            log.error("❌ 异常：{}.{}() 抛出异常：{}", className, methodName, ex.getMessage(), ex);
            throw ex;
        }

        return result;
    }

    private boolean isSkippableType(Object obj) {
        return obj instanceof MultipartFile ||
                obj instanceof MultipartFile[] ;

    }
}
