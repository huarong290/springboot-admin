package com.springboot.admin.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "用户登录请求参数")
public class UserLoginReqDTO {

    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "admin")
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @Schema(description = "密码", example = "123456")
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 验证码
     */
    @Schema(description = "验证码", example = "ABCD")
    @NotBlank(message = "验证码不能为空")
    private String captchaCode;

    /**
     * 验证码标识
     */
    @Schema(description = "验证码ID", example = "captcha_123456")
    @NotBlank(message = "验证码标识不能为空")
    private String captchaId;

    // ============================
    // 🟡【新增】设备与风控字段
    // ============================

    /**
     * 客户端设备唯一标识
     * 前端生成规则建议：MD5(浏览器指纹 + 屏幕分辨率 + UserAgent) 或 UUID (存LocalStorage)
     */
    @Schema(description = "客户端设备ID (UUID或指纹)", example = "web-3f92c2c9-88a1")
    @NotBlank(message = "设备ID不能为空")
    private String deviceId;

    /**
     * 客户端类型
     * 建议后端限制枚举值，防止脏数据
     */
    @Schema(description = "客户端类型: WEB/APP/MINI/H5", example = "WEB")
    @Pattern(regexp = "^(WEB|APP|MINI|H5|OTHER)$", message = "客户端类型格式错误")
    private String clientType = "WEB"; // 默认值

    /**
     * 客户端操作系统
     */
    @Schema(description = "操作系统", example = "Windows 11")
    private String os;

    /**
     * 浏览器信息
     */
    @Schema(description = "浏览器", example = "Chrome 121.0.0.0")
    private String browser;

    /**
     * 登录IP
     * ❌ 注意：不要让 Swagger 显示这个字段，也不要接受前端传值
     * 这个字段只应由 Controller 通过 HttpServletRequest 获取并 set 进去
     */
    @Schema(hidden = true)
    @JsonIgnore // 防止前端通过 JSON body 恶意篡改 IP
    private String loginIp;

}