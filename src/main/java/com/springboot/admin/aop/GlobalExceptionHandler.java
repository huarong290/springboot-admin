package com.springboot.admin.aop;

import com.springboot.admin.common.ApiResult;
import com.springboot.admin.common.ApiResultCode;
import com.springboot.admin.exception.BaseException;
import com.springboot.admin.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import reactor.core.publisher.Mono;

import java.lang.reflect.InaccessibleObjectException;

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
     * 参数校验异常处理
     */
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ApiResult<Void>> handleValidationException(WebExchangeBindException e) {
        String errorMsg = e.getAllErrors().stream()
                .map(objectError -> objectError.getDefaultMessage())
                .findFirst()
                .orElse(ApiResultCode.BAD_REQUEST.getMessage());
        log.warn("参数校验异常: {}", errorMsg);
        return Mono.just(ApiResult.failResult(ApiResultCode.BAD_REQUEST.getCode(), errorMsg));
    }

    /**
     * 静态资源找不到时，直接返回 404，不包装成 JSON
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public Mono<ResponseEntity<Void>> handleNoResourceFound(NoResourceFoundException e) {
        log.warn("静态资源未找到: {}", e.getMessage());
        return Mono.just(ResponseEntity.notFound().build());
    }
    /**
     * 处理 JDK 模块化反射访问异常
     */
    @ExceptionHandler(InaccessibleObjectException.class)
    public Mono<ApiResult<Void>> handleInaccessibleObjectException(InaccessibleObjectException e) {
        log.error("反射访问异常: {}", e.getMessage(), e);
        return Mono.just(ApiResult.failResult(ApiResultCode.INTERNAL_SERVER_ERROR.getCode(),
                "系统内部错误：反射访问受限，请检查 JVM 启动参数 --add-opens 配置"));
    }
    /**
     * 处理唯一约束冲突异常
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public Mono<ApiResult<Void>> handleDuplicateKeyException(DuplicateKeyException e) {
        log.warn("数据库唯一约束冲突: {}", e.getMessage());
        return Mono.just(ApiResult.failResult(ApiResultCode.CONFLICT.getCode(),
                "数据重复，违反唯一约束，请检查输入"));
    }

    /**
     * 处理通用数据库访问异常
     */
    @ExceptionHandler(DataAccessException.class)
    public Mono<ApiResult<Void>> handleDataAccessException(DataAccessException e) {
        log.error("数据库访问异常: {}", e.getMessage(), e);
        return Mono.just(ApiResult.failResult(ApiResultCode.INTERNAL_SERVER_ERROR.getCode(),
                "数据库访问错误，请联系管理员"));
    }

    /**
     * 处理其他未捕获异常
     */
    @ExceptionHandler(Exception.class)
    public Mono<ApiResult<Void>> handleException(Exception e) {
        log.error("系统异常: {}", e.getMessage(), e);
        return Mono.just(ApiResult.failResult(ApiResultCode.INTERNAL_SERVER_ERROR));
    }
    /**
     * 处理权限不足异常 (AccessDeniedException)
     */
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public Mono<ApiResult<Void>> handleAccessDeniedException(Exception e) {
        log.warn("权限不足: {}", e.getMessage());
        return Mono.just(ApiResult.failResult(ApiResultCode.FORBIDDEN.getCode(), "您没有权限访问该资源"));
    }
}
