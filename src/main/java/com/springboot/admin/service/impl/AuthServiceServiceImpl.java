package com.springboot.admin.service.impl;

import com.springboot.admin.config.JwtProperties;
import com.springboot.admin.exception.BusinessException;
import com.springboot.admin.model.dto.TokenRefreshReqDTO;
import com.springboot.admin.model.dto.TokenResDTO;
import com.springboot.admin.model.dto.UserLoginReqDTO;
import com.springboot.admin.service.IAuthService;
import com.springboot.admin.service.IRedisService;
import com.springboot.admin.service.ISysUserService;
import com.springboot.admin.utils.JwtUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class AuthServiceServiceImpl implements IAuthService {

    @Resource
    private JwtUtil jwtUtil;
    @Resource
    private  JwtProperties jwtProperties;

    @Resource
    private IRedisService redisService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ISysUserService sysUserService;
    /**
     * 用户登录：验证密码，生成并存储 Access 和 Refresh Token
     *
     * @param dto 用户登录请求参数（用户名、密码）
     * @return Mono<TokenResDTO> 响应式单对象，包含 AccessToken 和 RefreshToken
     */
    @Override
    public Mono<TokenResDTO> login(UserLoginReqDTO dto) {
        return sysUserService.getUserByUsername(dto.getUsername())
                .flatMap(user -> {
                    log.info("pass={}",passwordEncoder.encode(dto.getPassword()));
                    if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
                        log.warn("用户 {} 登录失败：密码错误", dto.getUsername());
                        return Mono.error(new RuntimeException("用户名或密码错误"));
                    }

                    Mono<String> accessTokenMono = jwtUtil.generateAccessToken(user.getUsername(), null);
                    Mono<String> refreshTokenMono = jwtUtil.generateRefreshToken(user.getUsername());

                    return Mono.zip(accessTokenMono, refreshTokenMono)
                            .flatMap(tuple -> {
                                String accessToken = tuple.getT1();
                                String refreshToken = tuple.getT2();

                                // 把存储 RefreshToken 放进响应式链
                                return redisService.storeRefreshToken(refreshToken, user.getUsername())
                                        .then(Mono.defer(() -> {
                                            TokenResDTO tokenResDTO = new TokenResDTO();
                                            tokenResDTO.setAccessToken(accessToken);
                                            tokenResDTO.setRefreshToken(refreshToken);


                                            // 设置过期时间（秒）
                                            tokenResDTO.setExpiresIn(jwtProperties.getAccessTokenExpiration() / 1000);
                                            log.info("用户 {} 登录成功，生成 Token", dto.getUsername());
                                            return Mono.just(tokenResDTO);
                                        }));
                            });
                })
                .switchIfEmpty(Mono.error(new RuntimeException("用户不存在")));
    }

    /**
     * 使用刷新令牌获取新的访问令牌
     */
    @Override
    public Mono<TokenResDTO> refreshToken(TokenRefreshReqDTO dto) {
        return redisService.isRefreshTokenStored(dto.getRefreshToken())
                .flatMap(exists -> {
                    if (!exists) {
                        log.warn("刷新令牌无效或已过期: {}", dto.getDeviceId());
                        return Mono.error(new RuntimeException("刷新令牌无效或已过期"));
                    }

                    // 从 Redis 获取用户名
                    return redisService.getValue("refresh:" + dto.getRefreshToken())
                            .flatMap(username -> {
                                // 生成新的 AccessToken
                                return jwtUtil.generateAccessToken(username, null)
                                        .flatMap(newAccessToken -> {
                                            TokenResDTO tokenResDTO = new TokenResDTO();
                                            tokenResDTO.setAccessToken(newAccessToken);
                                            tokenResDTO.setRefreshToken(dto.getRefreshToken()); // 保持原 RefreshToken
                                            tokenResDTO.setExpiresIn(jwtProperties.getAccessTokenExpiration() / 1000);
                                            tokenResDTO.setTokenType("Bearer");

                                            log.info("用户 {} 使用刷新令牌成功生成新的 AccessToken", username);
                                            return Mono.just(tokenResDTO);
                                        });
                            });
                });
    }

    @Override
    public Mono<Void> logout(String refreshToken) {
        return redisService.isRefreshTokenStored(refreshToken)
                .flatMap(exists -> {
                    if (!exists) {
                        // 抛出业务异常，交给全局异常处理器
                        return Mono.error(new BusinessException("0100103", "令牌已被撤销"));
                    }
                    return redisService.removeRefreshToken(refreshToken)
                            .doOnSuccess(v -> log.info("刷新令牌 {} 已移除，用户登出成功", refreshToken));
                });
    }



}
