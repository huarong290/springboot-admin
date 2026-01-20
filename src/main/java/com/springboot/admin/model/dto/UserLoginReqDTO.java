package com.springboot.admin.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

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

    // ============================
    // 🟡【新增】设备与风控字段
    // ============================

    /**
     * 客户端设备唯一标识（前端生成并持久化）
     * 用于 RefreshToken 绑定设备
     */
    @Schema(description = "客户端设备ID", example = "web-3f92c2c9-88a1")
    @NotBlank(message = "设备ID不能为空")
    private String deviceId;

    /**
     * 客户端类型：WEB / APP / MINI / OTHER
     */
    @Schema(description = "客户端类型", example = "WEB")
    private String clientType;

    /**
     * 客户端操作系统
     */
    @Schema(description = "操作系统", example = "Windows / Mac / Android / iOS")
    private String os;

    /**
     * 浏览器信息
     */
    @Schema(description = "浏览器", example = "Chrome 121")
    private String browser;

    /**
     * 登录IP
     */
    private String loginIp;

}
