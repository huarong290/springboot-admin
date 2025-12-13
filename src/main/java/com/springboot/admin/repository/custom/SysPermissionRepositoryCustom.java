package com.springboot.admin.repository.custom;

import com.springboot.admin.model.entity.sys.SysPermission;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


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
                    p.setPermissionStatus(row.get("permission_status", Integer.class));
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
                    p.setPermissionStatus(row.get("permission_status", Integer.class));
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
     * 新增权限，返回生成的主键 ID
     */
    public Mono<Long> insertPermission(SysPermission sysPermission) {
        String sql = "INSERT INTO sys_permission " +
                "(permission_code, permission_name, permission_type,permission_status, create_by, update_by) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        return client.sql(sql)
                .bind(0, sysPermission.getPermissionCode())
                .bind(1, sysPermission.getPermissionName())
                .bind(2, sysPermission.getPermissionType())
                .bind(3, sysPermission.getPermissionStatus())
                .bind(4, sysPermission.getCreateBy())
                .bind(5, sysPermission.getUpdateBy())
                .filter(statement -> statement.returnGeneratedValues("id"))
                .fetch()
                .first()
                .map(row -> (Long) row.get("id"));
    }

    /**
     * 更新权限信息，返回受影响的行数
     */
    public Mono<Long> updatePermission(SysPermission sysPermission) {
        String sql = "UPDATE sys_permission SET " +
                "permission_code = ?, " +
                "permission_name = ?, " +
                "permission_type = ?, " +
                "permission_status = ?, " +
                "update_by = ?, " +
                "update_time = NOW() " +
                "WHERE id = ?";
        return client.sql(sql)
                .bind(0, sysPermission.getPermissionCode())
                .bind(1, sysPermission.getPermissionName())
                .bind(2, sysPermission.getPermissionType())
                .bind(3, sysPermission.getPermissionStatus())
                .bind(4, sysPermission.getUpdateBy())
                .bind(5, sysPermission.getId())
                .fetch()
                .rowsUpdated()
                .map(Long::valueOf); // 转换为 Mono<Long>
    }

    /**
     * 删除权限
     *
     * 用途：
     * - 后台管理：逻辑删除（推荐，保留数据用于审计）
     * - 特殊场景：物理删除（彻底清除数据，例如测试数据清理）
     *
     * @param id 权限ID
     * @param logicalDelete 是否逻辑删除
     *                      true  = 逻辑删除（delete_flag = 1）
     *                      false = 物理删除（DELETE）
     * @return Mono<Long> 响应式单对象，返回删除成功的记录数（通常为 1）
     */
    public Mono<Long> deletePermissionById(Long id, boolean logicalDelete) {
        if (logicalDelete) {
            // 逻辑删除：更新 delete_flag = 1
            String sql = "UPDATE sys_permission SET " +
                    "delete_flag = 1, " +
                    "update_by = 'system', " +
                    "update_time = CURRENT_TIMESTAMP " +
                    "WHERE id = ? AND delete_flag = 0";

            return client.sql(sql)
                    .bind(0, id)
                    .fetch()
                    .rowsUpdated()
                    .map(Long::valueOf);
        } else {
            // 物理删除：直接 DELETE
            String sql = "DELETE FROM sys_permission WHERE id = ?";

            return client.sql(sql)
                    .bind(0, id)
                    .fetch()
                    .rowsUpdated()
                    .map(Long::valueOf);
        }
    }

}