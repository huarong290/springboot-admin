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
import org.springframework.util.AntPathMatcher;

import java.nio.charset.StandardCharsets;

/**
 * JwtAuthWebFilter
 *
 * <p>自定义 WebFlux 过滤器，用于处理基于 JWT 的认证逻辑。</p>
 *
 * <p>主要职责：</p>
 * <ul>
 *   <li>拦截所有进入系统的 HTTP 请求</li>
 *   <li>判断请求路径是否在白名单中，如果是则直接放行</li>
 *   <li>如果不是白名单路径，则检查请求头中的 Authorization 是否包含合法的 JWT</li>
 *   <li>验证 JWT 是否有效，解析用户名并加载用户信息</li>
 *   <li>将认证信息写入 ReactiveSecurityContextHolder，以便后续安全上下文使用</li>
 *   <li>如果认证失败，返回统一的 JSON 错误响应</li>
 * </ul>
 *
 * <p>注意事项：</p>
 * <ul>
 *   <li>白名单路径通过 AntPathMatcher 匹配，支持精确路径和通配符</li>
 *   <li>错误响应统一封装为 ApiResult，保证前后端交互一致性</li>
 * </ul>
 */
@Slf4j
@Component
public class JwtAuthWebFilter implements WebFilter {

    private final JwtUtil jwtUtil;
    private final IRedisService redisService;
    private final ReactiveUserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;
    private final SecurityWhitelistProperties securityWhitelistProperties;

    // 使用 Spring 提供的 AntPathMatcher 来匹配路径，支持 /swagger-ui.html 和 /swagger-ui/** 等模式
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public JwtAuthWebFilter(JwtUtil jwtUtil,
                            IRedisService redisService,
                            ReactiveUserDetailsService userDetailsService,
                            ObjectMapper objectMapper,
                            SecurityWhitelistProperties securityWhitelistProperties) {
        this.jwtUtil = jwtUtil;
        this.redisService = redisService;
        this.userDetailsService = userDetailsService;
        this.objectMapper = objectMapper;
        this.securityWhitelistProperties = securityWhitelistProperties;
    }

    /**
     * 核心过滤逻辑
     *
     * @param exchange 当前请求上下文
     * @param chain    WebFilterChain，用于继续执行过滤器链
     * @return Mono<Void> 响应式处理结果
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // 1. 白名单路径直接放行
        if (isWhitelisted(path)) {
            return chain.filter(exchange);
        }

        // 2. 从请求头中获取 Authorization
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // 缺少认证令牌 → 返回 401
            return sendErrorResponse(exchange, HttpStatus.UNAUTHORIZED,
                    "缺少认证令牌", BusinessResultCode.TOKEN_INVALID);
        }

        String token = authHeader.substring(7);
        log.info("Authorization header: {}", token);

        // 3. 验证 JWT 是否有效
        return jwtUtil.isAccessTokenValid(token)
                .flatMap(valid -> {
                    if (!valid) {
                        return sendErrorResponse(exchange, HttpStatus.UNAUTHORIZED,
                                "认证令牌已过期或无效", BusinessResultCode.TOKEN_INVALID);
                    }

                    // 4. 解析用户名
                    return jwtUtil.getUsername(token)
                            .flatMap(username -> {
                                if (username == null) {
                                    return sendErrorResponse(exchange, HttpStatus.UNAUTHORIZED,
                                            "认证令牌无效", BusinessResultCode.TOKEN_INVALID);
                                }

                                log.info("Token subject={}, exp={}", username,
                                        jwtUtil.parseToken(token).block().getExpiration());

                                // 5. 加载用户信息
                                return userDetailsService.findByUsername(username)
                                        .switchIfEmpty(Mono.error(new UsernameNotFoundException("用户不存在")))
                                        .flatMap(customDetails -> {
                                            if (!customDetails.isEnabled()) {
                                                // 用户被禁用 → 返回 403
                                                return sendErrorResponse(exchange, HttpStatus.FORBIDDEN,
                                                        "用户账号已被禁用", BusinessResultCode.USER_DISABLED);
                                            }

                                            // 6. 构建认证对象并写入安全上下文
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
     * 统一错误响应输出
     *
     * @param exchange 当前请求上下文
     * @param status   HTTP 状态码
     * @param message  错误提示信息
     * @param errorCode 业务错误码
     * @return Mono<Void> 响应式处理结果
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

    /**
     * 判断请求路径是否在白名单中
     *
     * @param path 当前请求路径
     * @return true 表示在白名单中，false 表示需要认证
     */
    private boolean isWhitelisted(String path) {
        return securityWhitelistProperties.getWhitelist().stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }
}
