package com.springboot.admin.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.admin.common.ApiResult;
import com.springboot.admin.common.ApiResultCode;
import com.springboot.admin.common.BusinessResultCode;
import com.springboot.admin.common.IApiResult;
import com.springboot.admin.config.SecurityWhitelistProperties;
import com.springboot.admin.service.IRedisService;
import com.springboot.admin.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class JwtAuthWebFilter implements WebFilter {

    private final JwtUtil jwtUtil;
    private final IRedisService redisService;
    private final ReactiveUserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;
    private final SecurityWhitelistProperties  securityWhitelistProperties;

    public JwtAuthWebFilter(JwtUtil jwtUtil,
                            IRedisService redisService,
                            ReactiveUserDetailsService userDetailsService,
                            ObjectMapper objectMapper,SecurityWhitelistProperties  securityWhitelistProperties) {
        this.jwtUtil = jwtUtil;
        this.redisService = redisService;
        this.userDetailsService = userDetailsService;
        this.objectMapper = objectMapper;
        this.securityWhitelistProperties = securityWhitelistProperties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // ⚠️ 白名单路径直接放行
        if (isWhitelisted(path)) {
            return chain.filter(exchange);
        }
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return sendErrorResponse(exchange, HttpStatus.UNAUTHORIZED,
                    "缺少认证令牌", BusinessResultCode.TOKEN_INVALID);
        }

        String token = authHeader.substring(7);
        log.info("Authorization header: {}", token);

        return jwtUtil.isAccessTokenValid(token)
                .flatMap(valid -> {
                    if (!valid) {
                        return sendErrorResponse(exchange, HttpStatus.UNAUTHORIZED,
                                "认证令牌已过期或无效", BusinessResultCode.TOKEN_INVALID);
                    }

                    return jwtUtil.getUsername(token)
                            .flatMap(username -> {
                                if (username == null) {
                                    return sendErrorResponse(exchange, HttpStatus.UNAUTHORIZED,
                                            "认证令牌无效", BusinessResultCode.TOKEN_INVALID);
                                }

                                log.info("Token subject={}, exp={}", username,
                                        jwtUtil.parseToken(token).block().getExpiration());

                                return userDetailsService.findByUsername(username)
                                        .switchIfEmpty(Mono.error(new UsernameNotFoundException("用户不存在")))
                                        .flatMap(customDetails -> {
                                            if (!customDetails.isEnabled()) {
                                                return sendErrorResponse(exchange, HttpStatus.FORBIDDEN,
                                                        "用户账号已被禁用", BusinessResultCode.USER_DISABLED);
                                            }

                                            UsernamePasswordAuthenticationToken authToken =
                                                    new UsernamePasswordAuthenticationToken(customDetails, null, customDetails.getAuthorities());

                                            SecurityContextImpl context = new SecurityContextImpl(authToken);

                                            return chain.filter(exchange)
                                                    .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));
                                        })
                                        // 用户不存在 → 401
                                        .onErrorResume(UsernameNotFoundException.class, e ->
                                                sendErrorResponse(exchange, HttpStatus.UNAUTHORIZED,
                                                        "用户不存在", BusinessResultCode.USER_NOT_FOUND))
                                        // 系统错误 → 500
                                        .onErrorResume(Exception.class, e -> {
                                            log.error("加载用户信息时发生错误: {}", e.getMessage(), e);
                                            return sendErrorResponse(exchange, HttpStatus.INTERNAL_SERVER_ERROR,
                                                    "系统内部错误", ApiResultCode.INTERNAL_SERVER_ERROR);
                                        });
                            });
                });
    }



    /**
     * 响应式错误输出
     */
    private Mono<Void> sendErrorResponse(ServerWebExchange exchange, HttpStatus status,
                                         String message, IApiResult errorCode) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(
                new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8));

        ApiResult<Object> result = ApiResult.failResult(errorCode, message);

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(result);
            return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                    .bufferFactory().wrap(bytes)));
        } catch (Exception e) {
            log.error("写入错误响应失败: {}", e.getMessage(), e);
            return exchange.getResponse().setComplete();
        }
    }

    private boolean isWhitelisted(String path) {
        return securityWhitelistProperties.getWhitelist().stream().anyMatch(pattern -> {
            // 把 ** 转换成正则 .* 来匹配
            String regex = pattern.replace("**", ".*");
            return path.matches(regex);
        });
    }

}
