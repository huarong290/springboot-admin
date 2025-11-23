package com.springboot.admin.service;

import com.springboot.admin.model.entity.sys.SysUserRole;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 用户角色关联表 Service 接口
 *
 * 提供用户与角色关联的业务逻辑方法。
 */
public interface ISysUserRoleService {

    /**
     * 根据用户ID查询角色关联关系
     *
     * @param userId 用户ID
     * @return Flux<SysUserRole> 响应式流，返回多个用户角色关联对象
     */
    Flux<SysUserRole> getRolesByUserId(Long userId);

    /**
     * 新增用户角色关联
     *
     * @param userRole 用户角色对象
     * @return Mono<SysUserRole> 响应式单对象，返回保存后的实体
     */
    Mono<SysUserRole> addUserRole(SysUserRole userRole);

    /**
     * 删除用户角色关联
     *
     * @param id 主键ID
     * @return Mono<Void> 响应式空对象，表示删除完成
     */
    Mono<Void> deleteUserRole(Long id);
}
