package com.springboot.admin.repository.custom;

import com.springboot.admin.model.entity.sys.SysDept;
import com.springboot.admin.model.entity.sys.SysUser;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * 自定义部门仓库类
 *
 * 封装部门相关的多表关联查询逻辑：
 *   - 部门 → 用户
 *
 * 单表操作由 SysDeptRepository (ReactiveCrudRepository) 负责，
 * 多表操作集中在此类中，保持分层清晰。
 */
@Repository
public class SysDeptRepositoryCustom {

    private final DatabaseClient client;

    public SysDeptRepositoryCustom(DatabaseClient client) {
        this.client = client;
    }
    /**
     * 根据组织ID查询部门列表
     *
     * @param orgId 组织ID
     * @return Flux<SysDept> 响应式流，返回该组织下的部门集合
     */
    public Flux<SysDept> findDeptListByOrgId(Long orgId) {
        String sql = "SELECT d.* FROM sys_dept d WHERE d.org_id = ? AND d.delete_flag = 0";

        return client.sql(sql)
                .bind(0, orgId)
                .map((row, meta) -> {
                    SysDept dept = new SysDept();
                    dept.setId(row.get("id", Long.class));
                    dept.setDeptName(row.get("dept_name", String.class));
                    dept.setDeptCode(row.get("dept_code", String.class));
                    return dept;
                })
                .all();
    }
}
