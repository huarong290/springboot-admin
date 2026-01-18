package com.springboot.admin.service;

import com.springboot.admin.model.dto.CaptchaDTO;

/**
 * 验证码服务接口
 * <p>
 * 提供生成验证码、校验验证码等功能
 */
public interface ICaptchaService {

    /**
     * 生成验证码
     *
     * @return CaptchaDTO 包含验证码图片 Base64 和标识
     */
    CaptchaDTO generateCaptcha();

    /**
     * 校验验证码
     *
     * @param captchaId 验证码标识
     * @param code 用户输入的验证码
     * @return 是否验证通过
     */
    boolean validateCaptcha(String captchaId, String code);

    /**
     * 删除验证码（返回是否成功）
     */
    boolean deleteCaptchaReturnBoolean(String captchaId);

    /**
     * 删除验证码（返回删除数量）
     */
    long deleteCaptchaReturnCount(String captchaId);
}
