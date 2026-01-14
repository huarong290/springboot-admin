package com.springboot.admin.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenResDTO {

    /**
     * 新生成的访问令牌（AccessToken）
     * 有效期通常较短（如30分钟）
     */
    private String accessToken;

    /**
     * 刷新令牌（RefreshToken）
     * 当采用轮换刷新令牌策略时返回新令牌
     */
    private String refreshToken;

    /**
     * AccessToken 过期时间（秒）
     * 前端用于倒计时或刷新策略
     */
    private Long expiresIn;

    /**
     * 令牌类型，固定为 "Bearer"
     */

    private  String tokenType = "Bearer";

    // ============================
    // 🟡【优化新增】可选字段，前端/后台显示或风控
    // ============================

    /**
     * 登录设备ID（与RefreshToken绑定）
     */
    private String deviceId;

    /**
     * 客户端类型：WEB / APP / MINI / OTHER
     */
    private String clientType;

    /**
     * 登录IP
     */
    private String ip;
}
