package com.springboot.admin.service;

import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

public interface IRedisService {

    /**
     * 存储用户令牌到Redis
     */
    Mono<Void> storeToken(String token, String username);

    /**
     * 验证令牌是否有效（存在且未过期）
     */
    Mono<Boolean> isTokenValid(String token);

    /**
     * 从Redis中移除指定令牌
     */
    Mono<Void> removeToken(String token);

    /**
     * 存储刷新令牌到Redis
     */
    Mono<Void> storeRefreshToken(String refreshToken, String username);

    /**
     * 检查刷新令牌是否存在
     */
    Mono<Boolean> isRefreshTokenStored(String refreshToken);

    /**
     * 移除指定的刷新令牌
     */
    Mono<Void> removeRefreshToken(String requestRefreshToken);

    /**
     * 设置键值对并指定过期时间
     */
    Mono<Void> setValue(String key, String value, long expire, TimeUnit timeUnit);

    /**
     * 获取值
     */
    Mono<String> getValue(String key);

    /**
     * 删除数据
     */
    Mono<Void> deleteKey(String key);

    /**
     * 根据用户名获取刷新令牌
     */
    Mono<String> getRefreshTokenByUsername(String username);
}
