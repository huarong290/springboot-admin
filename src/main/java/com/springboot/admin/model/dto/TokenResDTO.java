package com.springboot.admin.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenResDTO {
    /**
     * 新生成的访问令牌
     * 有效期通常较短（如30分钟）
     */
    private String accessToken;

    /**
     * 可选的刷新令牌
     * 当采用轮换刷新令牌策略时返回新令牌
     */
    private String refreshToken;

    /**
     * 访问令牌过期时间（秒）
     * 便于前端计算过期时间
     */
    private Long expiresIn;

    /**
     * 令牌类型
     * 固定为"Bearer"（OAuth 2.0标准）
     */
    private String tokenType = "Bearer";
}
