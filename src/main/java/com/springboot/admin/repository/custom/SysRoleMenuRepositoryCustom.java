package com.springboot.admin.repository.custom;

import com.springboot.admin.model.entity.sys.SysRoleMenu;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义角色菜单关联 Repository
 * <p>
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
    public Flux<SysRoleMenu> findMenuIdsByRoleId(Long roleId) {
        String sql = "SELECT id, role_id, menu_id " +
                "FROM sys_role_menu " +
                "WHERE role_id = ? AND delete_flag = 0";

        return client.sql(sql)
                .bind(0, roleId)
                .map((row, metadata) -> {
                    SysRoleMenu roleMenu = new SysRoleMenu();
                    roleMenu.setId(row.get("id", Long.class));
                    roleMenu.setRoleId(row.get("role_id", Long.class));
                    roleMenu.setMenuId(row.get("menu_id", Long.class));
                    return roleMenu;
                })
                .all();
    }


    /**
     * 删除角色菜单关联
     * <p>
     * 用途：
     * - 后台管理：逻辑删除（推荐，保留数据用于审计）
     * - 特殊场景：物理删除（彻底清除数据，例如测试数据清理）
     *
     * @param id            角色菜单关联ID
     * @param logicalDelete 是否逻辑删除
     *                      true  = 逻辑删除（delete_flag = 1）
     *                      false = 物理删除（DELETE）
     * @return Mono<Long> 响应式单对象，返回删除成功的记录数（通常为 1）
     */
    public Mono<Long> deleteRoleMenuById(Long id, boolean logicalDelete) {
        if (logicalDelete) {
            // 逻辑删除：更新 delete_flag = 1
            String sql = "UPDATE sys_role_menu SET " +
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
            String sql = "DELETE FROM sys_role_menu WHERE id = ?";

            return client.sql(sql)
                    .bind(0, id)
                    .fetch()
                    .rowsUpdated()
                    .map(Long::valueOf);
        }
    }

    /**
     * 删除角色菜单关联
     * <p>
     * 用途：
     * - 后台管理：逻辑删除（推荐，保留数据用于审计）
     * - 特殊场景：物理删除（彻底清除数据，例如测试数据清理）
     *
     * @param roleId        角色ID
     * @param menuIds       菜单ID集合
     * @param logicalDelete 是否逻辑删除
     *                      true  = 逻辑删除（delete_flag = 1）
     *                      false = 物理删除（DELETE）
     * @return Mono<Long> 响应式单对象，返回删除成功的记录数
     */
    public Mono<Long> deleteByRoleIdAndMenuIds(Long roleId, List<Long> menuIds, boolean logicalDelete) {
        if (menuIds == null || menuIds.isEmpty()) {
            return Mono.just(0L);
        }

        // 构造占位符 (?, ?, ?)
        String placeholders = menuIds.stream()
                .map(id -> "?")
                .collect(Collectors.joining(","));

        if (logicalDelete) {
            String sql = "UPDATE sys_role_menu SET delete_flag = 1, update_by = 'system', update_time = CURRENT_TIMESTAMP " +
                    "WHERE role_id = ? AND menu_id IN (" + placeholders + ") AND delete_flag = 0";

            var spec = client.sql(sql).bind(0, roleId);
            for (int i = 0; i < menuIds.size(); i++) {
                spec = spec.bind(i + 1, menuIds.get(i));
            }
            return spec.fetch().rowsUpdated().map(Long::valueOf);
        } else {
            String sql = "DELETE FROM sys_role_menu WHERE role_id = ? AND menu_id IN (" + placeholders + ")";

            var spec = client.sql(sql).bind(0, roleId);
            for (int i = 0; i < menuIds.size(); i++) {
                spec = spec.bind(i + 1, menuIds.get(i));
            }
            return spec.fetch().rowsUpdated().map(Long::valueOf);
        }
    }


    /**
     * 批量新增角色菜单关联
     *
     * 用途：
     * - 在增量绑定逻辑中，为某个角色新增一批菜单绑定关系
     * - 常用于前端传入的最新菜单集合中，数据库尚未存在的部分
     *
     * @param roleId  角色ID
     * @param menuIds 菜单ID集合（需要新增的）
     * @return Mono<Long> 响应式单对象，返回成功插入的记录数
     */
    public Mono<Long> insertRoleMenus(Long roleId, List<Long> menuIds) {
        if (menuIds == null || menuIds.isEmpty()) {
            return Mono.just(0L);
        }

        // 构造批量插入 SQL
        StringBuilder sql = new StringBuilder("INSERT INTO sys_role_menu (role_id, menu_id, create_by, create_time, delete_flag) VALUES ");
        for (int i = 0; i < menuIds.size(); i++) {
            sql.append("(?, ?, 'system', CURRENT_TIMESTAMP, 0)");
            if (i < menuIds.size() - 1) {
                sql.append(", ");
            }
        }

        var spec = client.sql(sql.toString());
        int bindIndex = 0;
        for (Long menuId : menuIds) {
            spec = spec.bind(bindIndex++, roleId)
                    .bind(bindIndex++, menuId);
        }

        return spec.fetch().rowsUpdated().map(Long::valueOf);
    }

}

