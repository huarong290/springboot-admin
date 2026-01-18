package com.springboot.admin.convert;

import com.springboot.admin.model.dto.user.UserInfoDTO;
import com.springboot.admin.model.vo.menu.SysMenuTreeVO;
import com.springboot.admin.model.vo.menu.SysMenuVO;
import com.springboot.admin.model.vo.permission.SysPermissionVO;
import com.springboot.admin.model.vo.role.SysRoleVO;
import com.springboot.admin.model.vo.user.SysUserInfoVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户信息转换器
 * <p>
 * 将 UserInfoDTO 转换为 SysUserInfoVO，用于接口层返回前端
 * - DTO 包含完整对象（角色、权限、菜单树）
 * - VO 仅返回前端需要的字段（角色编码、权限编码、菜单列表）
 */
@Mapper(componentModel = "spring")
public interface UserInfoConvert {

    /**
     * UserInfoDTO → SysUserInfoVO
     * <p>
     * 自动映射基本字段，复杂列表使用自定义方法处理
     *
     * @param dto DTO 对象
     * @return VO 对象
     */
    @Mapping(source = "roles", target = "roles", qualifiedByName = "mapRoles")
    @Mapping(source = "permissions", target = "permissions", qualifiedByName = "mapPermissions")
    @Mapping(source = "menus", target = "menus", qualifiedByName = "mapMenus")
    SysUserInfoVO dtoToVO(UserInfoDTO dto);

    /**
     * List<SysRoleVO> → List<String>（仅返回角色编码）
     */
    @Named("mapRoles")
    default List<String> mapRoles(List<SysRoleVO> roles) {
        if (roles == null) return null;
        return roles.stream()
                .map(SysRoleVO::getRoleCode) // 返回角色编码
                .collect(Collectors.toList());
    }

    /**
     * List<SysPermissionVO> → List<String>（仅返回权限编码）
     */
    @Named("mapPermissions")
    default List<String> mapPermissions(List<SysPermissionVO> permissions) {
        if (permissions == null) return null;
        return permissions.stream()
                .map(SysPermissionVO::getPermissionCode) // 返回权限编码
                .collect(Collectors.toList());
    }

    /**
     * List<SysMenuTreeVO> → List<SysMenuVO>
     * <p>
     * 将树形菜单 DTO 转换为前端 VO
     */
    @Named("mapMenus")
    default List<SysMenuVO> mapMenus(List<SysMenuTreeVO> menus) {
        if (menus == null) return null;
        return menus.stream()
                .map(menu -> {
                    SysMenuVO vo = new SysMenuVO();
                    vo.setId(menu.getId());
                    vo.setMenuParentId(menu.getMenuParentId());
                    vo.setMenuName(menu.getMenuName());
                    vo.setMenuPath(menu.getMenuPath());
                    vo.setMenuComponent(menu.getMenuComponent());
                    vo.setMenuIcon(menu.getMenuIcon());
                    vo.setMenuType(menu.getMenuType());
                    vo.setMenuSort(menu.getMenuSort());
                    vo.setMenuVisible(menu.getMenuVisible());
                    vo.setMenuStatus(menu.getMenuStatus());
                    vo.setDeleteFlag(0); // 默认未删除
                    vo.setCreateBy(null);
                    vo.setUpdateBy(null);
                    vo.setCreateTime(menu.getCreateTime());
                    vo.setUpdateTime(menu.getUpdateTime());
                    vo.setIsLeaf(menu.getChildren() == null || menu.getChildren().isEmpty());
                    return vo;
                })
                .collect(Collectors.toList());
    }
}
