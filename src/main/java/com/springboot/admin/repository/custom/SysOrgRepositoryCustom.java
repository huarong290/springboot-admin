package com.springboot.admin.repository.custom;

import com.springboot.admin.model.entity.sys.SysDept;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * 自定义组织仓库类
 * <p>
 * 封装组织相关的多表关联查询逻辑：
 * - 组织 → 部门
 * <p>
 * 单表操作由 SysOrgRepository (ReactiveCrudRepository) 负责，
 * 多表操作集中在此类中，保持分层清晰。
 */
@Repository
public class SysOrgRepositoryCustom {

    private final DatabaseClient client;

    public SysOrgRepositoryCustom(DatabaseClient client) {
        this.client = client;
    }

    /**
     * 根据组织ID查询部门列表
     *
     * @param orgId 组织ID
     * @return 该组织下的部门集合 (Flux<SysDept>)
     */
    public Flux<SysDept> findDeptsByOrgId(Long orgId) {
        String sql = "SELECT d.* FROM sys_dept d " +
                "WHERE d.org_id = ? AND d.delete_flag = 0";

        return client.sql(sql)
                .bind(0, orgId)
                .map((row, meta) -> {
                    SysDept dept = new SysDept();
                    dept.setId(row.get("id", Long.class));
                    dept.setDeptName(row.get("dept_name", String.class));
                    dept.setDeptCode(row.get("dept_code", String.class));
                    dept.setLeader(row.get("leader", String.class));
                    return dept;
                })
                .all();
    }
}
