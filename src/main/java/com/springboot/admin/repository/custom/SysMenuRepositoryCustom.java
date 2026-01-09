package com.springboot.admin.repository.custom;

import com.springboot.admin.model.dto.menu.SysMenuDTO;
import com.springboot.admin.model.entity.sys.SysMenu;
import com.springboot.admin.utils.R2dbcHelperUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class SysMenuRepositoryCustom {

    private final DatabaseClient client;

    public SysMenuRepositoryCustom(DatabaseClient client) {
        this.client = client;
    }

    /**
     * 新增菜单，返回生成的主键 ID
     */
    public Mono<Long> insertMenu(SysMenuDTO sysMenuDTO) {
        // 使用 LinkedHashMap 保持字段插入顺序
        Map<String, Object> fieldMap = new LinkedHashMap<>();

        // 菜单名称（必填）
        if (StringUtils.isNotBlank(sysMenuDTO.getMenuName())) {
            fieldMap.put("menu_name", sysMenuDTO.getMenuName());
        }

        // 父菜单 ID（顶级菜单为 0）
        if (sysMenuDTO.getMenuParentId() != null) {
            fieldMap.put("menu_parent_id", sysMenuDTO.getMenuParentId());
        }

        // 路由路径
        if (StringUtils.isNotBlank(sysMenuDTO.getMenuPath())) {
            fieldMap.put("menu_path", sysMenuDTO.getMenuPath());
        }

        // 前端组件路径
        if (StringUtils.isNotBlank(sysMenuDTO.getMenuComponent())) {
            fieldMap.put("menu_component", sysMenuDTO.getMenuComponent());
        }

        // 菜单图标
        if (StringUtils.isNotBlank(sysMenuDTO.getMenuIcon())) {
            fieldMap.put("menu_icon", sysMenuDTO.getMenuIcon());
        }

        // 菜单类型（0=目录，1=菜单，2=按钮）
        if (sysMenuDTO.getMenuType() != null) {
            fieldMap.put("menu_type", sysMenuDTO.getMenuType());
        }

        // 权限标识
        if (StringUtils.isNotBlank(sysMenuDTO.getMenuPermission())) {
            fieldMap.put("menu_permission", sysMenuDTO.getMenuPermission());
        }

        // 排序值
        if (sysMenuDTO.getMenuSort() != null) {
            fieldMap.put("menu_sort", sysMenuDTO.getMenuSort());
        }

        // 是否显示（1=显示，0=隐藏）
        if (sysMenuDTO.getMenuVisible() != null) {
            fieldMap.put("menu_visible", sysMenuDTO.getMenuVisible());
        }

        // 是否启用（1=启用，0=禁用）
        if (sysMenuDTO.getMenuStatus() != null) {
            fieldMap.put("menu_status", sysMenuDTO.getMenuStatus());
        }

        // 固定插入时间
        fieldMap.put("create_time", LocalDateTime.now());
        fieldMap.put("update_time", LocalDateTime.now());

        // 调用工具方法执行插入，并返回生成的主键 ID
        return R2dbcHelperUtil.insertAndReturnId(client, "sys_menu", fieldMap);
    }

    /**
     * 更新菜单，根据主键 ID 更新非空字段
     */
    public Mono<Long> updateMenu(SysMenuDTO sysMenuDTO) {
        // 使用 LinkedHashMap 保持字段顺序
        Map<String, Object> fieldMap = new LinkedHashMap<>();

        // 菜单名称
        if (StringUtils.isNotBlank(sysMenuDTO.getMenuName())) {
            fieldMap.put("menu_name", sysMenuDTO.getMenuName());
        }

        // 父菜单 ID
        if (sysMenuDTO.getMenuParentId() != null) {
            fieldMap.put("menu_parent_id", sysMenuDTO.getMenuParentId());
        }

        // 路由路径
        if (StringUtils.isNotBlank(sysMenuDTO.getMenuPath())) {
            fieldMap.put("menu_path", sysMenuDTO.getMenuPath());
        }

        // 前端组件路径
        if (StringUtils.isNotBlank(sysMenuDTO.getMenuComponent())) {
            fieldMap.put("menu_component", sysMenuDTO.getMenuComponent());
        }

        // 菜单图标
        if (StringUtils.isNotBlank(sysMenuDTO.getMenuIcon())) {
            fieldMap.put("menu_icon", sysMenuDTO.getMenuIcon());
        }

        // 菜单类型
        if (sysMenuDTO.getMenuType() != null) {
            fieldMap.put("menu_type", sysMenuDTO.getMenuType());
        }

        // 权限标识
        if (StringUtils.isNotBlank(sysMenuDTO.getMenuPermission())) {
            fieldMap.put("menu_permission", sysMenuDTO.getMenuPermission());
        }

        // 排序值
        if (sysMenuDTO.getMenuSort() != null) {
            fieldMap.put("menu_sort", sysMenuDTO.getMenuSort());
        }

        // 是否显示
        if (sysMenuDTO.getMenuVisible() != null) {
            fieldMap.put("menu_visible", sysMenuDTO.getMenuVisible());
        }

        // 是否启用
        if (sysMenuDTO.getMenuStatus() != null) {
            fieldMap.put("menu_status", sysMenuDTO.getMenuStatus());
        }

        // 更新时间固定更新
        fieldMap.put("update_time", LocalDateTime.now());

        // 执行更新操作，返回影响的行数
        return R2dbcHelperUtil.update(client, "sys_menu", fieldMap, "id", sysMenuDTO.getId())
                .map(Long::valueOf); // rowsUpdated 返回 Mono<Integer>，这里转成 Long
    }

    /**
     * 删除单个菜单
     * <p>
     * 用途：
     * - 后台管理：逻辑删除（推荐，保留数据用于审计）
     * - 特殊场景：物理删除（彻底清除数据，例如测试数据清理）
     *
     * @param id            菜单ID
     * @param logicalDelete 是否逻辑删除
     *                      true  = 逻辑删除（delete_flag = 1）
     *                      false = 物理删除（DELETE）
     * @return Mono<Long> 响应式单对象，返回删除成功的记录数（通常为 1）
     */
    public Mono<Long> deleteMenuById(Long id, boolean logicalDelete) {
        if (logicalDelete) {
            // 逻辑删除：更新 delete_flag = 1
            String sql = "UPDATE sys_menu SET " +
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
            String sql = "DELETE FROM sys_menu WHERE id = ?";

            return client.sql(sql)
                    .bind(0, id)
                    .fetch()
                    .rowsUpdated()
                    .map(Long::valueOf);
        }
    }


    /**
     * 根据用户ID查询菜单列表（基于权限表改造版）
     *
     * 用途：
     * - 用户登录后，通过角色ID查询其权限，再通过菜单权限表映射到菜单。
     * - 保证菜单和权限解耦，符合 RBAC + 菜单-权限映射的设计。
     *
     * 流程：
     * 1. 用户 → 角色 (sys_user_role)
     * 2. 角色 → 权限 (sys_role_permission + sys_permission)
     * 3. 权限 → 菜单 (sys_menu_permission + sys_menu)
     *
     * @param userId 用户ID
     * @return Flux<SysMenu> 用户所拥有的菜单集合
     */
    public Flux<SysMenu> getMenuListByUserId(Long userId) {
        String sql = "WITH RECURSIVE menu_cte AS ( SELECT DISTINCT m.* FROM sys_menu m INNER JOIN sys_menu_permission mp ON m.id = mp.menu_id INNER JOIN sys_permission p ON mp.permission_id = p.id INNER JOIN sys_role_permission rp ON p.id = rp.permission_id INNER JOIN sys_user_role ur ON rp.role_id = ur.role_id WHERE ur.user_id = ? AND m.delete_flag = 0 AND p.delete_flag = 0 AND p.permission_status = 1 AND rp.delete_flag = 0 UNION SELECT parent.* FROM sys_menu parent INNER JOIN menu_cte child ON parent.id = child.menu_parent_id WHERE parent.delete_flag = 0 ) SELECT DISTINCT * FROM menu_cte ORDER BY menu_sort";

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
                    menu.setMenuSort(row.get("menu_sort", Integer.class));
                    menu.setMenuVisible(row.get("menu_visible", Integer.class));
                    menu.setMenuStatus(row.get("menu_status", Integer.class));
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
//                    menu.setMenuPermission(row.get("menu_permission", String.class));
                    menu.setMenuSort(row.get("menu_sort", Integer.class));
                    menu.setMenuVisible(row.get("menu_visible", Integer.class));
                    menu.setMenuStatus(row.get("menu_status", Integer.class));
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

}
