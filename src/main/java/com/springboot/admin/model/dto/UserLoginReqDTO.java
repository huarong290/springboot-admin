package com.springboot.admin.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录实体类
 */
@Data
@Schema(description = "用户登录请求参数")
public class UserLoginReqDTO {
    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "admin", required = true)
    @NotBlank(message = "用户名不能为空")
    private String username;
    /**
     * 密码
     */
    @Schema(description = "密码", example = "123456", required = true)
    @NotBlank(message = "密码不能为空")
    private String password;
    /**
     * 验证码
     */
    @Schema(description = "验证码", example = "ABCD", required = true)
    @NotBlank(message = "验证码不能为空")
    private String captchaCode;
    /**
     * 验证码标识
     */
    @Schema(description = "验证码ID", example = "captcha_123456", required = true)
    @NotBlank(message = "验证码标识不能为空")
    private String captchaId;

}