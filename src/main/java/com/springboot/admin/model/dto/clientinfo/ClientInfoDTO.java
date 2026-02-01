package com.springboot.admin.model.dto.clientinfo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ClientInfoDTO {

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
}
