package com.springboot.admin.service.impl;

import com.springboot.admin.config.JwtProperties;
import com.springboot.admin.exception.BusinessException;
import com.springboot.admin.model.dto.TokenRefreshReqDTO;
import com.springboot.admin.model.dto.TokenResDTO;
import com.springboot.admin.model.dto.UserLoginReqDTO;
import com.springboot.admin.model.dto.menu.MenuDTO;
import com.springboot.admin.model.dto.menu.MetaDTO;
import com.springboot.admin.model.dto.user.UserInfoDTO;
import com.springboot.admin.model.entity.sys.SysMenu;
import com.springboot.admin.service.*;
import com.springboot.admin.utils.JwtUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

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
                    log.info("pass={}",passwordEncoder.encode(dto.getPassword()));
                    if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
                        log.warn("用户 {} 登录失败：密码错误", dto.getUsername());
                        return Mono.error(new RuntimeException("用户名或密码错误"));
                    }

                    Mono<String> accessTokenMono = jwtUtil.generateAccessToken(user.getUsername(), null);
                    Mono<String> refreshTokenMono = jwtUtil.generateRefreshToken(user.getUsername());

                    return Mono.zip(accessTokenMono, refreshTokenMono)
                            .flatMap(tuple -> {
                                String accessToken = tuple.getT1();
                                String refreshToken = tuple.getT2();

                                // 把存储 RefreshToken 放进响应式链
                                return redisService.storeRefreshToken(refreshToken, user.getUsername())
                                        .then(Mono.defer(() -> {
                                            TokenResDTO tokenResDTO = new TokenResDTO();
                                            tokenResDTO.setAccessToken(accessToken);
                                            tokenResDTO.setRefreshToken(refreshToken);


                                            // 设置过期时间（秒）
                                            tokenResDTO.setExpiresIn(jwtProperties.getAccessTokenExpiration() / 1000);
                                            log.info("用户 {} 登录成功，生成 Token", dto.getUsername());
                                            return Mono.just(tokenResDTO);
                                        }));
                            });
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
                        return Mono.error(new BusinessException("0100103", "令牌已被撤销"));
                    }
                    return redisService.removeRefreshToken(refreshToken)
                            .doOnSuccess(v -> log.info("刷新令牌 {} 已移除，用户登出成功", refreshToken));
                });
    }
    @Override
    public Mono<UserInfoDTO> getUserInfoByToken(String token) {
        // 1. 解析 token 获取 Claims
        return jwtUtil.parseToken(token)
                .flatMap(claims -> {
                    String username = claims.getSubject();
                    log.info("开始获取用户信息，username={}", username);

                    // 2. 查询用户信息
                    return sysUserService.getUserByUsername(username)
                            .switchIfEmpty(Mono.error(new BusinessException("0100104", "用户不存在")))
                            .flatMap(user -> {
                                log.info("用户信息: {}", user);

                                // 3. 查询角色 —— 使用 flatMap 避免 null
                                Mono<List<String>> rolesMono = sysRoleService.listRolesByUserId(user.getId())
                                        .flatMap(role -> {
                                            if (role == null || role.getRoleCode() == null) {
                                                return Mono.empty();
                                            }
                                            return Mono.just(role.getRoleCode());
                                        })
                                        .collectList()
                                        .doOnNext(roles -> log.info("角色列表: {}", roles));

                                // 4. 查询权限 —— 使用 flatMap 避免 null
                                Mono<List<String>> permissionsMono = sysPermissionService.listPermissionsByUserId(user.getId())
                                        .flatMap(permission -> {
                                            log.info("权限列表permission: {}", permission);
                                            if (permission == null || permission.getPermissionCode() == null) {
                                                return Mono.empty();
                                            }
                                            return Mono.just(permission.getPermissionCode());
                                        })
                                        .collectList()
                                        .doOnNext(perms -> log.info("权限列表: {}", perms));

                                // 5. 查询菜单 —— 使用 flatMap 避免 null
                                Mono<List<MenuDTO>> menusMono = sysMenuService.getMenuListByUserId(user.getId())
                                        .flatMap(menu -> {
                                            if (menu == null) {
                                                return Mono.empty();
                                            }
                                            return Mono.just(convertToMenuDTO(menu));
                                        })
                                        .collectList()
                                        .doOnNext(menus -> log.info("菜单列表: {}", menus));

                                // 6. 聚合结果
                                return Mono.zip(rolesMono, permissionsMono, menusMono)
                                        .map(tuple -> {
                                            List<String> roles = Optional.ofNullable(tuple.getT1()).orElse(List.of());
                                            List<String> permissions = Optional.ofNullable(tuple.getT2()).orElse(List.of());
                                            List<MenuDTO> menus = Optional.ofNullable(tuple.getT3()).orElse(List.of());

                                            UserInfoDTO dto = new UserInfoDTO();
                                            dto.setUserId(user.getId());
                                            dto.setUsername(user.getUsername());
                                            dto.setNickname(user.getNickname());
                                            dto.setAvatar(user.getAvatar());
                                            dto.setRoles(roles);
                                            dto.setPermissions(permissions);
                                            dto.setMenus(menus);

                                            log.info("最终组装的 UserInfoDTO: {}", dto);
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
     * SysMenu 转换为 MenuDTO
     */
    private MenuDTO convertToMenuDTO(SysMenu menu) {
        MenuDTO dto = new MenuDTO();
        dto.setId(menu.getId());
        dto.setPath(menu.getMenuPath());
        dto.setComponent(menu.getMenuComponent());
        dto.setParentId(menu.getMenuParentId());
        dto.setType(menu.getMenuType());
        dto.setPermission(menu.getMenuPermission());

        // 构建 meta 信息
        MetaDTO meta = new MetaDTO();
        meta.setTitle(menu.getMenuName());       // 菜单标题
        meta.setIcon(menu.getMenuIcon());        // 菜单图标
        meta.setVisible(menu.getMenuVisible() != null && menu.getMenuVisible() == 1);// 是否显示

        // 如果你有角色和权限的关联，可以在这里填充
        // meta.setRoles(...);
        // meta.setPermissions(...);

        dto.setMeta(meta);

        return dto;
    }




}
