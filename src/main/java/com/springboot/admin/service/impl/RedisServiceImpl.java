package com.springboot.admin.service.impl;

import com.springboot.admin.config.JwtProperties;
import com.springboot.admin.service.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

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
    public Mono<Void> storeToken(String token, String username) {
        return redisTemplate.opsForValue()
                .set(buildAccessKey(token), username, Duration.ofMillis(jwtProperties.getAccessTokenExpiration()))
                .then();
    }

    @Override
    public Mono<Boolean> isTokenValid(String token) {
        return redisTemplate.hasKey(buildAccessKey(token));
    }

    @Override
    public Mono<Void> removeToken(String token) {
        return redisTemplate.delete(buildAccessKey(token)).then();
    }

    @Override
    public Mono<Void> storeRefreshToken(String refreshToken, String username) {
        return redisTemplate.opsForValue()
                .set(buildRefreshKey(refreshToken), username, Duration.ofMillis(jwtProperties.getRefreshTokenExpiration()))
                .then();
    }

    @Override
    public Mono<Boolean> isRefreshTokenStored(String refreshToken) {
        return redisTemplate.hasKey(buildRefreshKey(refreshToken));
    }

    @Override
    public Mono<Void> removeRefreshToken(String requestRefreshToken) {
        return redisTemplate.delete(buildRefreshKey(requestRefreshToken)).then();
    }

    @Override
    public Mono<Void> setValue(String key, String value, long expire, TimeUnit timeUnit) {
        return redisTemplate.opsForValue()
                .set(key, value, Duration.ofMillis(timeUnit.toMillis(expire)))
                .then();
    }

    @Override
    public Mono<String> getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public Mono<Void> deleteKey(String key) {
        return redisTemplate.delete(key).then();
    }

    @Override
    public Mono<String> getRefreshTokenByUsername(String username) {
        return redisTemplate.keys("refresh:*")
                .flatMap(k -> redisTemplate.opsForValue().get(k)
                        .filter(v -> v.equals(username))
                        .map(v -> k.replace("refresh:", "")))
                .next();
    }

    private String buildAccessKey(String token) {
        return "access:" + token;
    }

    private String buildRefreshKey(String token) {
        return "refresh:" + token;
    }
}
