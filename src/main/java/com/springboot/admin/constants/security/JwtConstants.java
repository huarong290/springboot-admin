package com.springboot.admin.constants.security;

public final class JwtConstants {

    private JwtConstants() {}

    /** Authorization Header */
    public static final String JWT_HEADER = "Authorization";

    /** Bearer 前缀（注意后面有空格） */
    public static final String JWT_BEARER_PREFIX = "Bearer ";

    /** Redis 中 refreshToken 的 key 前缀 */
    public static final String REFRESH_TOKEN_PREFIX = "jwt:refresh:";

    /** Redis 中 jti 黑名单 key 前缀（防重放） */
    public static final String JTI_BLACKLIST_PREFIX = "jwt:blacklist:";
}
