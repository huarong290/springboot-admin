package com.springboot.admin.constants.security;

/**
 * JWT 相关常量定义
 * <p>
 * 统一管理：
 * - HTTP Header
 * - JWT 规范字段
 * - Token 类型
 * - Redis Key 前缀
 */
public final class JwtConstants {

    private JwtConstants() {}

    // ======================= HTTP Header =======================

    /**
     * Authorization Header
     */
    public static final String JWT_HEADER = "Authorization";

    /**
     * Bearer 前缀（注意后面有空格）
     */
    public static final String JWT_BEARER_PREFIX = "Bearer ";

    // ======================= JWT Claim =======================

    /**
     * JWT Claim：Token 类型字段名
     */
    public static final String CLAIM_TOKEN_TYPE = "type";

    /**
     * JWT ID（标准字段，用于防重放）
     */
    public static final String CLAIM_JTI = "jti";

    // ======================= Token Type =======================

    /**
     * Access Token
     */
    public static final String TOKEN_TYPE_ACCESS = "access";

    /**
     * Refresh Token
     */
    public static final String TOKEN_TYPE_REFRESH = "refresh";

    // ======================= Redis Key Prefix =======================

    /**
     * Redis 中 refreshToken 的 key 前缀
     * 示例：jwt:refresh:{username}:{deviceId}
     */
    public static final String REFRESH_TOKEN_PREFIX = "jwt:refresh:";

    /**
     * Redis 中 jti 黑名单 key 前缀（防重放）
     * 示例：jwt:blacklist:{jti}
     */
    public static final String JTI_BLACKLIST_PREFIX = "jwt:blacklist:";
}
