package com.springboot.admin.constants;

/**
 * 系统全局常量类
 * 1. 请求相关
 * 2. 日志/链路相关
 * 3. 默认分页/超时等
 * 4. 可扩展存放其他固定值
 */
public final class CommonConstants {

    private CommonConstants() {} // 私有构造，防止实例化

    // ---------------- 日志 / 链路相关 ----------------
    /** MDC 中 traceId 的 key */
    public static final String TRACE_ID = "traceId";
    /** HTTP 请求头 traceId */
    public static final String TRACE_HEADER = "X-Trace-Id";

    // ---------------- 请求相关 ----------------
    /** 默认分页参数 */
    public static final int DEFAULT_PAGE = 1;
    public static final int DEFAULT_SIZE = 10;

    // ---------------- 用户相关 ----------------
    public static final String DEFAULT_ADMIN_USERNAME = "system";

    // ---------------- JWT 相关 ----------------
    public static final String JWT_HEADER = "Authorization";
    public static final String JWT_BEARER_PREFIX = "Bearer ";

    // ---------------- Redis / 验证码相关 ----------------
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

    // ---------------- 其他 ----------------
    public static final String UTF8 = "UTF-8";
}
