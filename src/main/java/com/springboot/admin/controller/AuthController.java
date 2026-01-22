package com.springboot.admin.controller;

import com.springboot.admin.annotation.Loggable;
import com.springboot.admin.common.ApiResult;
import com.springboot.admin.model.dto.CaptchaDTO;
import com.springboot.admin.model.dto.TokenRefreshReqDTO;
import com.springboot.admin.model.dto.TokenResDTO;
import com.springboot.admin.model.dto.UserLoginReqDTO;
import com.springboot.admin.model.dto.user.UserInfoDTO;
import com.springboot.admin.service.IAuthService;
import com.springboot.admin.service.ICaptchaService;
import com.springboot.admin.utils.IpUtil;
import com.springboot.admin.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证管理", description = "用户登录、Token管理及用户信息查询")
@RequiredArgsConstructor // 使用构造器注入

public class AuthController {

    private final IAuthService authService;
    private final ICaptchaService captchaService;


    @Operation(summary = "获取验证码", description = "返回 Base64 图片及验证码标识")
    @Loggable(logRequest = false, logResponse = false) // 图片数据过大，不建议打入日志
    @GetMapping("/getCaptcha")
    public ApiResult<CaptchaDTO> getCaptcha() {

        return ApiResult.successResult(captchaService.generateCaptcha());
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "包含验证码校验、账号密码验证及 Token 发放")
    @Loggable(logRequest = true, logResponse = true)
    public ApiResult<TokenResDTO> login(@RequestBody UserLoginReqDTO dto, HttpServletRequest request) {
        // 逻辑完全下沉至 Service，保持接口层清爽
        // 获取客户端 IP
        String clientIp = IpUtil.getClientIp(request);
        dto.setLoginIp(clientIp);
        return ApiResult.successResult(authService.login(dto));
    }

    @PostMapping("/refresh")
    @Operation(summary = "刷新令牌", description = "通过旧的 RefreshToken 获取新的 AccessToken")
    @Loggable(logRequest = true, logResponse = true)
    public ApiResult<TokenResDTO> refresh(@RequestBody TokenRefreshReqDTO dto,HttpServletRequest request) {
        // 逻辑完全下沉至 Service，保持接口层清爽
        // 获取客户端 IP
        String clientIp = IpUtil.getClientIp(request);
        dto.setLoginIp(clientIp);
        return ApiResult.successResult(authService.refreshToken(dto));

    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "作废当前 accessToken 和 refreshToken，需要 deviceId")
    @Loggable(logRequest = true, logResponse = true)
    public ApiResult<Void> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
                                  @RequestBody TokenRefreshReqDTO dto) {
        String accessToken = JwtUtil.extractBearerToken(authHeader);
        authService.logout(accessToken, dto.getRefreshToken(), dto.getDeviceId());
        return ApiResult.defaultFailResult();
    }


    @GetMapping("/userInfo")
    @Operation(summary = "获取用户信息", description = "拉取当前登录用户的角色、权限标识及菜单树")
    @Loggable(logRequest = false, logResponse = true)
    public ApiResult<UserInfoDTO> getUserInfo(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        // 仅负责解析 Token 标识，业务由 Service 处理
        String token = JwtUtil.extractBearerToken(authHeader);

        return ApiResult.successResult(authService.getUserInfoByToken(token));

    }
}
