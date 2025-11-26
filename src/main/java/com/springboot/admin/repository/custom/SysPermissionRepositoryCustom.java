package com.springboot.admin.repository.custom;

import com.springboot.admin.model.entity.sys.SysPermission;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;


/**
 * 自定义角色仓库类
 * <p>
 * 封装角色相关的多表关联查询逻辑：
 * - 角色 → 权限
 * - 角色 → 菜单
 */
@Repository
public class SysPermissionRepositoryCustom {
    private final DatabaseClient client;

    public SysPermissionRepositoryCustom(DatabaseClient client) {
        this.client = client;
    }


    /** 根据用户ID查询权限列表 */
    public Flux<SysPermission> listPermissionsByUserId(Long userId) {
        String sql = "SELECT p.* FROM sys_permission p " +
                "INNER JOIN sys_role_permission rp ON p.id = rp.permission_id " +
                "INNER JOIN sys_user_role ur ON rp.role_id = ur.role_id " +
                "WHERE ur.user_id = ? AND p.delete_flag = 0";
        return client.sql(sql).bind(0, userId)
                .map((row, meta) -> {
                    SysPermission p = new SysPermission();
                    p.setId(row.get("id", Long.class));
                    p.setPermissionCode(row.get("permission_code", String.class));
                    p.setPermissionName(row.get("permission_name", String.class));
                    p.setPermissionType(row.get("permission_type", Integer.class));
                    p.setDeleteFlag(row.get("delete_flag", Integer.class));
                    p.setCreateBy(row.get("create_by", String.class));
                    p.setCreateTime(row.get("create_time", java.time.ZonedDateTime.class).toLocalDateTime());
                    p.setUpdateBy(row.get("update_by", String.class));
                    p.setUpdateTime(row.get("update_time", java.time.ZonedDateTime.class).toLocalDateTime());
                    return p;
                })
                .all();


    }

    /**
     * 根据角色ID查询权限列表
     *
     * @param roleId 角色ID
     * @return Flux<SysPermission> 响应式流，返回该角色拥有的权限集合
     */
    public Flux<SysPermission> listPermissionsByRoleId(Long roleId) {
        String sql = "SELECT p.* FROM sys_permission p " +
                "INNER JOIN sys_role_permission rp ON p.id = rp.permission_id " +
                "WHERE rp.role_id = ? AND p.delete_flag = 0";
        return client.sql(sql).bind(0, roleId)
                .map((row, meta) -> {
                    SysPermission p = new SysPermission();
                    p.setId(row.get("id", Long.class));
                    p.setPermissionCode(row.get("permission_code", String.class));
                    p.setPermissionName(row.get("permission_name", String.class));
                    p.setPermissionType(row.get("permission_type", Integer.class));
                    p.setDeleteFlag(row.get("delete_flag", Integer.class));
                    p.setCreateBy(row.get("create_by", String.class));
                    p.setCreateTime(row.get("create_time", java.time.ZonedDateTime.class).toLocalDateTime());
                    p.setUpdateBy(row.get("update_by", String.class));
                    p.setUpdateTime(row.get("update_time", java.time.ZonedDateTime.class).toLocalDateTime());
                    return p;
                })
                .all();
    }
}