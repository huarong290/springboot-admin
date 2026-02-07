
package com.springboot.admin.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务错误码枚举（七位）
 * 结构：AA-BBB-CC
 * AA = 系统编号
 * BBB = 模块编号
 * CC = 错误编号
 */
@Getter
@AllArgsConstructor
public enum BusinessResultCode implements IApiResult {
    BUSINESS_SUCCESS_CODE("0000000","业务执行成功"),
    // ================== 认证系统 (01) ==================
    AUTH_TOKEN_MISSING("0100101", "缺少认证令牌"),
    TOKEN_INVALID("0100102", "令牌无效或已过期"),
    TOKEN_REVOKED("0100103", "令牌已被撤销"),
    AUTH_PROCESSING_ERROR("0100104", "认证处理失败"),

    CAPTCHA_NOT_FOUND("0100105", "验证码不存在"),
    CAPTCHA_EXPIRED("0100106","验证码已过期"),
    CAPTCHA_INVALID("0100107", "验证码错误"),
    CAPTCHA_USED("0100107", "验证码已被使用"),
    // ================== 用户系统 (02) ==================
    USER_NOT_FOUND("0210001", "用户不存在"),
    USER_DISABLED("0210002", "用户账号已被禁用"),
    USER_ALREADY_EXISTS("0210003", "用户已存在"),
    USER_PASSWORD_ERROR("0210004", "密码错误"),

    // ================== 角色/权限系统 (03) ==================
    ROLE_NOT_EXIST("0320001", "角色不存在"),
    ROLE_ASSIGN_ERROR("0320002", "角色分配失败"),

    // ================== 订单系统 (04) ==================
    ORDER_NOT_FOUND("0430001", "订单不存在"),
    ORDER_STATUS_INVALID("0430002", "订单状态非法"),
    ORDER_PAYMENT_FAILED("0430003", "订单支付失败"),

    // ================== 支付系统 (05) ==================
    BALANCE_NOT_ENOUGH("0540001", "用户余额不足"),
    PAYMENT_METHOD_UNSUPPORTED("0540002", "不支持的支付方式"),

    // ================== 通用业务错误 (09) ==================
    OPERATION_NOT_ALLOWED("0990001", "操作不允许"),
    DATA_CONFLICT("0990002", "数据冲突"),
    PARAM_INVALID("0990003", "参数非法"),
    SYSTEM_INTERNAL_ERROR("0990004", "系统内部错误"),
    DEFAULT_BUSINESS_CODE("0999999", "默认业务异常码");

    private final String  code;
    private final String message;
}


