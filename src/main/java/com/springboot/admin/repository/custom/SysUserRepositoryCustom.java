package com.springboot.admin.repository.custom;

import com.springboot.admin.model.dto.user.SysUserDTO;
import com.springboot.admin.model.entity.sys.SysRole;
import com.springboot.admin.model.entity.sys.SysUser;
import lombok.Getter;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
        String sql = """
        INSERT INTO sys_user (username, password, email, phone, dept_id, org_id, nickname, status, avatar, create_time, update_time)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())
        """;

        return client.sql(sql)
                .bind(0, user.getUsername())
                .bind(1, user.getPassword())
                .bind(2, user.getEmail())
                .bind(3, user.getPhone())
                .bind(4, user.getDeptId())
                .bind(5, user.getOrgId())
                .bind(6, user.getNickname())
                .bind(7, user.getStatus())
                .bind(8, user.getAvatar())
                .filter(statement -> statement.returnGeneratedValues("id"))
                .fetch()
                .first()
                .map(row -> (Long) row.get("id"));
    }

    /**
     * 更新用户，返回更新成功的记录数
     */
    public Mono<Long> updateUser(SysUserDTO dto, String encodedPassword) {
        String sql = """
        UPDATE sys_user
        SET username = ?,
            password = ?,
            email = ?,
            phone = ?,
            dept_id = ?,
            org_id = ?,
            nickname = ?,
            enabled = ?,
            avatar = ?,
            update_time = NOW()
        WHERE id = ?
        """;

        return client.sql(sql)
                .bind(0, dto.getUsername())
                .bind(1, encodedPassword)
                .bind(2, dto.getEmail())
                .bind(3, dto.getPhone())
                .bind(4, dto.getDeptId())
                .bind(5, dto.getOrgId())
                .bind(6, dto.getNickname())
                .bind(7, dto.getStatus())
                .bind(8, dto.getAvatar())
                .bind(9, dto.getId())
                .fetch()
                .rowsUpdated()
                // 返回更新的记录数（通常为 1）
                .map(Long::valueOf);
    }


    /**
     * 删除单个用户
     *
     * @param id 用户ID
     * @return Mono<Integer> 返回受影响的行数
     */
    public Mono<Long> deleteUserById(Long id) {
        String sql = "DELETE FROM sys_user WHERE id = ?";
        return client.sql(sql)
                .bind(0, id)
                .fetch()
                .rowsUpdated();
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
     * 根据用户名查询用户列表
     *
     * @param username 用户名
     * @return Flux<SysUser> 响应式流，返回该部门下的用户集合
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
                    return user;
                });
    }
}
