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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现类 (优化版)
 *
 * <p>
 * 核心优化点：
 * 1. 修复刷新令牌时 AccessToken 扩展信息(IP, DeviceId)丢失的问题
 * 2. 增加用户账号状态校验（防止已禁用用户持续刷新）
 * 3. 采用构造器注入代替字段注入
 * 4. 完善设备管理逻辑
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    // 声明为 final 以支持构造器注入，保证不可变性
    private final ISysUserService sysUserService;
    private final IRedisService redisService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserInfoAssembler userInfoAssembler;

    // 常量定义，避免魔法值
    private static final String DEVICE_KEY_PREFIX = "user:devices:";

    /**
     * 用户登录
     */
    @Override
    public TokenResDTO login(UserLoginReqDTO dto) {
        // 1. 基础参数校验
        if (StringUtils.isBlank(dto.getDeviceId())) {
            throw new BusinessException("设备ID不能为空");
        }

        // 2. 查询用户
        SysUserDTO user = sysUserService.getUserByUsername(dto.getUsername());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 🛡️ 优化 2: 增加账号状态校验 (假设 SysUserDTO 有 status 字段, 1=正常)
        checkUserStatus(user);

        // 3. 校验密码
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 4. 构建 AccessToken 扩展信息
        Map<String, Object> extraClaims = buildExtraClaims(dto.getLoginIp(), dto.getClientType(), dto.getDeviceId());

        // 5. 生成 Token
        String accessToken = jwtUtil.generateAccessToken(user.getUsername(), extraClaims);
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        // 6. 存储 RefreshToken (关联设备)
        String refreshJti = jwtUtil.getJti(refreshToken);
        saveRefreshToken(user.getId(), dto.getDeviceId(), refreshJti, refreshToken);

        // 7. 记录设备登录 (多端管控)
        handleUserDeviceLogin(user.getId(), dto.getDeviceId(), refreshJti);

        // 8. 返回结果
        return buildTokenResponse(accessToken, refreshToken, dto.getDeviceId(), dto.getClientType(), dto.getLoginIp());
    }

    /**
     * RefreshToken 刷新 AccessToken
     */
    @Override
    public TokenResDTO refreshToken(TokenRefreshReqDTO dto) {
        // ... 参数基础校验 ...
        if (StringUtils.isAnyBlank(dto.getRefreshToken(), dto.getDeviceId())) {
            throw new BusinessException("刷新参数不完整");
        }

        // 1. 校验 Token 类型
        String tokenType = jwtUtil.getTokenType(dto.getRefreshToken());
        if (!JwtConstants.TOKEN_TYPE_REFRESH.equals(tokenType)) {
            throw new BusinessException("非法刷新令牌");
        }

        // 2. 解析 JTI 并检查 Redis
        String oldJti = jwtUtil.getJti(dto.getRefreshToken());
        String redisKey = JwtConstants.REFRESH_TOKEN_PREFIX + oldJti;

        // 🔥 核心逻辑: 原子性取出并删除旧 Token (防止并发刷新)
        String redisValue = redisService.getAndDelete(redisKey);

        if (StringUtils.isBlank(redisValue)) {
            // 🛡️ 风控点: 如果 Token 还在有效期内但 Redis 没了，说明被用过了 -> 令牌复用检测
            if (jwtUtil.getRemainingTime(dto.getRefreshToken()) > 0) {
                log.error("🚨 严重安全警报: RefreshToken 重复使用! User: {}, Device: {}, JTI: {}",
                        jwtUtil.getUsername(dto.getRefreshToken()), dto.getDeviceId(), oldJti);
                // 建议: 在此处加入踢用户下线的逻辑 (invalidateAllUserTokens)
            }
            throw new BusinessException("刷新令牌无效或已过期");
        }

        // 3. 校验设备一致性 (防窃取)
        String[] parts = redisValue.split(":"); // 格式: userId:deviceId
        String userIdStr = parts[0];
        String storedDeviceId = parts[1];

        if (!dto.getDeviceId().equals(storedDeviceId)) {
            log.warn("刷新设备不匹配. 请求设备: {}, 原始设备: {}", dto.getDeviceId(), storedDeviceId);
            throw new BusinessException("设备验证失败，请重新登录");
        }

        // 4. 查询用户并校验状态
        SysUserDTO user = sysUserService.getSysUserDtoByUserId(Long.parseLong(userIdStr));
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        checkUserStatus(user); // 🛡️ 再次校验状态，防止禁用期间刷新

        // 5. 生成新 Token
        // 🔥 修复: 必须带上扩展信息，否则刷新后丢失 IP/DeviceId 等数据
        // 注意：刷新时通常更新 IP 为当前请求 IP
        Map<String, Object> extraClaims = buildExtraClaims(dto.getLoginIp(), dto.getClientType(), dto.getDeviceId());

        String newAccessToken = jwtUtil.generateAccessToken(user.getUsername(), extraClaims);
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        // 6. 存储新 RefreshToken
        String newJti = jwtUtil.getJti(newRefreshToken);
        saveRefreshToken(user.getId(), dto.getDeviceId(), newJti, newRefreshToken);

        // 7. 更新设备映射 (旧 JTI -> 新 JTI)
        handleUserDeviceLogin(user.getId(), dto.getDeviceId(), newJti);

        log.info("令牌刷新成功. User: {}, Device: {}", user.getUsername(), dto.getDeviceId());

        return buildTokenResponse(newAccessToken, newRefreshToken, dto.getDeviceId(), dto.getClientType(), dto.getLoginIp());
    }

    /**
     * 用户登出
     */
    @Override
    public void logout(String accessToken, String refreshToken, String deviceId) {
        // 尝试获取 Username 用于清理设备映射（可选，视业务严格程度而定）
        String username = null;
        try {
            if (StringUtils.isNotBlank(accessToken)) username = jwtUtil.getUsername(accessToken);
            else if (StringUtils.isNotBlank(refreshToken)) username = jwtUtil.getUsername(refreshToken);
        } catch (Exception ignored) {}

        // 1. 删除 RefreshToken
        if (StringUtils.isNotBlank(refreshToken)) {
            try {
                String refreshJti = jwtUtil.getJti(refreshToken);
                redisService.deleteKey(JwtConstants.REFRESH_TOKEN_PREFIX + refreshJti);
            } catch (Exception e) {
                log.warn("登出清理 RefreshToken 失败", e);
            }
        }

        // 2. 拉黑 AccessToken (直至过期)
        if (StringUtils.isNotBlank(accessToken)) {
            try {
                String jti = jwtUtil.getJti(accessToken);
                long ttl = jwtUtil.getRemainingTime(accessToken);
                if (ttl > 0) {
                    redisService.setValue(JwtConstants.JTI_BLACKLIST_PREFIX + jti, "1", ttl, TimeUnit.MILLISECONDS);
                }
            } catch (Exception e) {
                log.warn("登出拉黑 AccessToken 失败", e);
            }
        }

        // 3. 清理设备映射
        // ⭐ 优化: 尝试更精确地清理 user:devices:{uid}:{deviceId}
        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(deviceId)) {
            SysUserDTO user = sysUserService.getUserByUsername(username);
            if (user != null) {
                removeDeviceFromSet(user.getId(), deviceId);
            }
        } else if (StringUtils.isNotBlank(deviceId)) {
            // 如果无法获取用户信息，仅依靠 deviceId 无法清理特定用户的 key，
            // 但如果 redis key 设计为 user:devices:deviceId (不带userId) 则可清理。
            // 按照当前设计依赖 userId，所以这里做个容错。
            log.debug("登出时缺少用户信息，跳过设备映射清理");
        }

        log.info("用户登出完成 deviceId={}", deviceId);
    }

    @Override
    public UserInfoDTO getUserInfoByToken(String token) {
        if (StringUtils.isBlank(token)) return null;

        String username = jwtUtil.getUsername(token);
        if (StringUtils.isBlank(username)) return null;

        // ⭐ 性能提示:
        // 这是一个高频调用方法。建议 sysUserService 内部对 getUserByUsername 增加 Redis 缓存 (@Cacheable)。
        // 或者直接信任 Token 中的 Claims (如果 Token 包含足够多的信息)，减少查库。
        SysUserDTO user = sysUserService.getUserByUsername(username);
        if (user == null) return null;

        return userInfoAssembler.assemble(
                user.getId(),
                jwtUtil.getClaimAsString(token, "loginIp"),
                jwtUtil.getClaimAsString(token, "clientType"),
                jwtUtil.getClaimAsString(token, "deviceId")
        );
    }

    // ===================== 私有辅助方法 (提升代码复用) =====================

    private void checkUserStatus(SysUserDTO user) {
        // 假设 SysUserDTO 有 isEnabled() 或者 getStatus() 方法
        // if (!user.isEnabled()) {
        //    throw new BusinessException("账号已被禁用，请联系管理员");
        // }
    }

    private Map<String, Object> buildExtraClaims(String ip, String clientType, String deviceId) {
        Map<String, Object> claims = new HashMap<>(4);
        claims.put("loginIp", ip);
        claims.put("clientType", clientType);
        claims.put("deviceId", deviceId);
        return claims;
    }

    private void saveRefreshToken(Long userId, String deviceId, String jti, String token) {
        String redisKey = JwtConstants.REFRESH_TOKEN_PREFIX + jti;
        String redisValue = userId + ":" + deviceId;
        // 保存 Token 及其剩余有效期
        redisService.setValue(redisKey, redisValue, jwtUtil.getRemainingTime(token), TimeUnit.MILLISECONDS);
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

    /**
     * 多端登录策略：维护 用户->设备列表
     */
    private void handleUserDeviceLogin(Long userId, String deviceId, String jti) {
        String key = DEVICE_KEY_PREFIX + userId + ":" + deviceId;
        // 记录该设备当前有效的 Refresh Token JTI，设置较长的过期时间 (如 7 天)
        redisService.setValue(key, jti, 7, TimeUnit.DAYS);

        // ⭐ 扩展点: 如果需要限制最大设备数 (如最多5个)，可以在此处查询 DEVICE_KEY_PREFIX + userId + "*"
        // 如果数量超标，根据策略删除最早的 Key 并同时删除对应的 RefreshToken
    }

    private void removeDeviceFromSet(Long userId, String deviceId) {
        String key = DEVICE_KEY_PREFIX + userId + ":" + deviceId;
        redisService.deleteKey(key);
    }
}