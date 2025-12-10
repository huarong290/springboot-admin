package com.springboot.admin.repository.custom;

import com.springboot.admin.model.entity.sys.SysRoleMenu;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * 自定义角色菜单关联 Repository
 *
 * 提供复杂查询逻辑，例如根据角色ID查询菜单关联关系。
 * 使用 R2DBC DatabaseClient 执行 SQL，返回响应式 Flux。
 */
@Repository
public class SysRoleMenuRepositoryCustom {

    private final DatabaseClient client;

    public SysRoleMenuRepositoryCustom(DatabaseClient client) {
        this.client = client;
    }

    /**
     * 根据角色ID查询菜单关联关系
     *
     * @param roleId 角色ID
     * @return Flux<SysRoleMenu> 响应式流，返回该角色绑定的菜单列表
     */
    public Flux<SysRoleMenu> findByRoleId(Long roleId) {
        String sql = "SELECT id, role_id, menu_id FROM sys_role_menu WHERE role_id = :roleId";

        return client.sql(sql)
                .bind("roleId", roleId)
                .map((row, metadata) -> {
                    SysRoleMenu roleMenu = new SysRoleMenu();
                    roleMenu.setId(row.get("id", Long.class));
                    roleMenu.setRoleId(row.get("role_id", Long.class));
                    roleMenu.setMenuId(row.get("menu_id", Long.class));
                    return roleMenu;
                })
                .all();
    }
}

