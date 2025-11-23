package com.springboot.admin.service;


import com.springboot.admin.model.entity.sys.SysRolePermission;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 角色权限点关联 Service 接口
 *
 * 提供角色与权限点关联的业务逻辑方法。
 */
public interface ISysRolePermissionService {

    /**
     * 根据角色ID查询权限点关联关系
     *
     * @param roleId 角色ID
     * @return Flux<SysRolePermission> 响应式流，返回多个角色权限点关联对象
     */
    Flux<SysRolePermission> getPermissionsByRoleId(Long roleId);

    /**
     * 新增角色权限点关联
     *
     * @param rolePermission 角色权限点关联对象
     * @return Mono<SysRolePermission> 响应式单对象，返回保存后的实体
     */
    Mono<SysRolePermission> addRolePermission(SysRolePermission rolePermission);

    /**
     * 删除角色权限点关联
     *
     * @param id 主键ID
     * @return Mono<Void> 响应式空对象，表示删除完成
     */
    Mono<Void> deleteRolePermission(Long id);
}
