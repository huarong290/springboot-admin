package com.springboot.admin.repository.custom;


import com.springboot.admin.model.entity.sys.SysUserRole;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 自定义用户角色关联 Repository
 * <p>
 * 提供复杂查询逻辑，例如根据用户ID查询角色关联关系。
 * 使用 R2DBC DatabaseClient 执行 SQL，返回响应式 Flux。
 */
@Repository
public class SysUserRoleRepositoryCustom {

    private final DatabaseClient client;

    public SysUserRoleRepositoryCustom(DatabaseClient client) {
        this.client = client;
    }

    /**
     * 根据用户ID查询角色关联关系
     *
     * @param userId 用户ID
     * @return Flux<SysUserRole> 响应式流，返回该用户绑定的角色列表
     */
    public Flux<SysUserRole> findByUserId(Long userId) {
        String sql = "SELECT id, user_id, role_id FROM sys_user_role WHERE user_id = :userId";

        return client.sql(sql)
                .bind("userId", userId)
                .map((row, metadata) -> {
                    SysUserRole userRole = new SysUserRole();
                    userRole.setId(row.get("id", Long.class));
                    userRole.setUserId(row.get("user_id", Long.class));
                    userRole.setRoleId(row.get("role_id", Long.class));
                    return userRole;
                })
                .all();
    }


    /**
     * 删除用户角色
     * <p>
     * 用途：
     * - 后台管理：逻辑删除（推荐，保留数据用于审计）
     * - 特殊场景：物理删除（彻底清除数据，例如测试数据清理）
     *
     * @param id            角色ID
     * @param logicalDelete 是否逻辑删除
     *                      true  = 逻辑删除（delete_flag = 1）
     *                      false = 物理删除（DELETE）
     * @return Mono<Long> 响应式单对象，返回删除成功的记录数（通常为 1）
     */
    public Mono<Long> deleteUserRoleById(Long id, boolean logicalDelete) {
        if (logicalDelete) {
            // 逻辑删除：更新 delete_flag = 1
            String sql = "UPDATE sys_user_role SET " +
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
            String sql = "DELETE FROM sys_user_role WHERE id = ?";

            return client.sql(sql)
                    .bind(0, id)
                    .fetch()
                    .rowsUpdated()
                    .map(Long::valueOf);
        }
    }

}
