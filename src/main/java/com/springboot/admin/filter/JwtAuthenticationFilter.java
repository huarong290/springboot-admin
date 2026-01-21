package com.springboot.admin.filter;

import com.springboot.admin.constants.security.JwtConstants;
import com.springboot.admin.exception.JwtAuthenticationException;
import com.springboot.admin.service.IRedisService;
import com.springboot.admin.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final IRedisService redisService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = JwtUtil.extractBearerToken(request.getHeader(JwtConstants.JWT_HEADER));

        if (StringUtils.isNotBlank(token)) {
            // JwtUtil 内部已经会抛 JwtAuthenticationException
            Claims claims = jwtUtil.parseToken(token);

            // 1️⃣ 校验 token 类型
            if (!JwtConstants.TOKEN_TYPE_ACCESS.equals(claims.get(JwtConstants.CLAIM_TOKEN_TYPE, String.class))) {
                throw new JwtAuthenticationException("非法 Token 类型");
            }

            // 2️⃣ 校验是否过期
            if (claims.getExpiration().before(new Date())) {
                throw new JwtAuthenticationException("Token 已过期");
            }

            // 3️⃣ 校验 jti 是否拉黑
            String jti = claims.get("jti", String.class);
            if (redisService.hasKey(JwtConstants.JTI_BLACKLIST_PREFIX + jti)) {
                throw new JwtAuthenticationException("Token 已失效");
            }

            // 4️⃣ 设置 SecurityContext
            String username = claims.getSubject();
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            Collections.emptyList()
                    );
            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
