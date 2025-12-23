package com.springboot.admin.repository.custom;


import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 自定义菜单权限关联 Repository
 * <p>
 * 提供复杂查询逻辑，例如根据角色ID查询权限点关联关系。
 * 使用 R2DBC DatabaseClient 执行 SQL，返回响应式 Flux。
 */
@Repository
public class SysMenuPermissionRepositoryCustom {

    private final DatabaseClient client;

    public SysMenuPermissionRepositoryCustom(DatabaseClient client) {
        this.client = client;
    }

    /**
     * 查询菜单已有权限ID集合
     */
    public Flux<Long> findPermissionIdsByMenuId(Long menuId) {
        String sql = "SELECT permission_id FROM sys_menu_permission WHERE menu_id = :menuId";
        return client.sql(sql)
                .bind("menuId", menuId)
                .map((row, meta) -> row.get("permission_id", Long.class))
                .all();
    }

    /**
     * 删除菜单绑定的部分权限
     */
    public Mono<Long> deleteByMenuIdAndPermissionIds(Long menuId, List<Long> permissionIds) {
        if (permissionIds.isEmpty()) return Mono.just(0L);
        String inClause = permissionIds.stream().map(String::valueOf).collect(Collectors.joining(","));
        String sql = "DELETE FROM sys_menu_permission WHERE menu_id = :menuId AND permission_id IN (" + inClause + ")";
        return client.sql(sql).bind("menuId", menuId).fetch().rowsUpdated();
    }

    /**
     * 批量插入菜单权限绑定
     */
    public Mono<Long> insertMenuPermissions(Long menuId, List<Long> permissionIds) {
        if (permissionIds.isEmpty()) return Mono.just(0L);

        StringBuilder sql = new StringBuilder("INSERT INTO sys_menu_permission (menu_id, permission_id, delete_flag, create_by, create_time, update_by, update_time) VALUES ");
        for (int i = 0; i < permissionIds.size(); i++) {
            sql.append("(:menuId, :permissionId").append(i).append(", 0, 'system', :now, 'system', :now)");
            if (i < permissionIds.size() - 1) sql.append(", ");
        }

        DatabaseClient.GenericExecuteSpec spec = client.sql(sql.toString());
        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < permissionIds.size(); i++) {
            spec = spec.bind("permissionId" + i, permissionIds.get(i));
        }
        spec = spec.bind("menuId", menuId).bind("now", now);

        return spec.fetch().rowsUpdated();
    }
}
