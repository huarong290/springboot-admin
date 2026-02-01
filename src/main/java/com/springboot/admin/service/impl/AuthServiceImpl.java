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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务核心实现类 (安全增强版)
 * <p>
 * 核心安全策略：
 * 1. 令牌轮转 (Refresh Token Rotation): 每次刷新都更换新的 Refresh Token。
 * 2. 令牌复用检测 (Reuse Detection): 检测到旧令牌被重复使用，视为盗号，强制下线所有端。
 * 3. 故障阻断 (Fail-Secure): Redis 写入失败时抛出异常，不允许“幽灵登录”。
 * 4. 设备绑定: Token 与 deviceId 绑定，防止异地窃取 Token 使用。
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    // 依赖组件 (构造器注入)
    private final ISysUserService sysUserService;
    private final IRedisService redisService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserInfoAssembler userInfoAssembler; // 用于 DTO 转换

    // 注入配置：Refresh Token 有效期 (默认 7 天)
    @Value("${jwt.refresh-token.validity-seconds:604800}")
    private long refreshTokenValiditySeconds;

    // Redis Key 前缀：用户设备映射 (user:devices:uid:deviceId)
    private static final String DEVICE_KEY_PREFIX = "user:devices:";

    /**
     * 用户登录流程
     */
    @Override
    public TokenResDTO login(UserLoginReqDTO dto) {
        // 1. 基础校验 (验证码校验逻辑通常由 Filter 或 AOP 处理，此处略)

        // 2. 查询用户
        SysUserDTO user = sysUserService.getUserDTOByUsername(dto.getUsername());
        if (user == null) {
            // 模糊提示，防止枚举账号攻击
            throw new BusinessException("用户名或密码错误");
        }

        // 3. 校验账号状态 (禁用/锁定)
        checkUserStatus(user);

        // 4. 校验密码
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 5. 生成 Token
        // 必须将 deviceId 等环境信息写入 Token，保证由 Token 可溯源
        Map<String, Object> extraClaims = buildExtraClaims(dto.getLoginIp(), dto.getClientInfo().getClientType(), dto.getClientInfo().getDeviceId());
        String accessToken = jwtUtil.generateAccessToken(user.getUsername(), extraClaims);
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        // 6. 存储 RefreshToken 到 Redis
        // JTI (JWT ID) 是 Refresh Token 的唯一标识
        String refreshJti = jwtUtil.getJti(refreshToken);

        // 🛑 核心安全点：Fail-Secure 写入。若 Redis 挂了，必须抛出异常，禁止登录。
        saveRefreshTokenOrThrow(user.getId(), dto.getClientInfo().getDeviceId(), refreshJti, refreshToken);

        // 7. 维护用户设备映射 (用于踢人、设备管理)
        handleUserDeviceLogin(user.getId(), dto.getClientInfo().getDeviceId(), refreshJti);

        // 8. 返回结果
        return buildTokenResponse(accessToken, refreshToken, dto.getClientInfo().getDeviceId(), dto.getClientInfo().getClientType(), dto.getLoginIp());
    }

    /**
     * 令牌刷新流程 (Refresh Token Rotation)
     */
    @Override
    public TokenResDTO refreshToken(TokenRefreshReqDTO dto) {
        String oldRefreshToken = dto.getRefreshToken();
        String deviceId = dto.getDeviceId();

        // 1. 令牌格式与类型校验
        if (StringUtils.isBlank(oldRefreshToken) || !JwtConstants.TOKEN_TYPE_REFRESH.equals(jwtUtil.getTokenType(oldRefreshToken))) {
            throw new BusinessException("非法刷新令牌");
        }

        String oldJti = jwtUtil.getJti(oldRefreshToken);
        String redisKey = JwtConstants.REFRESH_TOKEN_PREFIX + oldJti;

        // 🔥 2. 原子性获取并删除 (Anti-Concurrency / One-Time Use)
        // 使用 Lua 脚本保证 GET 和 DEL 是原子操作
        String redisValue = redisService.getAndDelete(redisKey);

        // 🚨 3. 令牌复用检测 (Token Reuse Detection) - 极高危场景
        if (StringUtils.isBlank(redisValue)) {
            // 如果 Token 签名有效且未过期，但 Redis 中已不存在 -> 说明该 Token 之前已经被用过了
            // 这意味着：黑客偷了 Token 并在用户之前（或之后）尝试刷新。
            if (jwtUtil.getRemainingTime(oldRefreshToken) > 0) {
                String username = jwtUtil.getUsername(oldRefreshToken);
                log.error("🚨 [严重安全警报] 令牌复用检测触发! User: {}, Device: {}, JTI: {}", username, deviceId, oldJti);

                // 🛡️ 响应措施：宁杀错不放过，强制该用户所有设备下线，迫使重新修改密码
                SysUserDTO user = sysUserService.getUserDTOByUsername(username);
                if (user != null) {
                    invalidateAllUserTokens(user.getId());
                }
            }
            throw new BusinessException("会话已失效或存在安全风险，请重新登录");
        }

        // 4. 解析 Redis 数据 (userId:deviceId) 并校验设备指纹
        // 防止黑客偷了 Token 在另一台设备上刷新
        String[] parts = redisValue.split(":");
        long userId = Long.parseLong(parts[0]);
        String storedDeviceId = parts[1];

        if (!deviceId.equals(storedDeviceId)) {
            log.warn("设备指纹不匹配! ReqDevice: {}, StoredDevice: {}", deviceId, storedDeviceId);
            throw new BusinessException("环境异常，请重新登录");
        }

        // 5. 校验用户状态
        SysUserDTO user = sysUserService.getSysUserDtoByUserId(userId);
        if (user == null) throw new BusinessException("用户不存在");
        checkUserStatus(user);

        // 6. 生成全新的一对 Token (Rotation)
        Map<String, Object> extraClaims = buildExtraClaims(dto.getLoginIp(), dto.getClientType(), deviceId);
        String newAccessToken = jwtUtil.generateAccessToken(user.getUsername(), extraClaims);
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        String newJti = jwtUtil.getJti(newRefreshToken);

        // 7. 保存新 Token，更新设备映射
        saveRefreshTokenOrThrow(userId, deviceId, newJti, newRefreshToken);
        handleUserDeviceLogin(userId, deviceId, newJti);

        log.info("令牌刷新成功. User: {}, Device: {}", user.getUsername(), deviceId);

        return buildTokenResponse(newAccessToken, newRefreshToken, deviceId, dto.getClientType(), dto.getLoginIp());
    }

    /**
     * 用户登出
     */
    @Override
    public void logout(String accessToken, String refreshToken, String deviceId) {
        // 尝试从 Token 中解析用户名 (用于清理设备 Key)
        String username = null;
        try {
            if (StringUtils.isNotBlank(accessToken)) username = jwtUtil.getUsername(accessToken);
        } catch (Exception ignored) {}

        // 1. 将 AccessToken 加入黑名单 (直至过期)
        // 因为 AccessToken 是无状态的，无法物理删除，只能通过黑名单拦截
        if (StringUtils.isNotBlank(accessToken)) {
            try {
                String jti = jwtUtil.getJti(accessToken);
                long ttl = jwtUtil.getRemainingTime(accessToken);
                if (ttl > 0) {
                    redisService.setValue(JwtConstants.JTI_BLACKLIST_PREFIX + jti, "1", ttl, TimeUnit.MILLISECONDS);
                }
            } catch (Exception e) {
                log.warn("登出失败: AccessToken 黑名单写入异常", e);
            }
        }

        // 2. 物理删除 RefreshToken
        if (StringUtils.isNotBlank(refreshToken)) {
            try {
                String refreshJti = jwtUtil.getJti(refreshToken);
                redisService.deleteKey(JwtConstants.REFRESH_TOKEN_PREFIX + refreshJti);
            } catch (Exception ignored) {}
        }

        // 3. 清理设备绑定关系
        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(deviceId)) {
            SysUserDTO user = sysUserService.getUserDTOByUsername(username);
            if (user != null) {
                redisService.deleteKey(DEVICE_KEY_PREFIX + user.getId() + ":" + deviceId);
            }
        }
    }

    @Override
    public UserInfoDTO getUserInfoByToken(String token) {
        if (StringUtils.isBlank(token)) return null;
        String username = jwtUtil.getUsername(token);
        if (StringUtils.isBlank(username)) return null;

        // 建议：此处 sysUserService 内部应增加缓存，避免高频查库
        SysUserDTO user = sysUserService.getUserDTOByUsername(username);
        if (user == null) return null;

        return userInfoAssembler.assemble(
                user.getId(),
                jwtUtil.getClaimAsString(token, "loginIp"),
                jwtUtil.getClaimAsString(token, "clientType"),
                jwtUtil.getClaimAsString(token, "deviceId")
        );
    }

    // ==========================================
    // 私有辅助方法
    // ==========================================

    /**
     * 强校验保存 Refresh Token (Fail-Secure)
     * 如果 Redis 写入失败，必须阻断流程，否则会导致“幽灵Token”
     */
    private void saveRefreshTokenOrThrow(Long userId, String deviceId, String jti, String token) {
        String redisKey = JwtConstants.REFRESH_TOKEN_PREFIX + jti;
        // Redis Value 格式: "userId:deviceId"
        String redisValue = userId + ":" + deviceId;

        // 保持 Redis 过期时间与 Token 声明的过期时间一致
        boolean success = redisService.setValue(redisKey, redisValue, jwtUtil.getRemainingTime(token), TimeUnit.MILLISECONDS);

        if (!success) {
            log.error("Redis 写入失败，阻断登录。Key: {}", redisKey);
            throw new BusinessException("系统繁忙，会话创建失败");
        }
    }

    /**
     * 维护设备登录状态
     * <p>
     * 记录 user:devices:{uid}:{deviceId} -> {current_refresh_jti}
     * 用于确保同一设备同一时间只有一个有效的 Refresh Token，也便于实现“踢人下线”。
     * </p>
     */
    private void handleUserDeviceLogin(Long userId, String deviceId, String jti) {
        String key = DEVICE_KEY_PREFIX + userId + ":" + deviceId;
        // 此 Key 有效期应 >= Refresh Token 有效期
        redisService.setValue(key, jti, refreshTokenValiditySeconds, TimeUnit.SECONDS);
    }

    /**
     * 🚨 强制下线用户所有设备
     * <p>
     * 场景：检测到令牌复用攻击、用户修改密码、管理员封号。
     * </p>
     */
    private void invalidateAllUserTokens(Long userId) {
        try {
            // 注意：生产环境如果 Key 数量巨大，使用 keys() 可能会阻塞 Redis。
            // 建议 IRedisService 实现 scan() 方法，或使用 Set 集合维护用户的设备列表 (user:device_list:uid -> [dev1, dev2])
            String pattern = DEVICE_KEY_PREFIX + userId + ":*";
            Set<String> deviceKeys = redisService.scan(pattern);

            if (deviceKeys != null && !deviceKeys.isEmpty()) {
                for (String deviceKey : deviceKeys) {
                    // 1. 获取该设备当前指向的 Refresh Token JTI
                    String currentJti = redisService.getValue(deviceKey);

                    // 2. 删除 Refresh Token
                    if (StringUtils.isNotBlank(currentJti)) {
                        redisService.deleteKey(JwtConstants.REFRESH_TOKEN_PREFIX + currentJti);
                    }

                    // 3. 删除设备绑定 Key
                    redisService.deleteKey(deviceKey);
                }
            }
            log.warn("已强制踢下线用户: {}", userId);
        } catch (Exception e) {
            log.error("踢人逻辑执行失败", e);
        }
    }

    private void checkUserStatus(SysUserDTO user) {
        // 预留方法：检查 status == 0 或 isLocked
        // if (!user.isEnabled()) throw new BusinessException("账号已停用");
    }

    private Map<String, Object> buildExtraClaims(String ip, String clientType, String deviceId) {
        Map<String, Object> claims = new HashMap<>(4);
        claims.put("loginIp", ip);
        claims.put("clientType", clientType);
        claims.put("deviceId", deviceId);
        return claims;
    }

    private TokenResDTO buildTokenResponse(String access, String refresh, String deviceId, String clientType, String ip) {
        TokenResDTO res = new TokenResDTO();
        res.setAccessToken(access);
        res.setRefreshToken(refresh);
        res.setTokenType("Bearer");
        res.setDeviceId(deviceId);
        res.setClientType(clientType);
        res.setIp(ip);
        res.setExpiresIn(jwtUtil.getRemainingTime(access));
        res.setRefreshExpiresIn(jwtUtil.getRemainingTime(refresh));
        return res;
    }
}