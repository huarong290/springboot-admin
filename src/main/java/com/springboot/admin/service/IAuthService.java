package com.springboot.admin.service;

import com.springboot.admin.model.dto.TokenRefreshReqDTO;
import com.springboot.admin.model.dto.TokenResDTO;
import com.springboot.admin.model.dto.UserLoginReqDTO;
import com.springboot.admin.model.dto.user.UserInfoDTO;

/**
 * 认证服务接口
 * <p>
 * 提供登录、登出、刷新 Token、获取用户信息等功能
 */
public interface IAuthService {

    /**
     * 用户登录
     *
     * @param dto 用户登录请求参数
     * @return TokenResDTO 包含 AccessToken、RefreshToken 等信息
     */
    TokenResDTO login(UserLoginReqDTO dto);

    /**
     * 刷新令牌
     *
     * @param dto Token 刷新请求
     * @return TokenResDTO 返回新的 AccessToken
     */
    TokenResDTO refreshToken(TokenRefreshReqDTO dto);

    /**
     * 用户登出
     *
     * @param refreshToken 当前用户的刷新令牌
     */
    void logout(String accessToken,String refreshToken, String deviceId);

    /**
     * 根据 Token 获取当前登录用户信息
     *
     * @param token AccessToken
     * @return UserInfoDTO
     */
    UserInfoDTO getUserInfoByToken(String token);
}

