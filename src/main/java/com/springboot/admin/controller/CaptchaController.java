package com.springboot.admin.controller;

import com.springboot.admin.common.ApiResult;
import com.springboot.admin.enums.CaptchaType;
import com.springboot.admin.model.dto.CaptchaDTO;
import com.springboot.admin.service.ICaptchaService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/captcha")
public class CaptchaController {

    @Autowired
    private ICaptchaService captchaService;

    /**
     * 获取图形验证码
     * <p>
     * 不记录请求日志，防止 Base64 图片数据撑爆日志文件
     * </p>
     */
    @Operation(summary = "获取验证码", description = "返回 Base64 图片及验证码标识")
    @GetMapping("/getCaptcha")
    public ApiResult<CaptchaDTO> getCaptcha(@RequestParam CaptchaType type,
                                            @RequestParam String scene,
                                            @RequestParam String target) {
        return ApiResult.successResult(captchaService.generate(type, scene, target));
    }

}