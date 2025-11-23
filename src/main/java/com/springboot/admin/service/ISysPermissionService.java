package com.springboot.admin.service;

import com.springboot.admin.model.entity.sys.SysPermission;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 权限点表 Service 接口
 *
 * 提供权限点相关的业务逻辑方法。
 */
public interface ISysPermissionService {

    /**
     * 根据权限点ID查询权限信息
     *
     * @param id 权限点ID
     * @return Mono<SysPermission> 响应式单对象
     */
    Mono<SysPermission> getPermissionById(Long id);

    /**
     * 新增权限点
     *
     * @param permission 权限点对象
     * @return Mono<SysPermission> 响应式单对象，返回保存后的实体
     */
    Mono<SysPermission> addPermission(SysPermission permission);

    /**
     * 更新权限点信息
     *
     * @param permission 权限点对象
     * @return Mono<SysPermission> 响应式单对象，返回更新后的实体
     */
    Mono<SysPermission> updatePermission(SysPermission permission);

    /**
     * 删除权限点
     *
     * @param id 权限点ID
     * @return Mono<Void> 响应式空对象，表示删除完成
     */
    Mono<Void> deletePermission(Long id);

    /**
     * 查询所有权限点
     *
     * @return Flux<SysPermission> 响应式流，返回多个权限点对象
     */
    Flux<SysPermission> listPermissions();
}

