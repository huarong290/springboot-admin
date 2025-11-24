package com.springboot.admin.service;

import com.springboot.admin.model.dto.CaptchaDTO;
import reactor.core.publisher.Mono;

public interface ICaptchaService {
    /**
     * 生成验证码，返回 Base64 图片字符串
     */
    Mono<CaptchaDTO> generateCaptcha();

    /**
     * 校验验证码
     *
     * @param captchaId   验证码ID
     * @param captchaCode 用户输入的验证码
     */
    Mono<Boolean> validateCaptcha(String captchaId, String captchaCode);



    /**
     * 删除验证码，返回布尔值（是否成功删除）
     */
    Mono<Boolean> deleteCaptchaReturnBoolean(String captchaId);

    /**
     * 删除验证码，返回删除数量
     */
    Mono<Long> deleteCaptchaReturnCount(String captchaId);
}
