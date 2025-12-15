package com.springboot.admin.repository.custom;

import com.springboot.admin.model.dto.user.SysUserDTO;
import com.springboot.admin.model.dto.user.SysUserQueryDTO;
import com.springboot.admin.model.entity.sys.SysRole;
import com.springboot.admin.model.entity.sys.SysUser;
import com.springboot.admin.model.vo.PageResult;
import com.springboot.admin.model.vo.user.SysUserVO;
import com.springboot.admin.utils.R2dbcHelperUtil;
import lombok.Getter;
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
 * 自定义用户仓库类
 *
 * 封装所有用户相关的自定义 SQL：
 *   - 用户 → 部门
 *   - 用户 → 角色
 *   - 用户 → 权限
 *   - 用户 → 菜单
 *   - 删除用户（单个/批量）
 *
 * 单表操作由 SysUserRepository (ReactiveCrudRepository) 负责，
 * 多表和复杂 SQL 操作集中在此类中，保持分层清晰。
 */
@Getter
@Repository
public class SysUserRepositoryCustom {

    /**
     * -- GETTER --
     *  提供 DatabaseClient 给 Service 层使用（如果需要扩展）
     */
    private final DatabaseClient client;

    public SysUserRepositoryCustom(DatabaseClient client) {
        this.client = client;
    }
    /**
     * 分页查询用户列表
     *
     * @param query 用户查询参数（包含分页和条件）
     * @return Mono<PageResult<SysUser>> 分页结果
     */
    public Mono<PageResult<SysUserVO>> pageUserList(SysUserQueryDTO query) {
        StringBuilder baseSql = new StringBuilder("FROM sys_user u WHERE u.delete_flag = 0 ");

        Map<String, Object> params = new HashMap<>();
        if (query.getUsername() != null && !query.getUsername().isBlank()) {
            baseSql.append("AND u.username LIKE :username ");
            params.put("username", "%" + query.getUsername() + "%");
        }
        if (query.getEmail() != null && !query.getEmail().isBlank()) {
            baseSql.append("AND u.email LIKE :email ");
            params.put("email", "%" + query.getEmail() + "%");
        }
        if (query.getPhone() != null && !query.getPhone().isBlank()) {
            baseSql.append("AND u.phone LIKE :phone ");
            params.put("phone", "%" + query.getPhone() + "%");
        }
        if (query.getDeptId() != null) {
            baseSql.append("AND u.dept_id = :deptId ");
            params.put("deptId", query.getDeptId());
        }

        return R2dbcHelperUtil.queryPage(
                client,
                baseSql.toString(),
                params,
                query.getPage(),
                query.getSize(),
                (row, meta) -> {
                    SysUserVO user = new SysUserVO();
                    user.setId(row.get("id", Long.class));
                    user.setUsername(row.get("username", String.class));
                    user.setNickname(row.get("nickname", String.class));
                    user.setEmail(row.get("email", String.class));
                    user.setPhone(row.get("phone", String.class));
                    user.setDeptId(row.get("dept_id", Long.class));
                    user.setOrgId(row.get("org_id", Long.class));
                    user.setStatus(row.get("status", Integer.class));
                    user.setLastLoginTime(
                            Optional.ofNullable(row.get("last_login_time", java.time.ZonedDateTime.class))
                                    .map(java.time.ZonedDateTime::toLocalDateTime)
                                    .orElse(null)
                    );
                    user.setCreateTime(
                            Optional.ofNullable(row.get("create_time", java.time.ZonedDateTime.class))
                                    .map(java.time.ZonedDateTime::toLocalDateTime)
                                    .orElse(null)
                    );
                    user.setUpdateTime(
                            Optional.ofNullable(row.get("update_time", java.time.ZonedDateTime.class))
                            .map(java.time.ZonedDateTime::toLocalDateTime)
                            .orElse(null));
                    return user;
                }
        );
    }
    /**
     * 根据部门ID查询用户列表
     *
     * @param deptId 部门ID
     * @return Flux<SysUser> 响应式流，返回该部门下的用户集合
     */
    public Flux<SysUser> findUsersByDeptId(Long deptId) {
        String sql = "SELECT u.* FROM sys_user u WHERE u.dept_id = ? AND u.delete_flag = 0";

        return client.sql(sql)
                .bind(0, deptId)
                .map((row, meta) -> {
                    SysUser user = new SysUser();
                    user.setId(row.get("id", Long.class));
                    user.setUsername(row.get("username", String.class));
                    user.setNickname(row.get("nickname", String.class));
                    user.setEmail(row.get("email", String.class));
                    return user;
                })
                .all();
    }

    /**
     * 根据用户ID查询角色列表
     *
     * @param userId 用户ID
     * @return Flux<SysRole> 响应式流，返回该用户所拥有的角色集合
     */
    public Flux<SysRole> findRolesByUserId(Long userId) {
        String sql = "SELECT r.* FROM sys_role r " +
                "INNER JOIN sys_user_role ur ON r.id = ur.role_id " +
                "WHERE ur.user_id = ? AND r.delete_flag = 0";

        return client.sql(sql)
                .bind(0, userId)
                .map((row, meta) -> {
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
     * 新增用户，返回生成的主键 ID
     */
    public Mono<Long> insertUser(SysUser user) {
        Map<String, Object> fieldMap = new LinkedHashMap<>();

        if (StringUtils.isNotBlank(user.getUsername())){
            fieldMap.put("username", user.getUsername());
        }
        if(StringUtils.isNotBlank(user.getPassword())){
            fieldMap.put("password", user.getPassword());
        }
        if(StringUtils.isNotBlank(user.getEmail())){
            fieldMap.put("email", user.getEmail());
        }
        if(StringUtils.isNotBlank(user.getPhone())){
            fieldMap.put("phone", user.getPhone());
        }
        if (user.getDeptId() != null) {
            fieldMap.put("dept_id", user.getDeptId());
        }
        if (user.getOrgId() != null) {
            fieldMap.put("org_id", user.getOrgId());
        }
        if (StringUtils.isNotBlank(user.getNickname())){
            fieldMap.put("nickname", user.getNickname());
        }
        if (user.getStatus() != null){
            fieldMap.put("status", user.getStatus());
        }
        if (user.getLastLoginTime() != null){
            fieldMap.put("last_login_time", user.getLastLoginTime());
        }
        if (user.getAvatar() != null){
            fieldMap.put("avatar", user.getAvatar());
        }
        // 固定插入时间
        fieldMap.put("create_time", LocalDateTime.now());
        fieldMap.put("update_time", LocalDateTime.now());

        return R2dbcHelperUtil.insertAndReturnId(client, "sys_user", fieldMap);
    }

    /**
     * 更新用户，返回更新成功的记录数
     */
    public Mono<Long> updateUser(SysUserDTO dto, String encodedPassword) {
        Map<String, Object> fieldMap = new LinkedHashMap<>();

        if (StringUtils.isNotBlank(dto.getUsername())){
            fieldMap.put("username", dto.getUsername());
        }
        if(StringUtils.isNotBlank(dto.getPassword())){
            fieldMap.put("password", dto.getPassword());
        }
        if(StringUtils.isNotBlank(dto.getEmail())){
            fieldMap.put("email", dto.getEmail());
        }
        if(StringUtils.isNotBlank(dto.getPhone())){
            fieldMap.put("phone", dto.getPhone());
        }
        if (dto.getDeptId() != null) {
            fieldMap.put("dept_id", dto.getDeptId());
        }
        if (dto.getOrgId() != null) {
            fieldMap.put("org_id", dto.getOrgId());
        }
        if (StringUtils.isNotBlank(dto.getNickname())){
            fieldMap.put("nickname", dto.getNickname());
        }
        if (dto.getStatus() != null){
            fieldMap.put("status", dto.getStatus());
        }
        if (dto.getAvatar() != null){
            fieldMap.put("avatar", dto.getAvatar());
        }
        // 更新时间
        fieldMap.put("update_time", LocalDateTime.now());

        return R2dbcHelperUtil.update(client, "sys_user", fieldMap, "id", dto.getId())
                .map(Long::valueOf); // rowsUpdated 返回 Mono<Integer>，这里转成 Long
    }




    /**
     * 删除单个用户
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
    public Mono<Long> deleteUserById(Long id, boolean logicalDelete) {
        if (logicalDelete) {
            // 逻辑删除：更新 delete_flag = 1
            String sql = "UPDATE sys_user SET " +
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
            String sql = "DELETE FROM sys_user WHERE id = ?";

            return client.sql(sql)
                    .bind(0, id)
                    .fetch()
                    .rowsUpdated()
                    .map(Long::valueOf);
        }
    }
    /**
     * 批量删除用户
     *
     * @param ids 用户ID集合
     * @return Mono<Integer> 返回受影响的行数
     */
    public Mono<Long> deleteUsersByIds(Iterable<Long> ids) {
        String sql = "DELETE FROM sys_user WHERE id IN (?)";
        return client.sql(sql)
                .bind(0, ids)
                .fetch()
                .rowsUpdated();
    }


    /**
     * 根据用户名查询单个用户
     *
     * @param username 用户名
     * @return Mono<SysUser> 响应式流，返回该用户
     */
    public Mono<SysUser> findByUsername(String username) {
        String sql = "SELECT u.* FROM sys_user u WHERE u.username = ? AND u.delete_flag = 0";

        return client.sql(sql)
                .bind(0, username)
                .map((row, meta) -> {
                    SysUser user = new SysUser();
                    user.setId(row.get("id", Long.class));
                    user.setUsername(row.get("username", String.class));
                    user.setNickname(row.get("nickname", String.class));
                    user.setEmail(row.get("email", String.class));
                    user.setPhone(row.get("phone", String.class));
                    user.setDeptId(row.get("dept_id", Long.class));
                    user.setOrgId(row.get("org_id", Long.class));
                    user.setPassword(row.get("password", String.class));
                    user.setAvatar(row.get("avatar", String.class));
                    user.setStatus(row.get("status", Integer.class));
                    user.setLastLoginTime(
                            Optional.ofNullable(row.get("last_login_time", java.time.ZonedDateTime.class))
                                    .map(java.time.ZonedDateTime::toLocalDateTime)
                                    .orElse(null)
                    );
                    return user;
                })
                .one(); // 返回单个结果
    }


    public Mono<Long> updateLastLoginTime(Long userId, LocalDateTime lastLoginTime) {
        return client.sql("UPDATE sys_user SET last_login_time = :lastLoginTime WHERE id = :userId")
                .bind("lastLoginTime", lastLoginTime)
                .bind("userId", userId)
                .fetch()
                .rowsUpdated();

    }
}
