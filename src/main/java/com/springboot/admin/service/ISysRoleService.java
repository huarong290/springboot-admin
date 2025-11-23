package com.springboot.admin.service;

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
     * 查询所有角色
     *
     * @return Flux<SysRole> 响应式流，返回多个角色对象
     */
    Flux<SysRole> listRoles();
}
