package com.springboot.admin.repository.custom;

import com.springboot.admin.model.entity.sys.SysRolePermission;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 自定义角色权限点关联 Repository
 * <p>
 * 提供复杂查询逻辑，例如根据角色ID查询权限点关联关系。
 * 使用 R2DBC DatabaseClient 执行 SQL，返回响应式 Flux。
 */
@Repository
public class SysRolePermissionRepositoryCustom {

    private final DatabaseClient client;

    public SysRolePermissionRepositoryCustom(DatabaseClient client) {
        this.client = client;
    }

    public Mono<Long> bindRolePermissionsBatch(Long roleId, List<Long> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return Mono.just(0L);
        }

        // 拼接批量插入 SQL
        StringBuilder sql = new StringBuilder(
                "INSERT INTO sys_role_permission (role_id, permission_id, delete_flag, create_by, create_time, update_by, update_time) VALUES "
        );

        for (int i = 0; i < permissionIds.size(); i++) {
            sql.append("(:roleId").append(i).append(", :permissionId").append(i)
                    .append(", 0, 'system', :now, 'system', :now)");
            if (i < permissionIds.size() - 1) {
                sql.append(", ");
            }
        }

        DatabaseClient.GenericExecuteSpec spec = client.sql(sql.toString());

        // 绑定参数
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < permissionIds.size(); i++) {
            spec = spec.bind("roleId" + i, roleId)
                    .bind("permissionId" + i, permissionIds.get(i))
                    .bind("now", now);
        }

        // 执行并返回影响条数
        return spec.fetch().rowsUpdated();
    }



    /**
     * 删除指定权限
     */
    public Mono<Long> deleteByRoleIdAndPermissionIds(Long roleId, List<Long> permissionIds) {
        if (permissionIds.isEmpty()) return Mono.just(0L);
        String inClause = permissionIds.stream()
                .map(pid -> pid.toString())
                .reduce((a, b) -> a + "," + b)
                .orElse("0");
        String sql = "DELETE FROM sys_role_permission WHERE role_id = :roleId AND permission_id IN (" + inClause + ")";
        return client.sql(sql)
                .bind("roleId", roleId)
                .fetch()
                .rowsUpdated();
    }

    /**
     * 批量插入新增权限
     */
    public Mono<Long> insertRolePermissions(Long roleId, List<Long> permissionIds) {
        if (permissionIds.isEmpty()) return Mono.just(0L);

        StringBuilder sql = new StringBuilder(
                "INSERT INTO sys_role_permission (role_id, permission_id, delete_flag, create_by, create_time, update_by, update_time) VALUES "
        );

        for (int i = 0; i < permissionIds.size(); i++) {
            sql.append("(:roleId, :permissionId").append(i)
                    .append(", 0, 'system', :now, 'system', :now)");
            if (i < permissionIds.size() - 1) {
                sql.append(", ");
            }
        }

        DatabaseClient.GenericExecuteSpec spec = client.sql(sql.toString());
        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < permissionIds.size(); i++) {
            spec = spec.bind("permissionId" + i, permissionIds.get(i));
        }
        spec = spec.bind("roleId", roleId).bind("now", now);

        return spec.fetch().rowsUpdated();
    }

    /**查询角色已有权限
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

