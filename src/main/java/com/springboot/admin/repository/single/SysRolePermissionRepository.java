package com.springboot.admin.repository.single;

import com.springboot.admin.model.entity.sys.SysRolePermission;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 角色权限点关联表 Repository 接口
 *
 * 用于查询角色和权限点的关联关系。
 */
public interface SysRolePermissionRepository extends ReactiveCrudRepository<SysRolePermission, Long> {

    /**
     * 根据角色ID查询权限点关联关系
     *
     * @param roleId 角色ID
     * @return Flux<SysRolePermission> 响应式流，返回多个角色权限点关联对象
     */
    Flux<SysRolePermission> findByRoleId(Long roleId);

}
