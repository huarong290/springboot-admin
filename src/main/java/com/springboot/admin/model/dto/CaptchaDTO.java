package com.springboot.admin.model.dto;

import lombok.Data;

/**
 * 验证码 数据传输对象
 */
@Data
public class CaptchaDTO {
    /**
     * 一个唯一的 captchaId（方便后端存储和校验）
     */
    private String captchaId;
    /**
     *一个 Base64 图片字符串
     */
    private String captchaImage;
    /**
     * 一个 过期时间
     */
    private Long expireTime;
    /**
     * 一个 是否启用标志
     */
    private Boolean captchaEnabled;
}
