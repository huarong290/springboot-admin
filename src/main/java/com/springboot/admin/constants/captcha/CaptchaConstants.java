package com.springboot.admin.constants.captcha;

/**
 * 验证码相关常量
 */
public final class CaptchaConstants {

    private CaptchaConstants() {}

    // ---------------- Redis ----------------

    /**
     * 验证码 Redis Key 模板
     * 建议格式：captcha:store:{id}
     * 这样在代码里可以用 String.format(CAPTCHA_STORE_KEY, id)
     */
    public static final String CAPTCHA_STORE_KEY = "captcha:store:%s";

    /**
     * 验证码频率限制 Redis Key 模板
     * 格式：captcha:limit:{scene}:{target}
     */
    public static final String CAPTCHA_LIMIT_KEY = "captcha:limit:%s:%s";

    /**
     * 验证码错误计数 Redis Key 模板 (用于实现 max-attempts)
     * 格式：captcha:attempts:{id}
     */
    public static final String CAPTCHA_ATTEMPT_KEY = "captcha:attempts:%s";
}
