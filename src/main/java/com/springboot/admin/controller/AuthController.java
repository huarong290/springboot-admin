package com.springboot.admin.controller;

import com.springboot.admin.annotation.Logable;
import com.springboot.admin.common.ApiResult;
import com.springboot.admin.model.dto.CaptchaDTO;
import com.springboot.admin.model.dto.TokenRefreshReqDTO;
import com.springboot.admin.model.dto.TokenResDTO;
import com.springboot.admin.model.dto.UserLoginReqDTO;
import com.springboot.admin.model.dto.user.UserInfoDTO;
import com.springboot.admin.service.IAuthService;
import com.springboot.admin.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证管理", description = "用户登录、Token管理及用户信息查询")
@RequiredArgsConstructor // 使用构造器注入

public class AuthController {

    private final IAuthService authService;
    private final ICaptchaService captchaService;

    @Operation(summary = "获取验证码", description = "返回 Base64 图片及验证码标识")
    @Logable(logRequest = false, logResponse = false) // 图片数据过大，不建议打入日志
    @GetMapping("/getCaptcha")
    public ApiResult<CaptchaDTO> getCaptcha() {
        return captchaService.generateCaptcha()
                .map(ApiResult::successResult);
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "包含验证码校验、账号密码验证及 Token 发放")
    @Logable(logRequest = true, logResponse = true)
    public ApiResult<TokenResDTO> login(@RequestBody UserLoginReqDTO dto) {
        // 逻辑完全下沉至 Service，保持接口层清爽
        return authService.login(dto)
                .map(ApiResult::successResult);
    }

    @PostMapping("/refresh")
    @Operation(summary = "刷新令牌", description = "通过旧的 RefreshToken 获取新的 AccessToken")
    @Logable(logRequest = true, logResponse = true)
    public ApiResult<TokenResDTO> refresh(@RequestBody TokenRefreshReqDTO dto) {
        return authService.refreshToken(dto)
                .map(ApiResult::successResult);
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "作废当前刷新令牌")
    @Logable(logRequest = true, logResponse = true)
    public ApiResult<Void> logout(@RequestBody TokenRefreshReqDTO dto) {
        return authService.logout(dto.getRefreshToken())
                .then(Mono.just(ApiResult.successResult("登出成功", null)));
    }

    @GetMapping("/userInfo")
    @Operation(summary = "获取用户信息", description = "拉取当前登录用户的角色、权限标识及菜单树")
    @Logable(logRequest = false, logResponse = true)
    public ApiResult<UserInfoDTO> getUserInfo(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        // 仅负责解析 Token 标识，业务由 Service 处理
        String token = JwtUtil.extractBearerToken(authHeader);
        return authService.getUserInfoByToken(token)
                .map(ApiResult::successResult);
    }
}
