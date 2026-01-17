package com.springboot.admin.service.impl;

import com.springboot.admin.common.BusinessResultCode;
import com.springboot.admin.config.JwtProperties;
import com.springboot.admin.convert.SysMenuConvert;
import com.springboot.admin.exception.BusinessException;
import com.springboot.admin.model.dto.TokenRefreshReqDTO;
import com.springboot.admin.model.dto.TokenResDTO;
import com.springboot.admin.model.dto.UserLoginReqDTO;
import com.springboot.admin.model.dto.user.UserInfoDTO;
import com.springboot.admin.model.vo.menu.MetaVO;
import com.springboot.admin.model.vo.menu.SysMenuTreeVO;
import com.springboot.admin.model.vo.menu.SysMenuVO;
import com.springboot.admin.service.*;
import com.springboot.admin.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 认证服务实现类
 * * 主要职责：
 * 1. 处理用户登录、登出、Token 刷新逻辑。
 * 2. 聚合用户信息、权限、菜单树等复杂业务数据。
 * 3. 采用 Reactor 响应式流处理，保证高并发下的非阻塞执行。
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceServiceImpl implements IAuthService {

    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;
    private final IRedisService redisService;
    private final PasswordEncoder passwordEncoder;
    private final ISysUserService sysUserService;
    private final ISysRoleService sysRoleService;
    private final ISysPermissionService sysPermissionService;
    private final ISysMenuService sysMenuService;
    private final ICaptchaService captchaService;
    // 注入 MapStruct 转换器
    private final SysMenuConvert sysMenuConvert;
    private static final String REFRESH_TOKEN_PREFIX = "refresh:";

    /**
     * 执行用户登录业务
     * * 流程：
     * 1. 验证码校验（失败则抛出异常，利用全局异常处理器拦截）。
     * 2. 异步删除验证码（副作用操作，不阻塞主流程）。
     * 3. 数据库查询用户信息。
     * 4. 账号状态与密码哈希比对。
     * 5. 并行生成双 Token (Access/Refresh) 并更新登录时间。
     * 6. 存储刷新令牌至 Redis 建立会话。
     *
     * @param dto 登录请求参数（包含用户名、密码、验证码 ID/Code）
     * @return 包含双 Token 的响应对象
     */
    @Override
    public Mono<TokenResDTO> login(UserLoginReqDTO dto) {
        // 1. 验证码预校验
        return captchaService.validateCaptcha(dto.getCaptchaId(), dto.getCaptchaCode())
                .flatMap(valid -> {
                    if (!valid) return Mono.error(new BusinessException(BusinessResultCode.PARAM_INVALID.getCode(), "验证码错误或已过期"));

                    // 【优化】校验通过立即删除（异步不阻塞），确保验证码一次性使用
                    return captchaService.deleteCaptchaReturnBoolean(dto.getCaptchaId()).thenReturn(true);
                })
                // 2. 账号基本信息检查
                .then(sysUserService.getUserByUsername(dto.getUsername()))
                .switchIfEmpty(Mono.error(new BusinessException(BusinessResultCode.USER_NOT_FOUND)))
                .flatMap(user -> {
                    // 3. 状态检查与密码核对
                    if (user.getStatus() == 0) return Mono.error(new BusinessException(BusinessResultCode.USER_DISABLED.getCode(), "账号已被禁用"));
                    if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
                        return Mono.error(new BusinessException(BusinessResultCode.USER_PASSWORD_ERROR));
                    }

                    // 4. 【并行处理】生成双 Token 及 更新登录时间
                    return Mono.zip(
                            jwtUtil.generateAccessToken(user.getUsername(), null),
                            jwtUtil.generateRefreshToken(user.getUsername()),
                            sysUserService.updateLastLoginTime(user.getId(), LocalDateTime.now()).onErrorReturn(0L)
                    ).flatMap(tuple -> {
                        String access = tuple.getT1();
                        String refresh = tuple.getT2();

                        // 5. 存储 RefreshToken 并返回结果
                        return redisService.storeRefreshToken(refresh, user.getUsername())
                                .thenReturn(buildTokenResponse(access, refresh));
                    });
                });
    }
    /**
     * 刷新访问令牌
     * * @param dto 包含旧 RefreshToken 的请求
     * @return 新的 AccessToken（维持原 RefreshToken）
     */
    @Override
    public Mono<TokenResDTO> refreshToken(TokenRefreshReqDTO dto) {
        return redisService.getValue("refresh:" + dto.getRefreshToken())
                .switchIfEmpty(Mono.error(new BusinessException(BusinessResultCode.TOKEN_INVALID)))
                .flatMap(username -> sysUserService.getUserByUsername(username)
                        .flatMap(user -> {
                            // 【优化】刷新时增加状态检查
                            if (user.getStatus() == 0) return Mono.error(new BusinessException(BusinessResultCode.USER_DISABLED.getCode(),"账号状态异常"));

                            return jwtUtil.generateAccessToken(username, null)
                                    .map(newAccess -> buildTokenResponse(newAccess, dto.getRefreshToken()));
                        }));
    }
    /**
     * 用户登出
     * * @param refreshToken 需要作废的刷新令牌
     * @return 无返回值的信号
     */
    @Override
    public Mono<Void> logout(String refreshToken) {
        return redisService.isRefreshTokenStored(refreshToken)
                .flatMap(exists -> {
                    if (!exists) return Mono.error(new BusinessException(BusinessResultCode.TOKEN_NOT_FOUND));
                    return redisService.removeRefreshToken(refreshToken);
                });
    }
    /**
     * 根据当前 Token 解析并拉取完整的用户信息负载
     * * 包含：
     * - 用户基本字段（ID, 昵称, 头像）
     * - 角色编码列表
     * - 权限字符串列表（由前端控制按钮显示）
     * - 树形菜单结构（前端动态生成侧边栏）
     *
     * @param token 访问令牌
     * @return 聚合后的用户信息 DTO
     */
    @Override
    public Mono<UserInfoDTO> getUserInfoByToken(String token) {
        return jwtUtil.parseToken(token)
                .flatMap(claims -> {
                    String username = claims.getSubject();
                    return sysUserService.getUserByUsername(username)
                            .switchIfEmpty(Mono.error(new BusinessException(BusinessResultCode.USER_NOT_FOUND)))
                            .flatMap(user ->
                                    Mono.zip(
                                            sysRoleService.listRolesByUserId(user.getId()).collectList(),
                                            sysPermissionService.listPermissionsByUserId(user.getId()).distinct().collectList(),
                                            sysMenuService.getMenuTreeByUserId(user.getId()).collectList()
                                    ).map(tuple -> {
                                        UserInfoDTO dto = new UserInfoDTO();
                                        // 基础信息设置
                                        dto.setUserId(user.getId());
                                        dto.setUsername(user.getUsername());
                                        dto.setNickname(user.getNickname());
                                        dto.setAvatar(user.getAvatar());

                                        dto.setRoles(tuple.getT1());
                                        dto.setPermissions(tuple.getT2());
                                        // 这里的 menus 已经是 Service 层处理好的树形结构
                                        dto.setMenus(tuple.getT3());
                                        return dto;
                                    })
                            );
                });
    }


    // -----------------------------------------------------------------------
    // 菜单结构处理核心算法
    // -----------------------------------------------------------------------

    /**
     * 构建树形菜单 (高性能 O(N) 实现)
     * 将扁平化的菜单列表转换为树形结构
     * 算法复杂度：O(N)，利用空间换时间，避免嵌套循环
     * * @param menus 数据库查出的平铺菜单列表（包含 parentId）
     * @param parentId 根节点 ID（通常为 0）
     * @return 组装好的树形菜单
     */
    public List<SysMenuTreeVO> buildMenuTree(List<SysMenuVO> menus, Long parentId) {
        if (menus == null || menus.isEmpty()) {
            return Collections.emptyList();
        }

        // 1. 使用 MapStruct 将 SysMenuVO 转换为带 Meta 的 SysMenuTreeVO，并按父ID分组
        // 复杂度: O(N)
        Map<Long, List<SysMenuTreeVO>> groupedNodes = menus.stream()
                .map(sysMenuConvert::toTreeVO)
                .collect(Collectors.groupingBy(SysMenuTreeVO::getMenuParentId));

        // 2. 递归递归填充 children
        return recursiveBuild(groupedNodes, parentId);
    }

    /**
     * 内部递归组装方法
     * * @param nodeMap 预分组的节点 Map
     * @param pid 当前处理的父 ID
     */
    private List<SysMenuTreeVO> recursiveBuild(Map<Long, List<SysMenuTreeVO>> nodeMap, Long pid) {
        List<SysMenuTreeVO> children = nodeMap.getOrDefault(pid, new ArrayList<>());

        // 排序并递归设置子节点
        children.sort(Comparator.comparingInt(SysMenuTreeVO::getMenuSort));
        children.forEach(node -> {
            List<SysMenuTreeVO> subChildren = recursiveBuild(nodeMap, node.getId());
            node.setChildren(subChildren);
        });

        return children;
    }

    /**
     * 构建统一 Token 返回格式
     */
    private TokenResDTO buildTokenResponse(String access, String refresh) {
        TokenResDTO res = new TokenResDTO();
        res.setAccessToken(access);
        res.setRefreshToken(refresh);
        // 将毫秒级失效时间转为秒级返回前端
        res.setExpiresIn(jwtProperties.getAccessTokenExpiration() / 1000);
        res.setTokenType("Bearer");
        return res;
    }

}
