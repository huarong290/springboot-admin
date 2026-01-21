package com.springboot.admin.constants;

/**
 * 系统全局通用常量类
 * <p>
 * 存放与具体业务无关、跨模块使用的常量
 */
public final class CommonConstants {

    private CommonConstants() {}

    // ---------------- Encoding ----------------

    /**
     * UTF-8 编码
     */
    public static final String UTF8 = "UTF-8";

    // ---------------- Redis ----------------

    /**
     * Redis Key 分隔符
     */
    public static final String REDIS_KEY_SEPARATOR = ":";
    /**
     *  根节点常量
     */
    public static final Long ROOT_PARENT_ID = 0L;
}

