package com.springboot.admin.aop;

import com.springboot.admin.common.ApiResult;
import com.springboot.admin.common.ApiResultCode;
import com.springboot.admin.exception.BaseException;
import com.springboot.admin.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import reactor.core.publisher.Mono;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Mono<ApiResult<Void>> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return Mono.just(ApiResult.failResult(e.getCode(), e.getMessage()));
    }

    /**
     * 处理基础异常
     */
    @ExceptionHandler(BaseException.class)
    public Mono<ApiResult<Void>> handleBaseException(BaseException e) {
        log.error("基础异常: code={}, message={}", e.getCode(), e.getMessage());
        return Mono.just(ApiResult.failResult(e.getCode(), e.getMessage()));
    }
    /**
     * ⚠️ 静态资源找不到时，直接返回 404，不包装成 JSON
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public Mono<ResponseEntity<Void>> handleNoResourceFound(NoResourceFoundException e) {
        log.warn("静态资源未找到: {}", e.getMessage());
        return Mono.just(ResponseEntity.notFound().build());
    }
    /**
     * 处理其他未捕获异常
     */
    @ExceptionHandler(Exception.class)
    public Mono<ApiResult<Void>> handleException(Exception e) {
        log.error("系统异常: {}", e.getMessage(), e);
        return Mono.just(ApiResult.failResult(ApiResultCode.FAILED.getCode(), "系统错误，请联系管理员"));
    }
}
