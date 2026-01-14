package com.springboot.admin.service;

import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

/**
 * Redis 服务接口，定义了系统中常用的 Redis 操作。
 * 使用 Reactor 的 Mono 类型，保证异步非阻塞。
 */
public interface IRedisService {

    /**
     * 设置一个键值对，并指定过期时间。
     *
     * @param key     Redis 键
     * @param value   Redis 值
     * @param expire  过期时间数值
     * @param timeUnit 过期时间单位（秒、分钟等）
     * @return Mono<Boolean> 表示写入是否成功
     */
    Mono<Boolean> setValue(String key, String value, long expire, TimeUnit timeUnit);

    /**
     * 根据 key 获取值。
     *
     * @param key Redis 键
     * @return Mono<String> 如果存在返回值，否则返回 Mono.empty()
     */
    Mono<String> getValue(String key);

    /**
     * 删除指定 key。
     *
     * @param key Redis 键
     * @return Mono<Long> 删除数量（0 表示未删除，1 表示删除成功）
     */
    Mono<Long> deleteKey(String key);

    /**
     * 存储访问令牌。
     *
     * @param token    访问令牌
     * @param username 用户名
     * @return Mono<Void> 表示异步完成
     */
    Mono<Void> storeToken(String token, String username);

    /**
     * 校验访问令牌是否有效。
     *
     * @param token 访问令牌
     * @return Mono<Boolean> true 表示存在，false 表示不存在
     */
    Mono<Boolean> isTokenValid(String token);

    /**
     * 删除访问令牌。
     *
     * @param token 访问令牌
     * @return Mono<Void> 表示异步完成
     */
    Mono<Void> removeToken(String token);

    /**
     * 存储刷新令牌。
     *
     * @param refreshToken 刷新令牌
     * @param username     用户名
     * @return Mono<Void> 表示异步完成
     */
    Mono<Void> storeRefreshToken(String refreshToken, String username);

    /**
     * 校验刷新令牌是否存在。
     *
     * @param refreshToken 刷新令牌
     * @return Mono<Boolean> true 表示存在，false 表示不存在
     */
    Mono<Boolean> isRefreshTokenStored(String refreshToken);

    /**
     * 删除刷新令牌。
     *
     * @param requestRefreshToken 刷新令牌
     * @return Mono<Void> 表示异步完成
     */
    Mono<Void> removeRefreshToken(String requestRefreshToken);

    /**
     * 根据用户名获取刷新令牌。
     *
     * @param username 用户名
     * @return Mono<String> 返回刷新令牌，如果不存在返回 Mono.empty()
     */
    Mono<String> getRefreshTokenByUsername(String username);
}
