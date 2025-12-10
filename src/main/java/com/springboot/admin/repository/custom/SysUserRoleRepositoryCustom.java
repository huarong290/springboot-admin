package com.springboot.admin.repository.custom;


import com.springboot.admin.model.entity.sys.SysUserRole;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * 自定义用户角色关联 Repository
 *
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
}
