package com.springboot.admin.repository.custom;

import com.springboot.admin.model.entity.sys.SysRole;
import com.springboot.admin.model.entity.sys.SysPermission;
import com.springboot.admin.model.entity.sys.SysMenu;
import com.springboot.admin.model.entity.sys.SysUser;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * 自定义用户仓库类
 *
 * 封装用户相关的多表关联查询逻辑：
 *   - 用户 → 角色
 *   - 用户 → 权限
 *   - 用户 → 菜单
 *
 * 单表操作由 SysUserRepository (ReactiveCrudRepository) 负责，
 * 多表操作集中在此类中，保持分层清晰。
 */
@Repository
public class SysUserRepositoryCustom {

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
}
