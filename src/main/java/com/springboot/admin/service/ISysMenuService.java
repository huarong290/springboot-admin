package com.springboot.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.springboot.admin.model.entity.sys.SysMenu;
import com.springboot.admin.model.vo.menu.SysMenuTreeVO;

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
     * 根据用户ID查询菜单树
     *
     * @param userId 用户ID
     * @return 菜单树VO集合
     */
    List<SysMenuTreeVO> listMenusByUserId(Long userId);
    /**
     * 根据角色ID集合查询菜单列表
     *
     * @param roleIds 角色ID集合
     * @return 菜单VO集合
     */
    List<SysMenu> selectMenusByRoleIds(List<Long> roleIds);
}
