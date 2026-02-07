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
    private String captchaContent;
    /**
     * 一个 过期时间
     */
    private Long expireTime;
    /**
     * 是否已使用标志（true=已使用，false=未使用）
     */
    private boolean used;

    /**
     * 验证码类型（IMAGE / SMS / EMAIL）
     */
    private String captchaType;

    /**
     * 验证码场景
     */
    private String captchaScene;
}
