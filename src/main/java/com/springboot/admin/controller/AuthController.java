package com.springboot.admin.controller;

import com.springboot.admin.annotation.Logable;
import com.springboot.admin.common.ApiResult;
import com.springboot.admin.common.ApiResultCode;
import com.springboot.admin.common.BusinessResultCode;
import com.springboot.admin.exception.BusinessException;
import com.springboot.admin.model.dto.CaptchaDTO;
import com.springboot.admin.model.dto.TokenRefreshReqDTO;
import com.springboot.admin.model.dto.TokenResDTO;
import com.springboot.admin.model.dto.UserLoginReqDTO;
import com.springboot.admin.model.dto.user.UserInfoDTO;
import com.springboot.admin.model.vo.menu.MetaVO;
import com.springboot.admin.model.vo.menu.SysMenuTreeVO;
import com.springboot.admin.service.IAuthService;
import com.springboot.admin.service.ICaptchaService;
import com.springboot.admin.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证管理", description = "用户登录、注册、Token刷新等认证相关接口")
@Slf4j
public class AuthController {
    @Autowired
    private IAuthService authService;
    @Autowired
    private ICaptchaService captchaService;
    @Operation(summary = "获取验证码", description = "登陆时使用验证码")
    @Logable(logRequest = true, logResponse = true)
    @GetMapping("/getCaptcha")
    public Mono<ApiResult<CaptchaDTO>> getCaptcha() {
        return captchaService.generateCaptcha()
                .map(ApiResult::successResult)
                .onErrorResume(e -> Mono.just(ApiResult.failResult(ApiResultCode.FAILED, "获取验证码失败")));
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户使用用户名密码和验证码登录系统")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<TokenResDTO>> login(@RequestBody UserLoginReqDTO dto) {
        return captchaService.validateCaptcha(dto.getCaptchaId(), dto.getCaptchaCode())
                .flatMap(valid -> {
                    if (!valid) {
                        return Mono.error(new BusinessException(BusinessResultCode.PARAM_INVALID.getCode(), "验证码错误或已过期"));
                    }
                    return authService.login(dto);
                })
                .map(ApiResult::successResult);
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
    public Mono<ApiResult<Void>> logout(@RequestBody TokenRefreshReqDTO dto) {
        String refreshToken = dto.getRefreshToken();


        return authService.logout(refreshToken)
                .thenReturn(ApiResult.<Void>successResult("退出登录成功", null))
                .onErrorResume(e -> {
                    // 如果是业务异常，返回对应错误码和消息
                    if (e instanceof BusinessException be) {
                        return Mono.just(ApiResult.<Void>failResult(be.getCode(), be.getMessage()));
                    }
                    return Mono.just(ApiResult.<Void>failResult("退出登录失败", null));
                });
    }


    @GetMapping("/userInfo")
    @Operation(summary = "获取用户信息", description = "登录后获取角色、权限、菜单")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<UserInfoDTO>> getUserInfo(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        String token = JwtUtil.extractBearerToken(authHeader);
        log.info("token={}", token.substring(0, 6) + "...");

        return authService.getUserInfoByToken(token)
                .map(ApiResult::successResult);
    }

    @GetMapping("/userInfoStep5")
    @Operation(summary = "测试用户信息 Step5", description = "返回完整菜单结构")
    public Mono<ApiResult<UserInfoDTO>> getUserInfoStep5() {
        SysMenuTreeVO menu = new SysMenuTreeVO();
        menu.setId(100L);
        menu.setMenuName("测试菜单");
        menu.setChildren(List.of()); // 空 children
        menu.setMeta(new MetaVO());

        UserInfoDTO dto = new UserInfoDTO();
        dto.setUserId(1L);
        dto.setUsername("testUser");
        dto.setNickname("测试用户");
        dto.setAvatar("https://example.com/avatar.png");
        dto.setRoles(new ArrayList<>());
        dto.setPermissions(new ArrayList<>());
        dto.setMenus(List.of(menu));
        return Mono.just(ApiResult.successResult(dto));
    }


}
