package com.springboot.admin.repository.custom;

import com.springboot.admin.model.dto.permission.SysPermissionDTO;
import com.springboot.admin.model.dto.permission.SysPermissionQueryDTO;
import com.springboot.admin.model.entity.sys.SysPermission;
import com.springboot.admin.model.vo.PageResult;
import com.springboot.admin.model.vo.permission.SysPermissionVO;
import com.springboot.admin.utils.R2dbcHelperUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;


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
    /**
     * 分页查询权限列表
     *
     * @param query 权限查询参数（包含分页和条件）
     * @return Mono<PageResult<SysPermissionVO>> 分页结果
     */
    public Mono<PageResult<SysPermissionVO>> pagePermissionList(SysPermissionQueryDTO query) {
        // 基础 SQL，过滤掉逻辑删除的数据
        StringBuilder baseSql = new StringBuilder("FROM sys_permission t WHERE t.delete_flag = 0 ");

        // 参数 Map，用于绑定查询条件
        Map<String, Object> params = new HashMap<>();

        // 按权限编码模糊查询
        if (StringUtils.isNotBlank(query.getPermissionCode())) {
            baseSql.append("AND t.permission_code LIKE :permissionCode ");
            params.put("permissionCode", "%" + query.getPermissionCode() + "%");
        }

        // 按权限名称模糊查询
        if (StringUtils.isNotBlank(query.getPermissionName())) {
            baseSql.append("AND t.permission_name LIKE :permissionName ");
            params.put("permissionName", "%" + query.getPermissionName() + "%");
        }

        // 按权限类型精确查询
        if (query.getPermissionType() != null) {
            baseSql.append("AND t.permission_type = :permissionType ");
            params.put("permissionType", query.getPermissionType());
        }

        // 按权限状态精确查询
        if (query.getPermissionStatus() != null) {
            baseSql.append("AND t.permission_status = :permissionStatus ");
            params.put("permissionStatus", query.getPermissionStatus());
        }

        // 按创建时间起始范围查询
        if (StringUtils.isNotBlank(query.getCreateTimeStart()) ) {
            baseSql.append("AND t.create_time >= :createTimeStart ");
            params.put("createTimeStart", query.getCreateTimeStart());
        }

        // 按创建时间结束范围查询
        if (StringUtils.isNotBlank(query.getCreateTimeEnd()) ) {
            baseSql.append("AND t.create_time <= :createTimeEnd ");
            params.put("createTimeEnd", query.getCreateTimeEnd());
        }

        // 调用工具类执行分页查询
        return R2dbcHelperUtil.queryPage(
                client,
                baseSql.toString(),
                params,
                query.getPage(),
                query.getSize(),
                (row, meta) -> {
                    SysPermissionVO permission = new SysPermissionVO();
                    permission.setId(row.get("id", Long.class));
                    permission.setPermissionCode(row.get("permission_code", String.class));
                    permission.setPermissionName(row.get("permission_name", String.class));
                    permission.setPermissionType(row.get("permission_type", Integer.class));
                    permission.setPermissionStatus(row.get("permission_status", Integer.class));
                    permission.setCreateTime(
                            Optional.ofNullable(row.get("create_time", java.time.ZonedDateTime.class))
                                    .map(java.time.ZonedDateTime::toLocalDateTime)
                                    .orElse(null)
                    );
                    permission.setUpdateTime(
                            Optional.ofNullable(row.get("update_time", java.time.ZonedDateTime.class))
                                    .map(java.time.ZonedDateTime::toLocalDateTime)
                                    .orElse(null)
                    );
                    return permission;
                }
        );
    }



    /**
     * 根据用户ID查询权限列表
     */
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
    /**
     * 新增权限，返回生成的主键 ID
     */
    public Mono<Long> insertPermission(SysPermissionDTO sysPermissionDTO) {
        Map<String, Object> fieldMap = new LinkedHashMap<>();

        if (StringUtils.isNotBlank(sysPermissionDTO.getPermissionCode())) {
            fieldMap.put("permission_code", sysPermissionDTO.getPermissionCode());
        }
        if (StringUtils.isNotBlank(sysPermissionDTO.getPermissionName())) {
            fieldMap.put("permission_name", sysPermissionDTO.getPermissionName());
        }
        if (sysPermissionDTO.getPermissionType() != null) {
            fieldMap.put("permission_type", sysPermissionDTO.getPermissionType());
        }
        if (sysPermissionDTO.getPermissionStatus() != null) {
            fieldMap.put("permission_status", sysPermissionDTO.getPermissionStatus());
        }

        // 固定插入时间和创建者
        fieldMap.put("create_by", "system");
        fieldMap.put("create_time", LocalDateTime.now());
        fieldMap.put("update_by", "system");
        fieldMap.put("update_time", LocalDateTime.now());

        return R2dbcHelperUtil.insertAndReturnId(client, "sys_permission", fieldMap);
    }


    /**
     * 更新权限信息，返回受影响的行数
     */
    /**
     * 更新权限，根据主键 ID 更新非空字段
     */
    public Mono<Long> updatePermission(SysPermissionDTO sysPermissionDTO) {
        Map<String, Object> fieldMap = new LinkedHashMap<>();

        if (StringUtils.isNotBlank(sysPermissionDTO.getPermissionCode())) {
            fieldMap.put("permission_code", sysPermissionDTO.getPermissionCode());
        }
        if (StringUtils.isNotBlank(sysPermissionDTO.getPermissionName())) {
            fieldMap.put("permission_name", sysPermissionDTO.getPermissionName());
        }
        if (sysPermissionDTO.getPermissionType() != null) {
            fieldMap.put("permission_type", sysPermissionDTO.getPermissionType());
        }
        if (sysPermissionDTO.getPermissionStatus() != null) {
            fieldMap.put("permission_status", sysPermissionDTO.getPermissionStatus());
        }

        // 更新时间固定更新
        fieldMap.put("update_by", "system");
        fieldMap.put("update_time", LocalDateTime.now());

        return R2dbcHelperUtil.update(client, "sys_permission", fieldMap, "id", sysPermissionDTO.getId());
    }


    /**
     * 删除权限
     * <p>
     * 用途：
     * - 后台管理：逻辑删除（推荐，保留数据用于审计）
     * - 特殊场景：物理删除（彻底清除数据，例如测试数据清理）
     *
     * @param id            权限ID
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