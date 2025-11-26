package com.springboot.admin.repository.custom;


import com.springboot.admin.model.entity.sys.SysRole;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * 自定义角色仓库类
 *
 * 用于封装复杂的多表关联查询逻辑，例如根据用户ID查询角色列表。
 * 单表操作由 SysRoleRepository (ReactiveCrudRepository) 负责，
 * 多表操作集中在此类中，保持分层清晰。
 */
@Repository
public class SysRoleRepositoryCustom {

    private final DatabaseClient client;

    public SysRoleRepositoryCustom(DatabaseClient client) {
        this.client = client;
    }

    /**
     * 根据用户ID查询角色列表
     *
     * @param userId 用户ID
     * @return 返回该用户所拥有的角色集合 (Flux<SysRole>)
     *
     * SQL逻辑：
     *  - sys_role 与 sys_user_role 进行 INNER JOIN
     *  - 过滤条件：user_id = ? 且 delete_flag = 0
     */
    public Flux<SysRole> findRolesByUserId(Long userId) {
        String sql = "SELECT r.* FROM sys_role r " +
                "INNER JOIN sys_user_role ur ON r.id = ur.role_id " +
                "WHERE ur.user_id = ? AND r.delete_flag = 0";

        return client.sql(sql)
                .bind(0, userId)   // MySQL → 使用位置参数绑定
                .map((row, metadata) -> {
                    SysRole role = new SysRole();
                    role.setId(row.get("id", Long.class));
                    role.setRoleName(row.get("role_name", String.class));
                    role.setRoleCode(row.get("role_code", String.class));
                    role.setRoleDescription(row.get("role_description", String.class));
                    return role;
                })
                .all();
    }


    /**
     * 根据权限ID查询角色列表
     *
     * @param permissionId 权限ID
     * @return Flux<SysRole> 响应式流，返回拥有该权限的角色集合
     */
    public Flux<SysRole> findRolesByPermissionId(Long permissionId) {
        String sql = "SELECT r.* FROM sys_role r " +
                "INNER JOIN sys_role_permission rp ON r.id = rp.role_id " +
                "WHERE rp.permission_id = ? AND r.delete_flag = 0";
        return client.sql(sql).bind(0, permissionId)
                .map((row, meta) -> {
                    SysRole role = new SysRole();
                    role.setId(row.get("id", Long.class));
                    role.setRoleName(row.get("role_name", String.class));
                    role.setRoleCode(row.get("role_code", String.class));
                    return role;
                }).all();
    }

    /**
     * 根据菜单ID查询角色列表
     *
     * @param menuId 菜单ID
     * @return Flux<SysRole> 响应式流，返回拥有该菜单的角色集合
     */
    public Flux<SysRole> findRolesByMenuId(Long menuId) {
        String sql = "SELECT r.* FROM sys_role r " +
                "INNER JOIN sys_role_menu rm ON r.id = rm.role_id " +
                "WHERE rm.menu_id = ? AND r.delete_flag = 0";
        return client.sql(sql).bind(0, menuId)
                .map((row, meta) -> {
                    SysRole role = new SysRole();
                    role.setId(row.get("id", Long.class));
                    role.setRoleName(row.get("role_name", String.class));
                    role.setRoleCode(row.get("role_code", String.class));
                    return role;
                }).all();
    }
}

