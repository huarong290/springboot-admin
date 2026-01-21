package com.springboot.admin.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JWT 令牌刷新请求数据传输对象
 * 用于接收客户端提交的刷新令牌请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "刷新令牌请求对象")
public class TokenRefreshReqDTO {

    /**
     * 刷新令牌字符串
     */
    @Schema(description = "刷新令牌", required = true)
    @NotBlank(message = "刷新令牌不能为空")
    private String refreshToken;

    /**
     * 客户端设备唯一标识（前端生成并持久化）
     * 用于多设备登录场景下的令牌绑定（建议传入）
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
    @Schema(description = "浏览器信息", example = "Chrome 121")
    private String browser;

    /**
     * 登录IP
     */
    private String loginIp;
}

