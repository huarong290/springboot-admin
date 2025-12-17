package com.springboot.admin.repository.custom;


import com.springboot.admin.model.dto.role.SysRoleDTO;
import com.springboot.admin.model.dto.role.SysRoleQueryDTO;
import com.springboot.admin.model.entity.sys.SysRole;
import com.springboot.admin.model.vo.PageResult;
import com.springboot.admin.model.vo.role.SysRoleVO;
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
     * 分页查询角色列表
     *
     * @param query 角色查询参数（包含分页和条件）
     * @return Mono<PageResult<SysRoleVO>> 分页结果
     */
    public Mono<PageResult<SysRoleVO>> pageRoleList(SysRoleQueryDTO query) {
        StringBuilder baseSql = new StringBuilder("FROM sys_role t WHERE t.delete_flag = 0 ");

        Map<String, Object> params = new HashMap<>();
        if (query.getRoleName() != null && !query.getRoleName().isBlank()) {
            baseSql.append("AND t.role_name LIKE :roleName ");
            params.put("roleName", "%" + query.getRoleName() + "%");
        }
        if (query.getRoleCode() != null && !query.getRoleCode().isBlank()) {
            baseSql.append("AND t.role_code LIKE :roleCode ");
            params.put("roleCode", "%" + query.getRoleCode() + "%");
        }
        if (query.getRoleStatus() != null) {
            baseSql.append("AND t.role_status = :status ");
            params.put("status", query.getRoleStatus());
        }
        if (query.getCreateTimeStart() != null && !query.getCreateTimeStart().isBlank()) {
            baseSql.append("AND t.create_time >= :createTimeStart ");
            params.put("createTimeStart", query.getCreateTimeStart());
        }
        if (query.getCreateTimeEnd() != null && !query.getCreateTimeEnd().isBlank()) {
            baseSql.append("AND t.create_time <= :createTimeEnd ");
            params.put("createTimeEnd", query.getCreateTimeEnd());
        }

        return R2dbcHelperUtil.queryPage(
                client,
                baseSql.toString(),
                params,
                query.getPage(),
                query.getSize(),
                (row, meta) -> {
                    SysRoleVO role = new SysRoleVO();
                    role.setId(row.get("id", Long.class));
                    role.setRoleName(row.get("role_name", String.class));
                    role.setRoleCode(row.get("role_code", String.class));
                    role.setRoleDescription(row.get("role_description", String.class));
                    role.setRoleStatus(row.get("role_status", Integer.class));
                    role.setCreateTime(
                            Optional.ofNullable(row.get("create_time", java.time.ZonedDateTime.class))
                                    .map(java.time.ZonedDateTime::toLocalDateTime)
                                    .orElse(null)
                    );
                    role.setUpdateTime(
                            Optional.ofNullable(row.get("update_time", java.time.ZonedDateTime.class))
                                    .map(java.time.ZonedDateTime::toLocalDateTime)
                                    .orElse(null)
                    );
                    return role;
                }
        );
    }
    /**
     * 新增角色，返回生成的主键 ID
     */
    public Mono<Long> insertRole(SysRoleDTO roleDTO) {
        Map<String, Object> fieldMap = new LinkedHashMap<>();
        // 基本字段
        if (StringUtils.isNotBlank(roleDTO.getRoleName())){
            fieldMap.put("role_name", roleDTO.getRoleName());
        }
        if (StringUtils.isNotBlank(roleDTO.getRoleCode())){
            fieldMap.put("role_code", roleDTO.getRoleCode());
        }
        if (StringUtils.isNotBlank(roleDTO.getRoleDescription())){
            fieldMap.put("role_description", roleDTO.getRoleDescription());
        }
        if (roleDTO.getRoleStatus() != null) {
            fieldMap.put("role_status", roleDTO.getRoleStatus());
        }
        // 固定插入时间
        fieldMap.put("create_time", LocalDateTime.now());
        fieldMap.put("update_time", LocalDateTime.now());

        return R2dbcHelperUtil.insertAndReturnId(client, "sys_role", fieldMap);
    }
    /**
     * 更新角色信息
     *
     * @param sysRole 角色实体（包含要更新的字段和主键 ID）
     * @return Mono<Integer> 受影响的行数
     */
    /**
     * 更新角色信息
     *
     * 用途：
     * - 后台管理：修改角色名称、编码、描述、启用状态等
     *
     * SQL逻辑：
     *  - 使用 UPDATE 语句更新 sys_role 表
     *  - 过滤条件：id = ? 且 delete_flag = 0
     *
     * @param dto 角色实体对象（包含需要更新的字段）
     * @return Mono<Long> 响应式单对象，返回更新成功的记录数
     */
    public Mono<Long> updateRole(SysRoleDTO dto) {
        Map<String, Object> fieldMap = new LinkedHashMap<>();

        // 基本字段（只更新非空）
        if (StringUtils.isNotBlank(dto.getRoleName())) {
            fieldMap.put("role_name", dto.getRoleName());
        }
        if (StringUtils.isNotBlank(dto.getRoleCode())) {
            fieldMap.put("role_code", dto.getRoleCode());
        }
        if (StringUtils.isNotBlank(dto.getRoleDescription())) {
            fieldMap.put("role_description", dto.getRoleDescription());
        }
        if (dto.getRoleStatus() != null) {
            fieldMap.put("role_status", dto.getRoleStatus());
        }

        // 更新时间固定刷新
        fieldMap.put("update_time", LocalDateTime.now());

        // 调用工具方法更新（根据主键 ID）
        return R2dbcHelperUtil.update(client, "sys_role", fieldMap, "id", dto.getId())
                .map(Long::valueOf);
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
                    role.setRoleStatus(row.get("role_status", Integer.class));
                    role.setDeleteFlag(row.get("delete_flag", Integer.class));
                    role.setCreateTime(
                            Optional.ofNullable(row.get("create_time", java.time.ZonedDateTime.class))
                                    .map(java.time.ZonedDateTime::toLocalDateTime)
                                    .orElse(null)
                    );
                    role.setUpdateTime(
                            Optional.ofNullable(row.get("update_time", java.time.ZonedDateTime.class))
                                    .map(java.time.ZonedDateTime::toLocalDateTime)
                                    .orElse(null)
                    );
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
                    role.setRoleDescription(row.get("role_description", String.class));
                    role.setRoleStatus(row.get("role_status", Integer.class));
                    role.setDeleteFlag(row.get("delete_flag", Integer.class));
                    role.setCreateTime(
                            Optional.ofNullable(row.get("create_time", java.time.ZonedDateTime.class))
                                    .map(java.time.ZonedDateTime::toLocalDateTime)
                                    .orElse(null)
                    );
                    role.setUpdateTime(
                            Optional.ofNullable(row.get("update_time", java.time.ZonedDateTime.class))
                                    .map(java.time.ZonedDateTime::toLocalDateTime)
                                    .orElse(null)
                    );
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
                    role.setRoleDescription(row.get("role_description", String.class));
                    role.setRoleStatus(row.get("role_status", Integer.class));
                    role.setDeleteFlag(row.get("delete_flag", Integer.class));
                    role.setCreateTime(
                            Optional.ofNullable(row.get("create_time", java.time.ZonedDateTime.class))
                                    .map(java.time.ZonedDateTime::toLocalDateTime)
                                    .orElse(null)
                    );
                    role.setUpdateTime(
                            Optional.ofNullable(row.get("update_time", java.time.ZonedDateTime.class))
                                    .map(java.time.ZonedDateTime::toLocalDateTime)
                                    .orElse(null)
                    );
                    return role;
                }).all();
    }


    /**
     * 物理删除角色（彻底删除）
     *
     * 用途：
     * - 特殊场景：需要彻底清除角色数据（例如测试数据清理）
     *
     * SQL逻辑：
     *  - 使用 DELETE 语句直接删除 sys_role 表中的记录
     *  - 过滤条件：id = ?
     *
     * @param id 角色ID
     * @return Mono<Long> 响应式单对象，返回删除成功的记录数（通常为 1）
     */
    public Mono<Long> deleteRolePhysicallyById(Long id) {
        String sql = "DELETE FROM sys_role WHERE id = ?";

        return client.sql(sql)
                .bind(0, id)
                .fetch()
                .rowsUpdated()
                .map(Long::valueOf);
    }
    /**
     * 删除角色
     *
     * 用途：
     * - 后台管理：逻辑删除（推荐，保留数据用于审计）
     * - 特殊场景：物理删除（彻底清除数据，例如测试数据清理）
     *
     * @param id 角色ID
     * @param logicalDelete 是否逻辑删除
     *                      true  = 逻辑删除（delete_flag = 1）
     *                      false = 物理删除（DELETE）
     * @return Mono<Long> 响应式单对象，返回删除成功的记录数（通常为 1）
     */
    public Mono<Long> deleteRoleById(Long id, boolean logicalDelete) {
        if (logicalDelete) {
            // 逻辑删除：更新 delete_flag = 1
            String sql = "UPDATE sys_role SET " +
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
            String sql = "DELETE FROM sys_role WHERE id = ?";

            return client.sql(sql)
                    .bind(0, id)
                    .fetch()
                    .rowsUpdated()
                    .map(Long::valueOf);
        }
    }

    /**
     * 批量删除角色
     *
     * 用途：
     * - 后台管理：逻辑删除（推荐，保留数据用于审计）
     * - 特殊场景：物理删除（彻底清除数据，例如测试数据清理）
     *
     * @param ids 角色ID集合
     * @param logicalDelete 是否逻辑删除
     *                      true  = 逻辑删除（delete_flag = 1）
     *                      false = 物理删除（DELETE）
     * @return Mono<Long> 响应式单对象，返回删除成功的记录数
     */
    public Mono<Long> deleteRolesByIds(Iterable<Long> ids, boolean logicalDelete) {
        StringBuilder sql;

        if (logicalDelete) {
            // 逻辑删除：更新 delete_flag = 1
            sql = new StringBuilder("UPDATE sys_role SET " +
                    "delete_flag = 1, " +
                    "update_by = 'system', " +
                    "update_time = CURRENT_TIMESTAMP " +
                    "WHERE delete_flag = 0 AND id IN (");
        } else {
            // 物理删除：直接 DELETE
            sql = new StringBuilder("DELETE FROM sys_role WHERE id IN (");
        }

        // 构建占位符 (?, ?, ?)
        int size = 0;
        for (Long id : ids) {
            if (size > 0) {
                sql.append(", ");
            }
            sql.append("?");
            size++;
        }
        sql.append(")");

        DatabaseClient.GenericExecuteSpec spec = client.sql(sql.toString());
        int index = 0;
        for (Long id : ids) {
            spec = spec.bind(index++, id);
        }

        return spec.fetch()
                .rowsUpdated()
                .map(Long::valueOf);
    }


}

