package com.springboot.admin.service.impl;

import com.springboot.admin.constants.CommonConstants;
import com.springboot.admin.mapper.auto.SysUserMapper;
import com.springboot.admin.mapper.auto.SysRoleMapper;
import com.springboot.admin.mapper.auto.SysPermissionMapper;
import com.springboot.admin.model.dto.TokenRefreshReqDTO;
import com.springboot.admin.model.dto.TokenResDTO;
import com.springboot.admin.model.dto.UserLoginReqDTO;
import com.springboot.admin.model.dto.user.UserInfoDTO;
import com.springboot.admin.model.vo.menu.SysMenuTreeVO;
import com.springboot.admin.model.vo.permission.SysPermissionVO;
import com.springboot.admin.model.vo.role.SysRoleVO;
import com.springboot.admin.service.IAuthService;
import com.springboot.admin.service.IRedisService;
import com.springboot.admin.util.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
public class AuthServiceImpl implements IAuthService {

    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final SysPermissionMapper permissionMapper;
    private final IRedisService redisService;
    private final JwtTokenUtil jwtTokenUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthServiceImpl(SysUserMapper userMapper,
                           SysRoleMapper roleMapper,
                           SysPermissionMapper permissionMapper,
                           IRedisService redisService,
                           JwtTokenUtil jwtTokenUtil,
                           BCryptPasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.permissionMapper = permissionMapper;
        this.redisService = redisService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 用户登录
     */
    @Override
    public TokenResDTO login(UserLoginReqDTO dto) {
        // 1. 根据用户名查询用户
        var user = userMapper.selectByUsername(dto.getUsername());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 2. 校验密码
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 3. 生成 JWT 访问令牌
        String accessToken = jwtTokenUtil.generateAccessToken(user.getId(), user.getUsername());

        // 4. 生成刷新令牌
        String refreshToken = jwtTokenUtil.generateRefreshToken(user.getId(), user.getUsername());

        // 5. 保存刷新令牌到 Redis
        redisService.setValue(
                CommonConstants.JWT_BEARER_PREFIX + refreshToken,
                user.getId().toString(),
                jwtTokenUtil.getRefreshTokenExpireMinutes(),
                TimeUnit.MINUTES
        );

        TokenResDTO tokenRes = new TokenResDTO();
        tokenRes.setAccessToken(accessToken);
        tokenRes.setRefreshToken(refreshToken);
        tokenRes.setTokenType("Bearer");
        tokenRes.setExpiresIn(jwtTokenUtil.getAccessTokenExpireSeconds());

        log.info("用户登录成功: username={}", user.getUsername());
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

        String redisKey = CommonConstants.JWT_BEARER_PREFIX + dto.getRefreshToken();
        String userId = redisService.getValue(redisKey);
        if (StringUtils.isBlank(userId)) {
            throw new RuntimeException("刷新令牌无效或已过期");
        }

        // 1. 根据用户 ID 查询用户名
        var user = userMapper.selectById(Long.parseLong(userId));
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 2. 生成新的访问令牌
        String accessToken = jwtTokenUtil.generateAccessToken(user.getId(), user.getUsername());

        // 3. 可选：刷新刷新令牌（此处不刷新）
        TokenResDTO tokenRes = new TokenResDTO();
        tokenRes.setAccessToken(accessToken);
        tokenRes.setRefreshToken(dto.getRefreshToken());
        tokenRes.setTokenType("Bearer");
        tokenRes.setExpiresIn(jwtTokenUtil.getAccessTokenExpireSeconds());

        log.info("刷新令牌成功: userId={}", userId);
        return tokenRes;
    }

    /**
     * 用户登出
     */
    @Override
    public void logout(String refreshToken) {
        if (StringUtils.isBlank(refreshToken)) return;

        String redisKey = CommonConstants.JWT_BEARER_PREFIX + refreshToken;
        redisService.deleteKey(redisKey);

        log.info("用户登出成功, refreshToken={}", refreshToken);
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
