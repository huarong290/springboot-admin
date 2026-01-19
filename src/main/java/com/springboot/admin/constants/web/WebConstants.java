package com.springboot.admin.constants.web;

/**
 * Web 层相关常量
 */
public final class WebConstants {

    private WebConstants() {}

    // ---------------- Pagination ----------------

    /**
     * 默认页码
     */
    public static final int DEFAULT_PAGE = 1;

    /**
     * 默认分页大小
     */
    public static final int DEFAULT_SIZE = 10;

    /**
     * 最大分页大小（防止恶意请求）
     */
    public static final int MAX_PAGE_SIZE = 100;
}
