package com.springboot.admin.common;

import com.springboot.admin.common.ApiResultCode;
import com.springboot.admin.common.IApiResult;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ApiResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private String code;
    private String message;
    private T data;
    private Long timestamp;

    /** 默认成功返回 */
    public static <T> ApiResult<T> defaultSuccessResult() {
        return buildResult(ApiResultCode.SUCCESS.getCode(), ApiResultCode.SUCCESS.getMessage(), null);
    }

    /** 成功返回，带数据 */
    public static <T> ApiResult<T> successResult(T data) {
        return buildResult(ApiResultCode.SUCCESS.getCode(), ApiResultCode.SUCCESS.getMessage(), data);
    }

    public static <T> ApiResult<T> successResult(String message, T data) {
        return buildResult(ApiResultCode.SUCCESS.getCode(), message, data);
    }

    public static <T> ApiResult<T> successResult(IApiResult iApiResult, T data) {
        return buildResult(iApiResult.getCode(), iApiResult.getMessage(), data);
    }

    /** 默认失败返回 */
    public static <T> ApiResult<T> defaultFailResult() {
        return buildResult(ApiResultCode.FAILED.getCode(), ApiResultCode.FAILED.getMessage(), null);
    }

    /** 失败返回（只带消息） */
    public static <T> ApiResult<T> failResult(String message) {
        return buildResult(ApiResultCode.FAILED.getCode(), message, null);
    }

    /** 失败返回（带错误码和消息） */
    public static <T> ApiResult<T> failResult(String code, String message) {
        return buildResult(code, message, null);
    }

    /** 失败返回（带枚举错误码） */
    public static <T> ApiResult<T> failResult(IApiResult iApiResult) {
        return buildResult(iApiResult.getCode(), iApiResult.getMessage(), null);
    }

    /** 失败返回（带数据） */
    public static <T> ApiResult<T> failResult(T data) {
        return buildResult(ApiResultCode.FAILED.getCode(), ApiResultCode.FAILED.getMessage(), data);
    }

    public static <T> ApiResult<T> failResult(String message, T data) {
        return buildResult(ApiResultCode.FAILED.getCode(), message, data);
    }

    public static <T> ApiResult<T> failResult(IApiResult iApiResult, T data) {
        return buildResult(iApiResult.getCode(), iApiResult.getMessage(), data);
    }
    public static <T> ApiResult<T> failResult(IApiResult iApiResult, String message) {
        return buildResult(iApiResult.getCode(), message, null);
    }

    /** 构造统一返回结果 */
    private static <T> ApiResult<T> buildResult(String code, String message, T data) {
        ApiResult<T> result = new ApiResult<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }

    /** 转换为 Map */
    public Map<String, Object> buildResultMap(String code, String message, T data) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", code);
        resultMap.put("message", message);
        resultMap.put("data", data);
        resultMap.put("timestamp", System.currentTimeMillis());
        return resultMap;
    }
}
