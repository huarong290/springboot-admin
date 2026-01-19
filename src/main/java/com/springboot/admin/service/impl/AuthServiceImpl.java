package com.springboot.admin.service.impl;

import com.springboot.admin.constants.CommonConstants;
import com.springboot.admin.constants.security.JwtConstants;
import com.springboot.admin.mapper.auto.SysPermissionMapper;
import com.springboot.admin.mapper.auto.SysRoleMapper;
import com.springboot.admin.model.dto.TokenRefreshReqDTO;
import com.springboot.admin.model.dto.TokenResDTO;
import com.springboot.admin.model.dto.UserLoginReqDTO;
import com.springboot.admin.model.dto.user.SysUserDTO;
import com.springboot.admin.model.dto.user.UserInfoDTO;
import com.springboot.admin.model.vo.menu.SysMenuTreeVO;
import com.springboot.admin.model.vo.permission.SysPermissionVO;
import com.springboot.admin.model.vo.role.SysRoleVO;
import com.springboot.admin.service.IAuthService;
import com.springboot.admin.service.IRedisService;
import com.springboot.admin.service.ISysUserService;
import com.springboot.admin.utils.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 用户认证与授权服务实现类
 * <p>
 * - 登录校验用户名/密码
 * - 生成 JWT 访问令牌和刷新令牌
 * - 支持刷新令牌刷新
 * - 支持登出
 * - 获取用户信息，包括角色、权限、菜单树
 */
@Slf4j
@Service
@AllArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final ISysUserService iSysUserService;
    private final SysRoleMapper roleMapper;
    private final SysPermissionMapper permissionMapper;
    private final IRedisService redisService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    /**
     * 用户登录
     */
    @Override
    public TokenResDTO login(UserLoginReqDTO dto) {
        // 1. 根据用户名查询用户
        SysUserDTO user = iSysUserService.getUserByUsername(dto.getUsername());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 2. 校验密码
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 3. 生成 JWT 访问令牌
        String accessToken = jwtUtil.generateAccessToken(user.getUsername(), null);
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        // 3. 解析 refreshToken 的 jti
        String refreshJti = jwtUtil.getJti(refreshToken);


        // 4. refreshToken jti 存 Redis（防重放核心）
        String redisKey = JwtConstants.REFRESH_TOKEN_PREFIX + refreshToken;
        redisService.setValue(
                redisKey,
                user.getId().toString(),
                jwtUtil.getRemainingTime(refreshToken),
                TimeUnit.MILLISECONDS
        );

        TokenResDTO tokenRes = new TokenResDTO();
        tokenRes.setAccessToken(accessToken);
        tokenRes.setRefreshToken(refreshToken);
        tokenRes.setTokenType("Bearer");
        tokenRes.setExpiresIn(jwtUtil.getRemainingTime(accessToken) / 1000);

        log.info("用户登录成功 username={}, userId={}", user.getUsername(), user.getId());
        return tokenRes;
    }

    /**
     * 刷新令牌
     */
    @Override
    public TokenResDTO refreshToken(TokenRefreshReqDTO dto) {
        if (StringUtils.isBlank(dto.getRefreshToken())) {
            throw new RuntimeException("刷新令牌不能为空");
        }

        String redisKey = JwtConstants.REFRESH_TOKEN_PREFIX + dto.getRefreshToken();
        String userId = redisService.getValue(redisKey);
        if (StringUtils.isBlank(userId)) {
            throw new RuntimeException("刷新令牌无效或已过期");
        }

        // 1. 根据用户 ID 查询用户名
        var user = iSysUserService.getById(Long.parseLong(userId));
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 2. 生成新的访问令牌
        String accessToken = jwtUtil.generateAccessToken(user.getUsername(), null);

        // 3. 可选：刷新刷新令牌（此处不刷新）
        TokenResDTO tokenRes = new TokenResDTO();
        tokenRes.setAccessToken(accessToken);
        tokenRes.setRefreshToken(dto.getRefreshToken());
        tokenRes.setTokenType("Bearer");
        tokenRes.setExpiresIn(jwtUtil.getAccessTokenExpireSeconds());

        log.info("刷新令牌成功: userId={}", userId);
        return tokenRes;
    }

    /**
     * 用户登出
     */
    @Override
    public void logout(String accessToken, String refreshToken) {
        // 1. 删除 refreshToken
        if (StringUtils.isNotBlank(refreshToken)) {
            redisService.deleteKey(
                    JwtConstants.REFRESH_TOKEN_PREFIX + refreshToken
            );
        }

        // 2. 拉黑 accessToken 的 jti（防重放）
        if (StringUtils.isNotBlank(accessToken)) {
            String jti = jwtUtil.getJti(accessToken);
            long ttl = jwtUtil.getRemainingTime(accessToken);

            if (ttl > 0) {
                redisService.setValue(
                        JwtConstants.JTI_BLACKLIST_PREFIX + jti,
                        "1",
                        ttl,
                        TimeUnit.MILLISECONDS
                );
            }
        }

        log.info("用户登出成功");
    }


    /**
     * 根据 token 获取用户信息
     */
    @Override
    public UserInfoDTO getUserInfoByToken(String token) {
        if (StringUtils.isBlank(token)) return null;

        Long userId = jwtTokenUtil.getUserIdFromToken(token);
        if (userId == null) return null;

        var user = userMapper.selectById(userId);
        if (user == null) return null;

        UserInfoDTO dto = new UserInfoDTO();
        dto.setUserId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setNickname(user.getNickname());
        dto.setAvatar(user.getAvatar());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setStatus(user.getUserStatus());
        dto.setTenantId(user.getTenantId());

        // 1. 查询角色列表
        List<SysRoleVO> roles = roleMapper.selectRolesByUserId(userId);
        dto.setRoles(roles);

        // 2. 查询权限列表
        List<SysPermissionVO> permissions = permissionMapper.selectPermissionsByUserId(userId);
        dto.setPermissions(permissions);

        // 3. 查询菜单树
        List<SysMenuTreeVO> menus = permissionMapper.selectMenusByUserId(userId);
        dto.setMenus(menus);

        return dto;
    }
}
