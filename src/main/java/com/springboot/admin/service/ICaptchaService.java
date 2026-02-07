package com.springboot.admin.service;

import com.springboot.admin.common.CaptchaValidationResult;
import com.springboot.admin.model.dto.CaptchaDTO;

public interface ICaptchaService {

    /**
     * 生成验证码
     *
     * @param captchaType 验证码类型（IMAGE / SMS / EMAIL）
     * @param length 验证码长度（不同类型下语义不同）
     * @return CaptchaDTO 包含验证码图片 Base64、标识、过期时间等信息
     */
    CaptchaDTO generateCaptcha(String captchaType, int length);

    /**
     * 校验验证码
     *
     * @param captchaId 验证码标识
     * @param code 用户输入的验证码
     * @return 校验结果枚举，明确区分成功/失败原因
     *
     * <p>注意：校验成功后验证码将自动删除，避免重复使用。</p>
     */
    CaptchaValidationResult validateCaptcha(String captchaId, String code);

    /**
     * 删除验证码（主要用于管理或异常处理场景）
     * @param captchaId 验证码标识
     * @return 是否删除成功
     */
    boolean deleteCaptcha(String captchaId);
}

