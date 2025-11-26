package com.springboot.admin.service;

import com.springboot.admin.model.entity.sys.SysMenu;
import com.springboot.admin.model.entity.sys.SysPermission;
import com.springboot.admin.model.entity.sys.SysRole;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 角色表 Service 接口
 *
 * 提供角色相关的业务逻辑方法。
 */
public interface ISysRoleService {

    /**
     * 根据角色ID查询角色信息
     *
     * @param id 角色ID
     * @return Mono<SysRole> 响应式单对象
     */
    Mono<SysRole> getRoleById(Long id);
    /**
     * 根据角色编码查询角色信息
     *
     * @param roleCode 角色编码
     * @return Mono<SysRole> 响应式单对象
     */
    Mono<SysRole> getRoleByCode(String roleCode);
    /**
     * 新增角色
     *
     * @param role 角色对象
     * @return Mono<SysRole> 响应式单对象，返回保存后的实体
     */
    Mono<SysRole> addRole(SysRole role);

    /**
     * 更新角色信息
     *
     * @param role 角色对象
     * @return Mono<SysRole> 响应式单对象，返回更新后的实体
     */
    Mono<SysRole> updateRole(SysRole role);

    /**
     * 删除角色
     *
     * @param id 角色ID
     * @return Mono<Void> 响应式空对象，表示删除完成
     */
    Mono<Void> deleteRole(Long id);

    /**
     * 批量删除角色
     *
     * @param ids 角色ID集合
     * @return Mono<Void> 响应式空对象，表示删除完成
     */
    Mono<Void> deleteRoles(Iterable<Long> ids);

    /**
     * 查询所有角色
     *
     * @return Flux<SysRole> 响应式流，返回多个角色对象
     */
    Flux<SysRole> listRoles();


    /**
     * 根据用户ID查询用户的角色列表
     *
     * @param userId 用户ID
     * @return Flux<SysRole> 响应式流，返回该用户的角色对象
     */
    Flux<SysRole> listRolesByUserId(Long userId);

    /**
     * 判断角色是否存在
     *
     * @param roleCode 角色编码
     * @return Mono<Boolean> 响应式布尔值
     */
    Mono<Boolean> existsByRoleCode(String roleCode);

    /**
     * 根据权限ID查询角色列表
     *
     * @param permissionId 权限ID
     * @return Flux<SysRole> 响应式流，返回拥有该权限的角色集合
     */
     Flux<SysRole> findRolesByPermissionId(Long permissionId);
    /**
     * 根据菜单D查询角色表
     *
     * @param menuId 菜单D
     * @return Flux<SysRole> 响应式流，返回该菜单拥有的角色集合
     */
    Flux<SysRole> listPermissionsByRoleId(Long menuId);
//
//    /**
//     * 根据角色ID查询菜单列表
//     *
//     * @param roleId 角色ID
//     * @return Flux<SysMenu> 响应式流，返回该角色拥有的菜单集合
//     */
//    Flux<SysMenu> listMenusByRoleId(Long roleId);
}
