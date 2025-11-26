package com.springboot.admin.service;

import com.springboot.admin.model.dto.TokenRefreshReqDTO;
import com.springboot.admin.model.dto.TokenResDTO;
import com.springboot.admin.model.dto.UserLoginReqDTO;
import com.springboot.admin.model.dto.user.UserInfoDTO;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface IAuthService {
    /**
     * 用户登录
     */
    Mono<TokenResDTO> login(UserLoginReqDTO dto);
    /**
     * 使用刷新令牌获取新的访问令牌
     */
    Mono<TokenResDTO> refreshToken(TokenRefreshReqDTO dto);
    /**
     * 用户登出：移除刷新令牌，使用户立即失效
     */
    Mono<Void> logout(String refreshToken);

    /**
     *
     * @param token
     * @return
     */
    Mono<UserInfoDTO> getUserInfoByToken(String token);
}
