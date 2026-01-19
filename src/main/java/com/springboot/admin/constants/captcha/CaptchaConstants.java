package com.springboot.admin.constants.captcha;



public final class CaptchaConstants {

    private CaptchaConstants() {}

    /** 验证码 Redis 前缀 */
    public static final String CAPTCHA_PREFIX = "captcha:";

    /** 验证码过期时间（分钟） */
    public static final long CAPTCHA_EXPIRE_MINUTES = 5L;

    /** 验证码长度 */
    public static final int CAPTCHA_LENGTH = 4;

    /** 验证码图片宽度 */
    public static final int CAPTCHA_WIDTH = 120;

    /** 验证码图片高度 */
    public static final int CAPTCHA_HEIGHT = 40;
}
