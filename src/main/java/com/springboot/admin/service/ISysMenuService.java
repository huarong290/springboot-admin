package com.springboot.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.springboot.admin.model.dto.menu.SysMenuDTO;
import com.springboot.admin.model.entity.sys.SysMenu;
import com.springboot.admin.model.vo.menu.SysMenuTreeVO;
import com.springboot.admin.model.vo.menu.SysMenuVO;

import java.util.List;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 * @author system
 * @since 2026-01-18
 */
public interface ISysMenuService extends IService<SysMenu> {
    /**
     * 新增菜单
     *
     * @param menuDTO 菜单实体
     * @return Long 新增菜单ID
     */
    Long addMenu(SysMenuDTO menuDTO);

    /**
     * 更新菜单信息
     *
     * @param menuDTO 菜单实体
     * @return 更新的记录数
     */
    int updateMenu(SysMenuDTO menuDTO);


    /**
     *
     * @param menuId 菜单ID
     * @return 删除的菜单数量
     */
    int deleteMenu(Long menuId,boolean logicalDelete);

    /**
     * 根据菜单ID查询菜单详情
     *
     * @param menuId 菜单ID
     * @return 菜单 VO
     */
    SysMenuVO getMenuById(Long menuId);

    /**
     * 根据父菜单ID查询子菜单列表
     *
     * @param parentId 父菜单ID
     * @return 子菜单列表
     */
    List<SysMenuVO> getMenusByParentId(Long parentId);

    /**
     * 查询所有菜单（平铺结构）
     *
     * @return 菜单列表
     */
    List<SysMenuVO> getMenuList();

    /**
     * 查询所有菜单（树形结构）
     *
     * @return 菜单树列表
     */
    List<SysMenuTreeVO> getMenuTree();

    /**
     * 根据用户ID查询菜单树
     *
     * @param userId 用户ID
     * @return 用户可访问的菜单树
     */
    List<SysMenuTreeVO> getMenuTreeByUserId(Long userId);


    /**
     * 根据用户ID查询菜单列表（平铺）
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<SysMenuVO> getMenuListByUserId(Long userId);

    /**
     * 根据角色ID查询菜单列表
     *
     * @param roleId 角色ID
     * @return 菜单列表
     */
    List<SysMenuVO> listMenusByRoleId(Long roleId);
    /**
     * 根据角色ID集合查询菜单列表
     *
     * @param roleIds 角色ID集合
     * @return 菜单VO集合
     */
    List<SysMenu> selectMenusByRoleIds(List<Long> roleIds);
}
