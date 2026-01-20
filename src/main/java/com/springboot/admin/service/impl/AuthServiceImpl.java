package com.springboot.admin.service.impl;

import com.springboot.admin.assemble.UserInfoAssembler;
import com.springboot.admin.constants.security.JwtConstants;
import com.springboot.admin.exception.BusinessException;
import com.springboot.admin.model.dto.TokenRefreshReqDTO;
import com.springboot.admin.model.dto.TokenResDTO;
import com.springboot.admin.model.dto.UserLoginReqDTO;
import com.springboot.admin.model.dto.user.SysUserDTO;
import com.springboot.admin.model.dto.user.UserInfoDTO;
import com.springboot.admin.service.*;
import com.springboot.admin.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AuthServiceImpl implements IAuthService {

    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private ISysRoleService sysRoleService;
    @Autowired
    private ISysPermissionService sysPermissionService;
    @Autowired
    private ISysMenuService sysMenuService;
    @Autowired
    private IRedisService redisService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserInfoAssembler userInfoAssembler;

    @Override
    public TokenResDTO login(UserLoginReqDTO dto) {
        // 1. 校验必填字段
        if (StringUtils.isBlank(dto.getDeviceId())) {
            throw new BusinessException("设备ID不能为空");
        }

        // 2. 查询用户
        SysUserDTO user = sysUserService.getUserByUsername(dto.getUsername());
        if (user == null) throw new BusinessException("用户不存在");

        // 3. 校验密码
        log.info("password:{}", passwordEncoder.encode(dto.getPassword()));
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("密码错误");
        }

        // 4. 生成 JWT
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("loginIp", dto.getLoginIp());
        extraClaims.put("clientType", dto.getClientType());
        extraClaims.put("deviceId", dto.getDeviceId());
        String accessToken = jwtUtil.generateAccessToken(user.getUsername(), null);
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        // 5. refreshToken 存 Redis，绑定设备ID
        String redisKey = JwtConstants.REFRESH_TOKEN_PREFIX + refreshToken;
        // Redis 存储格式: userId:deviceId
        String redisValue = user.getId() + ":" + dto.getDeviceId();
        redisService.setValue(
                redisKey,
                redisValue,
                jwtUtil.getRemainingTime(refreshToken),
                TimeUnit.MILLISECONDS
        );

        // 6. 返回结果
        TokenResDTO tokenRes = new TokenResDTO();
        tokenRes.setAccessToken(accessToken);
        tokenRes.setRefreshToken(refreshToken);
        tokenRes.setTokenType("Bearer");
        tokenRes.setIp(dto.getLoginIp());
        tokenRes.setDeviceId(dto.getDeviceId());
        tokenRes.setClientType(dto.getClientType());
        tokenRes.setExpiresIn(jwtUtil.getRemainingTime(accessToken));
        tokenRes.setRefreshExpiresIn(jwtUtil.getRemainingTime(refreshToken));
        log.info("用户登录成功 username={}, userId={}, deviceId={}", user.getUsername(), user.getId(), dto.getDeviceId());
        return tokenRes;
    }



    @Override
    public TokenResDTO refreshToken(TokenRefreshReqDTO dto) {
        if (StringUtils.isBlank(dto.getRefreshToken())) {
            throw new BusinessException("刷新令牌不能为空");
        }

        if (StringUtils.isBlank(dto.getDeviceId())) {
            throw new BusinessException("设备ID不能为空");
        }

        // 1. 从 Redis 获取 refreshToken 信息
        String redisKey = JwtConstants.REFRESH_TOKEN_PREFIX + dto.getRefreshToken();
        String redisValue = redisService.getValue(redisKey);
        if (StringUtils.isBlank(redisValue)) {
            throw new BusinessException("刷新令牌无效或已过期");
        }

        // 2. Redis 中存储格式: userId:deviceId
        String[] parts = redisValue.split(":");
        if (parts.length != 2) {
            throw new BusinessException("刷新令牌数据异常");
        }
        String userId = parts[0];
        String storedDeviceId = parts[1];

        // 3. 校验 deviceId 是否一致
        if (!dto.getDeviceId().equals(storedDeviceId)) {
            throw new BusinessException("刷新令牌与设备ID不匹配");
        }

        // 4. 查询用户信息
        SysUserDTO user = sysUserService.getSysUserDtoByUserId(Long.parseLong(userId));
        if (user == null) throw new BusinessException("用户不存在");

        // 5. 生成新的 AccessToken
        String accessToken = jwtUtil.generateAccessToken(user.getUsername(), null);

        // 6. 可选：刷新 refreshToken（此处不刷新）
        TokenResDTO tokenRes = new TokenResDTO();
        tokenRes.setAccessToken(accessToken);
        tokenRes.setRefreshToken(dto.getRefreshToken());
        tokenRes.setTokenType("Bearer");
        tokenRes.setExpiresIn(jwtUtil.getRemainingTime(accessToken));

        log.info("刷新令牌成功: userId={}, deviceId={}", userId, dto.getDeviceId());
        return tokenRes;
    }

    /**
     * 用户登出（支持单设备登出）
     */
    @Override
    public void logout(String accessToken, String refreshToken, String deviceId) {
        // 1️⃣ 删除 refreshToken
        if (StringUtils.isNotBlank(refreshToken)) {
            String redisKey = JwtConstants.REFRESH_TOKEN_PREFIX + refreshToken;
            String redisValue = redisService.getValue(redisKey);

            if (StringUtils.isNotBlank(redisValue)) {
                String[] parts = redisValue.split(":");
                if (parts.length == 2) {
                    String storedDeviceId = parts[1];
                    if (deviceId == null || deviceId.equals(storedDeviceId)) {
                        redisService.deleteKey(redisKey);
                        log.info("删除 refreshToken 成功: deviceId={}, refreshToken={}", storedDeviceId, refreshToken);
                    } else {
                        log.warn("refreshToken 所属设备与请求设备不匹配 deviceId={}，storedDeviceId={}", deviceId, storedDeviceId);
                    }
                }
            }
        }

        // 2️⃣ 拉黑 AccessToken 的 jti
        if (StringUtils.isNotBlank(accessToken)) {
            try {
                String jti = jwtUtil.getJti(accessToken);
                long ttl = jwtUtil.getRemainingTime(accessToken);
                if (ttl > 0) {
                    redisService.setValue(JwtConstants.JTI_BLACKLIST_PREFIX + jti, "1", ttl, TimeUnit.MILLISECONDS);
                    log.info("AccessToken 拉黑成功, jti={}", jti);
                }
            } catch (Exception e) {
                log.warn("拉黑 AccessToken 失败: {}", e.getMessage());
            }
        }

        log.info("用户登出成功, deviceId={}", deviceId);
    }


    /**
     * 根据 JWT Token 获取当前用户完整信息
     *
     * @param token JWT AccessToken
     * @return UserInfoDTO
     */
    @Override
    public UserInfoDTO getUserInfoByToken(String token) {
        // 1️⃣ 校验 token
        if (StringUtils.isBlank(token)) {
            return null;
        }

        // 2️⃣ 从 token 中解析用户名
        String username = jwtUtil.getUsername(token);
        if (StringUtils.isBlank(username)) {
            return null;
        }

        // 3️⃣ 根据用户名查询用户基础信息（SysUserDTO）
        SysUserDTO user = sysUserService.getUserByUsername(username);
        if (user == null) {
            return null;
        }

        // 4️⃣ 调用装配器生成完整 DTO
        // 装配器内部会处理：
        // - 用户基础信息
        // - 角色列表
        // - 权限码列表
        // - 菜单树
        // - 数据权限
        return userInfoAssembler.assemble(
                user.getId(),
                jwtUtil.getClaimAsString(token, "loginIp"),     // 可从 token 或请求上下文获取
                jwtUtil.getClaimAsString(token, "clientType"),  // token 内存储或默认 WEB
                jwtUtil.getClaimAsString(token, "deviceId")     // token 内存储或客户端传递
        );
    }

}
