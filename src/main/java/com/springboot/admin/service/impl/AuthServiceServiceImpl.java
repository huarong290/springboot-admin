package com.springboot.admin.service.impl;

import com.springboot.admin.config.JwtProperties;
import com.springboot.admin.exception.BusinessException;
import com.springboot.admin.model.dto.TokenRefreshReqDTO;
import com.springboot.admin.model.dto.TokenResDTO;
import com.springboot.admin.model.dto.UserLoginReqDTO;
import com.springboot.admin.model.dto.user.SysUserDTO;
import com.springboot.admin.model.dto.user.UserInfoDTO;
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
    /**
     * 用户登录：验证密码，生成并存储 Access 和 Refresh Token
     *
     * @param dto 用户登录请求参数（用户名、密码）
     * @return Mono<TokenResDTO> 响应式单对象，包含 AccessToken 和 RefreshToken
     */
    @Override
    public Mono<TokenResDTO> login(UserLoginReqDTO dto) {
        return sysUserService.getUserByUsername(dto.getUsername())
                .flatMap(user -> {
                    log.info("pass={}", passwordEncoder.encode(dto.getPassword()));
                    if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
                        log.warn("用户 {} 登录失败：密码错误", dto.getUsername());
                        return Mono.error(new RuntimeException("用户名或密码错误"));
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
                })
                .switchIfEmpty(Mono.error(new RuntimeException("用户不存在")));
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

                    // Step2: 查询用户信息
                    return sysUserService.getUserByUsername(username)
                            .switchIfEmpty(Mono.error(new BusinessException("0100104", "用户不存在")))
                            .flatMap(user -> {
                                log.info("[Step2] 查询到用户信息: {}", user);

                                // Step3: 查询角色列表
                                Mono<List<String>> rolesMono = sysRoleService.listRolesByUserId(user.getId())
                                        .map(SysRoleVO::getRoleCode)
                                        .filter(Objects::nonNull)
                                        .collectList()
                                        .doOnNext(roles -> log.info("[Step3] 角色列表: {}", roles));

                                // Step4: 查询权限列表（基于权限表）
                                Mono<List<String>> permissionsMono = sysPermissionService
                                        .listPermissionsByUserId(user.getId())
                                        .map(SysPermissionVO::getPermissionCode)
                                        .filter(Objects::nonNull)
                                        .distinct()
                                        .collectList()
                                        .doOnNext(perms -> log.info("[Step4] 权限列表: {}", perms));

                                // Step5: 查询菜单列表（递归 SQL 补齐父菜单）
                                Mono<List<SysMenuTreeVO>> menusMono = sysMenuService
                                        .getMenuListByUserId(user.getId())
                                        .collectList()
                                        .map(menuList -> {
                                            log.info("[Step5] 用户 {} 拥有 {} 个菜单，开始构建树形结构", user.getId(), menuList.size());
                                            return buildMenuTree(menuList, 0L); // 从根节点开始
                                        })
                                        .doOnNext(menus -> log.info("[Step5] 菜单树: {}", menus));

                                // Step6: 聚合结果
                                return Mono.zip(rolesMono, permissionsMono, menusMono)
                                        .map(tuple -> {
                                            UserInfoDTO dto = buildUserInfoDTO(
                                                    user,
                                                    tuple.getT1(), // 角色列表
                                                    tuple.getT2(), // 权限列表
                                                    tuple.getT3()  // 菜单树
                                            );
                                            log.info("[Step6] 最终组装的 UserInfoDTO: {}", dto);
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
                                         List<String> roles,
                                         List<String> permissions,
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
                    SysMenuTreeVO vo = new SysMenuTreeVO();
                    vo.setId(menu.getId());
                    vo.setMenuName(menu.getMenuName());
                    vo.setMenuPath(menu.getMenuPath());
                    vo.setMenuComponent(menu.getMenuComponent());
                    vo.setMenuIcon(menu.getMenuIcon());
                    vo.setMenuType(menu.getMenuType());
                    vo.setMenuSort(menu.getMenuSort());
                    vo.setMenuParentId(menu.getMenuParentId());
                    vo.setMenuStatus(menu.getMenuStatus());
                    vo.setCreateTime(menu.getCreateTime());
                    vo.setChildren(buildMenuTree(menus, menu.getId())); // 递归构建子菜单
                    return vo;
                })
                .collect(Collectors.toList());
    }


}
