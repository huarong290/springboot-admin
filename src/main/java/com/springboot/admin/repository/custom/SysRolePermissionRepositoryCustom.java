package com.springboot.admin.repository.custom;

import com.springboot.admin.model.entity.sys.SysRolePermission;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * 自定义角色权限点关联 Repository
 *
 * 提供复杂查询逻辑，例如根据角色ID查询权限点关联关系。
 * 使用 R2DBC DatabaseClient 执行 SQL，返回响应式 Flux。
 */
@Repository
public class SysRolePermissionRepositoryCustom {

    private final DatabaseClient client;

    public SysRolePermissionRepositoryCustom(DatabaseClient client) {
        this.client = client;
    }

    /**
     * 根据角色ID查询权限点关联关系
     *
     * @param roleId 角色ID
     * @return Flux<SysRolePermission> 响应式流，返回该角色绑定的权限点列表
     */
    public Flux<SysRolePermission> findByRoleId(Long roleId) {
        String sql = "SELECT id, role_id, permission_id FROM sys_role_permission WHERE role_id = :roleId";

        return client.sql(sql)
                .bind("roleId", roleId)
                .map((row, metadata) -> {
                    SysRolePermission rolePermission = new SysRolePermission();
                    rolePermission.setId(row.get("id", Long.class));
                    rolePermission.setRoleId(row.get("role_id", Long.class));
                    rolePermission.setPermissionId(row.get("permission_id", Long.class));
                    return rolePermission;
                })
                .all();
    }
}

