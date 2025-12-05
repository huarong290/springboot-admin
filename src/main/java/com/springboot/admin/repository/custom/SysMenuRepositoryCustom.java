package com.springboot.admin.repository.custom;

import com.springboot.admin.model.entity.sys.SysMenu;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Repository
public class SysMenuRepositoryCustom {

    private final DatabaseClient client;

    public SysMenuRepositoryCustom(DatabaseClient client) {
        this.client = client;
    }
    /**
     * 根据用户ID查询菜单列表
     *
     * @param userId 用户ID
     * @return 用户所拥有的菜单集合 (Flux<SysMenu>)
     */
    public Flux<SysMenu> getMenuListByUserId(Long userId) {
        String sql = "SELECT m.* FROM sys_menu m " +
                "INNER JOIN sys_role_menu rm ON m.id = rm.menu_id " +
                "INNER JOIN sys_user_role ur ON rm.role_id = ur.role_id " +
                "WHERE ur.user_id = ? AND m.delete_flag = 0";


        return client.sql(sql)
                .bind(0, userId)
                .map((row, meta) -> {
                    SysMenu menu = new SysMenu();
                    menu.setId(row.get("id", Long.class));
                    menu.setMenuParentId(row.get("menu_parent_id", Long.class));
                    menu.setMenuName(row.get("menu_name", String.class));
                    menu.setMenuPath(row.get("menu_path", String.class));
                    menu.setMenuComponent(row.get("menu_component", String.class));
                    menu.setMenuIcon(row.get("menu_icon", String.class));
                    menu.setMenuType(row.get("menu_type", Integer.class));
                    menu.setMenuPermission(row.get("menu_permission", String.class));
                    menu.setMenuSort(row.get("menu_sort", Integer.class));
                    menu.setMenuVisible(row.get("menu_visible", Integer.class));
                    menu.setMenuEnabled(row.get("menu_enabled", Integer.class));
                    menu.setDeleteFlag(row.get("delete_flag", Integer.class));
                    menu.setCreateBy(row.get("create_by", String.class));
                    menu.setCreateTime(
                            Optional.ofNullable(row.get("create_time", java.time.ZonedDateTime.class))
                                    .map(java.time.ZonedDateTime::toLocalDateTime)
                                    .orElse(null)
                    );
                    menu.setUpdateBy(row.get("update_by", String.class));
                    menu.setUpdateTime(
                            Optional.ofNullable(row.get("update_time", java.time.ZonedDateTime.class))
                                    .map(java.time.ZonedDateTime::toLocalDateTime)
                                    .orElse(null)
                    );
                    return menu;
                })
                .all();
    }
    /**
     * 根据角色ID查询菜单列表
     *
     * @param roleId 角色ID
     * @return Flux<SysMenu> 响应式流，返回该角色拥有的菜单集合
     */
    public Flux<SysMenu> getMenuListByRoleId(Long roleId) {
        String sql = "SELECT m.* FROM sys_menu m " +
                "INNER JOIN sys_role_menu rm ON m.id = rm.menu_id " +
                "WHERE rm.role_id = ? AND m.delete_flag = 0";

        return client.sql(sql)
                .bind(0, roleId)
                .map((row, meta) -> {
                    SysMenu menu = new SysMenu();
                    menu.setId(row.get("id", Long.class));
                    menu.setMenuParentId(row.get("menu_parent_id", Long.class));
                    menu.setMenuName(row.get("menu_name", String.class));
                    menu.setMenuPath(row.get("menu_path", String.class));
                    menu.setMenuComponent(row.get("menu_component", String.class));
                    menu.setMenuIcon(row.get("menu_icon", String.class));
                    menu.setMenuType(row.get("menu_type", Integer.class));
                    menu.setMenuPermission(row.get("menu_permission", String.class));
                    menu.setMenuSort(row.get("menu_sort", Integer.class));
                    menu.setMenuVisible(row.get("menu_visible", Integer.class));
                    menu.setMenuEnabled(row.get("menu_enabled", Integer.class));
                    menu.setDeleteFlag(row.get("delete_flag", Integer.class));
                    menu.setCreateBy(row.get("create_by", String.class));
                    menu.setCreateTime(
                            Optional.ofNullable(row.get("create_time", java.time.ZonedDateTime.class))
                                    .map(java.time.ZonedDateTime::toLocalDateTime)
                                    .orElse(null)
                    );
                    menu.setUpdateBy(row.get("update_by", String.class));
                    menu.setUpdateTime(
                            Optional.ofNullable(row.get("update_time", java.time.ZonedDateTime.class))
                                    .map(java.time.ZonedDateTime::toLocalDateTime)
                                    .orElse(null)
                    );
                    return menu;
                })
                .all();
    }


    /**
     * 删除单个菜单
     *
     * @param id 菜单ID
     * @return Mono<Integer> 返回受影响的行数
     */
    public Mono<Long> deleteMenuById(Long id) {
        String sql = "DELETE FROM sys_menu WHERE id = ?";
        return client.sql(sql)
                .bind(0, id)
                .fetch()
                .rowsUpdated();
    }

}
