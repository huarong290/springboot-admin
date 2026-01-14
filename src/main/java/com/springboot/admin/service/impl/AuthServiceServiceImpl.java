package com.springboot.admin.service.impl;

import com.springboot.admin.common.BusinessResultCode;
import com.springboot.admin.config.JwtProperties;
import com.springboot.admin.exception.BusinessException;
import com.springboot.admin.model.dto.TokenRefreshReqDTO;
import com.springboot.admin.model.dto.TokenResDTO;
import com.springboot.admin.model.dto.UserLoginReqDTO;
import com.springboot.admin.model.dto.user.SysUserDTO;
import com.springboot.admin.model.dto.user.UserInfoDTO;
import com.springboot.admin.model.vo.menu.MetaVO;
import com.springboot.admin.model.vo.menu.SysMenuTreeVO;
import com.springboot.admin.model.vo.menu.SysMenuVO;
import com.springboot.admin.model.vo.permission.SysPermissionVO;
import com.springboot.admin.model.vo.role.SysRoleVO;
import com.springboot.admin.service.*;
import com.springboot.admin.utils.JwtUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AuthServiceServiceImpl implements IAuthService {

    @Resource
    private JwtUtil jwtUtil;
    @Resource
    private  JwtProperties jwtProperties;

    @Resource
    private IRedisService redisService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ISysUserService sysUserService;


    @Autowired
    private ISysRoleService sysRoleService;
    @Autowired
    private ISysPermissionService sysPermissionService;
    @Autowired
    private ISysMenuService sysMenuService;

    @Autowired
    private ICaptchaService captchaService;
    /**
     * 用户登录：验证密码，生成并存储 Access 和 Refresh Token
     *
     * @param dto 用户登录请求参数（用户名、密码）
     * @return Mono<TokenResDTO> 响应式单对象，包含 AccessToken 和 RefreshToken
     */
    @Override
    public Mono<TokenResDTO> login(UserLoginReqDTO dto) {
        return sysUserService.getUserByUsername(dto.getUsername())
                .switchIfEmpty(Mono.error(new BusinessException(BusinessResultCode.USER_NOT_FOUND)))
                .flatMap(user -> {
                    log.info("pass={}", passwordEncoder.encode(dto.getPassword()));
                    if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
                        return Mono.error(new BusinessException(BusinessResultCode.USER_PASSWORD_ERROR));
                    }

                    //  登录成功后更新 lastLoginTime
                    Mono<Long> updateLoginTimeMono = sysUserService.updateLastLoginTime(user.getId(), LocalDateTime.now());

                    Mono<String> accessTokenMono = jwtUtil.generateAccessToken(user.getUsername(), null);
                    Mono<String> refreshTokenMono = jwtUtil.generateRefreshToken(user.getUsername());

                    //  把更新登录时间放进链里，保证执行
                    return updateLoginTimeMono.then(
                            Mono.zip(accessTokenMono, refreshTokenMono)
                                    .flatMap(tuple -> {
                                        String accessToken = tuple.getT1();
                                        String refreshToken = tuple.getT2();
                                        // 删除验证码（可选失败不影响登录）
                                        Mono<Boolean> deleteCaptchaMono = Mono.empty();
                                        if (dto.getCaptchaId() != null) {
                                            deleteCaptchaMono = captchaService.deleteCaptchaReturnBoolean(dto.getCaptchaId())
                                                    .doOnNext(success -> {
                                                        if (success) {
                                                            log.info("验证码已删除: {}", dto.getCaptchaId());
                                                        } else {
                                                            log.warn("验证码删除失败: {}", dto.getCaptchaId());
                                                        }
                                                    })
                                                    .onErrorResume(e -> {
                                                        log.warn("删除验证码异常: {}", e.getMessage(), e);
                                                        return Mono.just(false);
                                                    });
                                        }
                                        // 存储 RefreshToken
                                        return redisService.storeRefreshToken(refreshToken, user.getUsername())
                                                .then(Mono.defer(() -> {
                                                    TokenResDTO tokenResDTO = new TokenResDTO();
                                                    tokenResDTO.setAccessToken(accessToken);
                                                    tokenResDTO.setRefreshToken(refreshToken);
                                                    tokenResDTO.setExpiresIn(jwtProperties.getAccessTokenExpiration() / 1000);
                                                    log.info("用户 {} 登录成功，生成 Token 并更新 lastLoginTime", dto.getUsername());
                                                    return Mono.just(tokenResDTO);
                                                }));
                                    })
                    );
                });
    }

    /**
     * 使用刷新令牌获取新的访问令牌
     */
    @Override
    public Mono<TokenResDTO> refreshToken(TokenRefreshReqDTO dto) {
        return redisService.isRefreshTokenStored(dto.getRefreshToken())
                .flatMap(exists -> {
                    if (!exists) {
                        log.warn("刷新令牌无效或已过期: {}", dto.getDeviceId());
                        return Mono.error(new RuntimeException("刷新令牌无效或已过期"));
                    }

                    // 从 Redis 获取用户名
                    return redisService.getValue("refresh:" + dto.getRefreshToken())
                            .flatMap(username -> {
                                // 生成新的 AccessToken
                                return jwtUtil.generateAccessToken(username, null)
                                        .flatMap(newAccessToken -> {
                                            TokenResDTO tokenResDTO = new TokenResDTO();
                                            tokenResDTO.setAccessToken(newAccessToken);
                                            tokenResDTO.setRefreshToken(dto.getRefreshToken()); // 保持原 RefreshToken
                                            tokenResDTO.setExpiresIn(jwtProperties.getAccessTokenExpiration() / 1000);
                                            tokenResDTO.setTokenType("Bearer");

                                            log.info("用户 {} 使用刷新令牌成功生成新的 AccessToken", username);
                                            return Mono.just(tokenResDTO);
                                        });
                            });
                });
    }

    @Override
    public Mono<Void> logout(String refreshToken) {
        return redisService.isRefreshTokenStored(refreshToken)
                .flatMap(exists -> {
                    if (!exists) {
                        // 抛出业务异常，交给全局异常处理器
                        return Mono.error(new BusinessException("0100103", "令牌已经失效"));
                    }
                    return redisService.removeRefreshToken(refreshToken)
                            .doOnSuccess(v -> log.info("刷新令牌 {} 已移除，用户登出成功", refreshToken));
                });
    }


    /**
     * 根据 Token 获取用户信息
     *
     * 用途：
     * - 前端调用 /userInfo 接口时，后端通过解析 Token 获取用户信息。
     * - 返回用户的基本信息、角色列表、权限列表、菜单树。
     *
     * 流程：
     * 1. 解析 Token，获取用户名。
     * 2. 查询用户基本信息。
     * 3. 查询用户角色列表。
     * 4. 查询用户权限列表（调用 listPermissionsByUserId）。
     * 5. 查询用户菜单树。
     * 6. 聚合结果，组装成 UserInfoDTO。
     *
     * @param token 前端传入的 JWT Token
     * @return Mono<UserInfoDTO> 响应式单对象，包含用户信息、角色、权限、菜单树
     */
    @Override
    public Mono<UserInfoDTO> getUserInfoByToken(String token) {
        return jwtUtil.parseToken(token)
                .flatMap(claims -> {
                    String username = claims.getSubject();
                    log.info("[Step1] 成功解析 Token，username={}", username);

                    return sysUserService.getUserByUsername(username)
                            .switchIfEmpty(Mono.error(new BusinessException("0100104", "用户不存在")))
                            .flatMap(user -> {
                                log.info("[Step2] 查询到用户信息: {}", user.getUsername());

                                Mono<List<SysRoleVO>> rolesMono = sysRoleService.listRolesByUserId(user.getId())
                                        .collectList()
                                        .doOnNext(roles -> log.info("[Step3] 角色列表: {}", roles));

                                Mono<List<SysPermissionVO>> permissionsMono = sysPermissionService
                                        .listPermissionsByUserId(user.getId())
                                        .distinct()
                                        .collectList()
                                        .doOnNext(perms -> log.info("[Step4] 权限数量: {}", perms.size()));

                                Mono<List<SysMenuTreeVO>> menusMono = sysMenuService
                                        .getMenuTreeByUserId(user.getId()) // 直接返回树形结构
                                        .collectList()
                                        .doOnNext(menus -> log.info("[Step5] 菜单数量: {}", menus.size()));

                                return Mono.zip(rolesMono, permissionsMono, menusMono)
                                        .map(tuple -> {
                                            UserInfoDTO dto = new UserInfoDTO();
                                            dto.setUserId(user.getId());
                                            dto.setUsername(user.getUsername());
                                            dto.setNickname(user.getNickname());
                                            dto.setAvatar(user.getAvatar());
                                            dto.setRoles(tuple.getT1());
                                            dto.setPermissions(tuple.getT2());
                                            dto.setMenus(tuple.getT3());
                                            log.info("[Step6] UserInfoDTO 构建完成: {}", dto.getUsername());
                                            return dto;
                                        });
                            });
                })
                .onErrorResume(e -> {
                    log.error("获取用户信息失败: {}", e.getMessage(), e);
                    return Mono.error(new BusinessException("0100105", "获取用户信息失败"));
                });
    }



    /**
     * 构建 UserInfoDTO
     *
     * @param sysUserDTO 用户实体对象
     * @param roles 用户角色列表
     * @param permissions 用户权限列表
     * @param menus 用户菜单树
     * @return UserInfoDTO 用户信息 DTO
     */
    private UserInfoDTO buildUserInfoDTO(SysUserDTO sysUserDTO,
                                         List<SysRoleVO> roles,
                                         List<SysPermissionVO> permissions,
                                         List<SysMenuTreeVO> menus) {
        UserInfoDTO dto = new UserInfoDTO();
        dto.setUserId(sysUserDTO.getId());
        dto.setUsername(sysUserDTO.getUsername());
        dto.setNickname(sysUserDTO.getNickname());
        dto.setAvatar(sysUserDTO.getAvatar());
        dto.setRoles(roles);
        dto.setPermissions(permissions);
        dto.setMenus(menus);
        return dto;
    }



    public List<SysMenuTreeVO> buildMenuTree(List<SysMenuVO> menus, Long parentId) {
        return menus.stream()
                .filter(menu -> Objects.equals(menu.getMenuParentId(), parentId))
                .sorted(Comparator.comparing(SysMenuVO::getMenuSort))
                .map(menu -> {
                    SysMenuTreeVO treeVO = new SysMenuTreeVO();
                    treeVO.setId(menu.getId());
                    treeVO.setMenuName(menu.getMenuName());
                    treeVO.setMenuPath(menu.getMenuPath());
                    treeVO.setMenuComponent(menu.getMenuComponent());
                    treeVO.setMenuParentId(menu.getMenuParentId());
                    treeVO.setMenuType(menu.getMenuType());
                    treeVO.setMenuIcon(menu.getMenuIcon().toLowerCase());
                    treeVO.setMenuPermission(menu.getMenuPermission());
                    treeVO.setMenuSort(menu.getMenuSort());
                    treeVO.setMenuVisible(menu.getMenuVisible());
                    treeVO.setMenuStatus(menu.getMenuStatus());
                    treeVO.setCreateTime(menu.getCreateTime());
                    treeVO.setUpdateTime(menu.getUpdateTime());

                    // 构建 meta 信息
                    MetaVO meta = new MetaVO();
                    meta.setTitle(menu.getMenuName());
                    meta.setIcon(menu.getMenuIcon().toLowerCase());
                    meta.setKeepAlive(true);
                    meta.setHidden(menu.getMenuVisible() != null && menu.getMenuVisible() == 0);
                    treeVO.setMeta(meta);
                    treeVO.setChildren(buildMenuTree(menus, menu.getId())); // 递归构建子菜单
                    return treeVO;
                })
                .collect(Collectors.toList());
    }


}
