package com.springboot.admin.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 验证码校验结果枚举
 */
@Getter
@AllArgsConstructor
public enum CaptchaValidationResult {

    SUCCESS(BusinessResultCode.BUSINESS_SUCCESS_CODE),

    NOT_FOUND(BusinessResultCode.CAPTCHA_NOT_FOUND),
    INVALID(BusinessResultCode.CAPTCHA_INVALID),
    USED(BusinessResultCode.CAPTCHA_USED),
    MISMATCH(BusinessResultCode.MISMATCH),
    INVALID_CONTEXT(BusinessResultCode.CAPTCHA_CONTEXT_MISMATCH),
    TOO_MANY_ATTEMPTS(BusinessResultCode.CAPTCHA_TOO_MANY_ATTEMPTS),
    ;
    private final BusinessResultCode businessCode;

    public boolean isSuccess() {
        return this == SUCCESS;
    }
}
