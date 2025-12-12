package com.springboot.admin.service;

import com.springboot.admin.model.dto.rolemenu.SysRoleMenuDTO;
import com.springboot.admin.model.entity.sys.SysRoleMenu;
import com.springboot.admin.model.vo.rolemenu.SysRoleMenuVO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 角色菜单关联表 Service 接口
 *
 * 提供角色与菜单关联的业务逻辑方法。
 */
public interface ISysRoleMenuService {

    /**
     * 根据角色ID查询菜单关联关系
     *
     * @param roleId 角色ID
     * @return Flux<SysRoleMenuVO> 响应式流，返回多个角色菜单关联对象
     */
    Flux<SysRoleMenuVO> getMenusByRoleId(Long roleId);

    /**
     * 新增角色菜单关联
     *
     * @param sysRoleMenuDTO 角色菜单对象
     * @return Mono<SysRoleMenu> 响应式单对象，返回保存后的实体
     */
    Mono<Long> addRoleMenu(SysRoleMenuDTO sysRoleMenuDTO);

    /**
     * 删除角色菜单关联
     *
     * @param id 主键ID
     * @return Mono<Long> 响应式空对象，表示删除完成
     */
    Mono<Long> deleteRoleMenu(Long id);

    /**
     * 查询所有角色菜单关联
     *
     * @return Flux<SysRoleMenu> 响应式流，返回多个角色菜单关联对象
     */
    Flux<SysRoleMenuVO> getRoleMenuList();
}

