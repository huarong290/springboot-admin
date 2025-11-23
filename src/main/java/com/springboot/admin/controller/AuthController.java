package com.springboot.admin.controller;

import com.springboot.admin.annotation.Logable;
import com.springboot.admin.common.ApiResult;
import com.springboot.admin.common.ApiResultCode;
import com.springboot.admin.exception.BusinessException;
import com.springboot.admin.model.dto.TokenRefreshReqDTO;
import com.springboot.admin.model.dto.TokenResDTO;
import com.springboot.admin.model.dto.UserLoginReqDTO;
import com.springboot.admin.service.IAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证管理", description = "用户登录、注册、Token刷新等认证相关接口")
public class AuthController {
    @Autowired
    private IAuthService authService;

    @Operation(summary = "用户登录", description = "用户使用用户名密码和验证码登录系统")
    @Logable(logRequest = true, logResponse = true)
    @PostMapping("/login")
    public Mono<ApiResult<TokenResDTO>> login(@RequestBody UserLoginReqDTO dto) {
        return authService.login(dto)
                .map(ApiResult::successResult)
                .onErrorResume(e -> Mono.just(ApiResult.failResult(ApiResultCode.FAILED, null)));
    }

    @PostMapping("/refresh")
    @Operation(summary = "刷新令牌", description = "使用刷新令牌获取新的访问令牌")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<TokenResDTO>> refresh(@RequestBody TokenRefreshReqDTO dto) {
        return authService.refreshToken(dto)
                .map(ApiResult::successResult)
                .onErrorResume(e -> Mono.just(ApiResult.failResult(ApiResultCode.FAILED, null)));
    }


    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "移除刷新令牌，使用户立即失效")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<Void>> logout(ServerHttpRequest request) {
        String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isNotBlank(token) && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        return authService.logout(token)
                .thenReturn(ApiResult.<Void>successResult("退出登录成功", null))
                .onErrorResume(e -> {
                    // 如果是业务异常，返回对应错误码和消息
                    if (e instanceof BusinessException be) {
                        return Mono.just(ApiResult.<Void>failResult(be.getCode(), be.getMessage()));
                    }
                    return Mono.just(ApiResult.<Void>failResult("退出登录失败", null));
                });
    }



}
