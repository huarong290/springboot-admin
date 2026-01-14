package com.springboot.admin.service.impl;

import com.springboot.admin.config.JwtProperties;
import com.springboot.admin.service.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Redis 服务实现类，基于 ReactiveStringRedisTemplate 实现异步非阻塞操作。
 * 所有方法均返回 Reactor Mono 类型，保证响应式编程风格。
 */
@Service
@Slf4j
public class RedisServiceImpl implements IRedisService {

    private final JwtProperties jwtProperties;
    private final ReactiveStringRedisTemplate redisTemplate;

    public RedisServiceImpl(JwtProperties jwtProperties, ReactiveStringRedisTemplate redisTemplate) {
        this.jwtProperties = jwtProperties;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Mono<Boolean> setValue(String key, String value, long expire, TimeUnit timeUnit) {
        // 将过期时间转换为 Duration
        Duration duration = Duration.ofMillis(timeUnit.toMillis(expire));
        return redisTemplate.opsForValue()
                .set(key, value, duration)
                .doOnNext(success -> log.info("Redis写入: key={}, value={}, expire={} {}, success={}",
                        key, value, expire, timeUnit, success));
    }

    @Override
    public Mono<String> getValue(String key) {
        return redisTemplate.opsForValue().get(key)
                .doOnNext(val -> log.info("Redis读取: key={}, value={}", key, val))
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Redis未找到 key={}", key);
                    return Mono.empty();
                }));
    }

    @Override
    public Mono<Long> deleteKey(String key) {
        return redisTemplate.delete(key)
                .doOnNext(count -> log.info("Redis删除: key={}, count={}", key, count));
    }

    @Override
    public Mono<Void> storeToken(String token, String username) {
        return redisTemplate.opsForValue()
                .set(buildAccessKey(token), username, Duration.ofMillis(jwtProperties.getAccessTokenExpiration()))
                .doOnNext(success -> log.info("存储访问令牌: token={}, username={}, success={}", token, username, success))
                .then();
    }

    @Override
    public Mono<Boolean> isTokenValid(String token) {
        return redisTemplate.hasKey(buildAccessKey(token))
                .doOnNext(valid -> log.info("校验访问令牌: token={}, valid={}", token, valid));
    }

    @Override
    public Mono<Void> removeToken(String token) {
        return redisTemplate.delete(buildAccessKey(token))
                .doOnNext(count -> log.info("删除访问令牌: token={}, count={}", token, count))
                .then();
    }

    @Override
    public Mono<Void> storeRefreshToken(String refreshToken, String username) {
        return redisTemplate.opsForValue()
                .set(buildRefreshKey(refreshToken), username, Duration.ofMillis(jwtProperties.getRefreshTokenExpiration()))
                .doOnNext(success -> log.info("存储刷新令牌: refreshToken={}, username={}, success={}", refreshToken, username, success))
                .then();
    }

    @Override
    public Mono<Boolean> isRefreshTokenStored(String refreshToken) {
        return redisTemplate.hasKey(buildRefreshKey(refreshToken))
                .doOnNext(valid -> log.info("校验刷新令牌: refreshToken={}, valid={}", refreshToken, valid));
    }

    @Override
    public Mono<Void> removeRefreshToken(String requestRefreshToken) {
        return redisTemplate.delete(buildRefreshKey(requestRefreshToken))
                .doOnNext(count -> log.info("删除刷新令牌: refreshToken={}, count={}", requestRefreshToken, count))
                .then();
    }

    @Override
    public Mono<String> getRefreshTokenByUsername(String username) {
        return redisTemplate.keys("refresh:*")
                .flatMap(k -> redisTemplate.opsForValue().get(k)
                        .filter(v -> v.equals(username))
                        .map(v -> k.replace("refresh:", "")))
                .next()
                .doOnNext(token -> log.info("根据用户名获取刷新令牌: username={}, refreshToken={}", username, token));
    }

    private String buildAccessKey(String token) {
        return "access:" + token;
    }

    private String buildRefreshKey(String token) {
        return "refresh:" + token;
    }
}
