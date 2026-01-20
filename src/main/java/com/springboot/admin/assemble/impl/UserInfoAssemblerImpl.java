package com.springboot.admin.assemble.impl;

import com.springboot.admin.assemble.UserInfoAssembler;
import com.springboot.admin.convert.SysMenuConvert;
import com.springboot.admin.model.dto.user.UserInfoDTO;
import com.springboot.admin.model.entity.sys.SysMenu;
import com.springboot.admin.model.entity.sys.SysUser;
import com.springboot.admin.model.vo.menu.SysMenuTreeVO;
import com.springboot.admin.model.vo.role.SysRoleVO;
import com.springboot.admin.service.*;
import com.springboot.admin.utils.MenuTreeBuilderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用户信息装配器
 * <p>
 * 负责将多源数据组装成 UserInfoDTO
 * - 用户基础信息
 * - 角色列表
 * - 权限列表（按钮/接口）
 * - 菜单树
 * - 数据权限范围
 */
@Component
public class UserInfoAssemblerImpl implements UserInfoAssembler {

    @Autowired
    private ISysUserService iSysUserService;
    @Autowired
    private ISysRoleService iSysRoleService;
    @Autowired
    private ISysPermissionService iSysPermissionService;
    @Autowired
    private ISysMenuService iSysMenuService;
    @Autowired
    private IDataScopeCalculator iDataScopeCalculator;
    @Autowired
    private SysMenuConvert sysMenuConvert;

    @Override
    public UserInfoDTO assemble(Long userId, String loginIp, String clientType, String deviceId) {
        UserInfoDTO dto = new UserInfoDTO();
        // =======================
        // 1️⃣ 用户基础信息
        // =======================
        SysUser user = iSysUserService.getById(userId);
        dto.setUserId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setNickname(user.getNickname());
        dto.setAvatar(user.getAvatar());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setUserStatus(user.getUserStatus());
        dto.setTenantId(user.getTenantId());
        dto.setLastLoginTime(user.getLastLoginTime());

        dto.setLoginIp(loginIp);
        dto.setLoginClientType(clientType);
        dto.setLoginDeviceId(deviceId);
        // =======================
        // 2️⃣ 用户角色列表
        // =======================
        List<SysRoleVO> roles = iSysRoleService.selectRolesByUserId(userId);
        dto.setRoles(roles);
        List<Long> roleIds = roles.stream()
                .map(SysRoleVO::getId)
                .toList();
        // =======================
        // 3️⃣ 功能权限（最终鉴权来源 按钮/接口）
        // =======================
        List<String> permissionCodes =
                iSysPermissionService.selectPermissionCodesByRoleIds(roleIds);
        dto.setPermissionCodes(permissionCodes);
        // =======================
        // 4️⃣ 用户菜单树
        // =======================
        List<SysMenu> menus =
                iSysMenuService.selectMenusByRoleIds(roleIds);
        List<SysMenuTreeVO> menuVOs = sysMenuConvert.toVoList(menus);
        dto.setMenus(MenuTreeBuilderUtil.build(menuVOs));

        // =======================
        // 5️⃣ 数据权限范围
        // =======================
        dto.setDataScope(iDataScopeCalculator.calculate(userId));
        return dto;
    }
}
