package com.springboot.admin.handler;

import com.springboot.admin.common.ApiResult;
import com.springboot.admin.common.ApiResultCode;
import com.springboot.admin.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * 作用：
 * 1. 捕获 Controller 层抛出的所有异常
 * 2. 转换为统一 ApiResult 返回给前端
 * 3. 防止异常信息直接暴露给用户
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理【业务异常】
     *
     * 使用场景：
     *  - 参数非法
     *  - 状态不允许
     *  - 权限不足
     *
     * 特点：
     *  - 异常是“预期内的”
     *  - 日志级别使用 warn
     */
    @ExceptionHandler(BaseException.class)
    public ApiResult<Void> handleBaseException(BaseException ex) {
        log.warn("业务异常 code={}, message={}", ex.getCode(), ex.getMessage());
        return ApiResult.failResult(ex.getCode(), ex.getMessage());
    }

    /**
     * 处理 @Valid 校验失败异常
     *
     * 例如：
     *  public ApiResult<?> add(@Valid UserDTO dto)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResult<Void> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {

        // 只返回第一个错误提示（够用且友好）
        String message = ex.getBindingResult()
                .getFieldError()
                .getDefaultMessage();

        return ApiResult.failResult(
                ApiResultCode.BAD_REQUEST.getCode(),
                message
        );
    }

    /**
     * 处理普通参数绑定异常
     *
     * 例如：
     *  Long id 传了 abc
     */
    @ExceptionHandler(BindException.class)
    public ApiResult<Void> handleBindException(BindException ex) {
        String message = ex.getFieldError().getDefaultMessage();
        return ApiResult.failResult(
                ApiResultCode.BAD_REQUEST.getCode(),
                message
        );
    }

    /**
     * 兜底异常（程序错误）
     *
     * 特点：
     *  - 一般是 BUG
     *  - 日志级别 ERROR
     *  - 前端不返回具体异常信息
     */
    @ExceptionHandler(Exception.class)
    public ApiResult<Void> handleException(Exception ex) {
        log.error("系统异常", ex);
        return ApiResult.failResult(ApiResultCode.INTERNAL_SERVER_ERROR);
    }
}
