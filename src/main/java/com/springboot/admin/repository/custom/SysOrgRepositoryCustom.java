package com.springboot.admin.repository.custom;

import com.springboot.admin.model.dto.org.SysOrgDTO;
import com.springboot.admin.model.dto.org.SysOrgQueryDTO;
import com.springboot.admin.model.entity.sys.SysDept;
import com.springboot.admin.model.vo.PageResult;
import com.springboot.admin.model.vo.org.SysOrgTreeVO;
import com.springboot.admin.model.vo.org.SysOrgVO;
import com.springboot.admin.utils.R2dbcHelperUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.*;

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
     * 分页查询组织列表
     *
     * @param query 组织查询参数（包含分页和条件）
     * @return Mono<PageResult<SysOrgVO>> 分页结果
     */
    public Mono<PageResult<SysOrgVO>> pageOrgList(SysOrgQueryDTO query) {
        StringBuilder baseSql = new StringBuilder("FROM sys_org t WHERE t.delete_flag = 0 ");

        Map<String, Object> params = new HashMap<>();
        if (query.getOrgName() != null && !query.getOrgName().isBlank()) {
            baseSql.append("AND t.org_name LIKE :orgName ");
            params.put("orgName", "%" + query.getOrgName() + "%");
        }
        if (query.getOrgCode() != null && !query.getOrgCode().isBlank()) {
            baseSql.append("AND t.org_code LIKE :orgCode ");
            params.put("orgCode", "%" + query.getOrgCode() + "%");
        }
        if (query.getOrgStatus() != null) {
            baseSql.append("AND t.org_status = :orgStatus ");
            params.put("org_status", query.getOrgStatus());
        }

        return R2dbcHelperUtil.queryPage(
                client,
                baseSql.toString(),
                params,
                query.getPage(),
                query.getSize(),
                (row, meta) -> {
                    SysOrgVO org = new SysOrgVO();
                    org.setId(row.get("id", Long.class));
                    org.setOrgName(row.get("org_name", String.class));
                    org.setOrgCode(row.get("org_code", String.class));
                    org.setParentId(row.get("parent_id", Long.class));
                    org.setOrgSort(row.get("org_sort", Integer.class));
                    org.setOrgStatus(row.get("org_status", Integer.class));
                    org.setCreateTime(
                            Optional.ofNullable(row.get("create_time", java.time.ZonedDateTime.class))
                                    .map(java.time.ZonedDateTime::toLocalDateTime)
                                    .orElse(null)
                    );
                    org.setUpdateTime(
                            Optional.ofNullable(row.get("update_time", java.time.ZonedDateTime.class))
                                    .map(java.time.ZonedDateTime::toLocalDateTime)
                                    .orElse(null)
                    );
                    return org;
                }
        );
    }
    /**
     * 新增组织，返回生成的主键 ID
     */
    public Mono<Long> insertOrg(SysOrgDTO dto) {
        Map<String, Object> fieldMap = new LinkedHashMap<>();

        if (StringUtils.isNotBlank(dto.getOrgName())) {
            fieldMap.put("org_name", dto.getOrgName());
        }
        if (StringUtils.isNotBlank(dto.getOrgCode())) {
            fieldMap.put("org_code", dto.getOrgCode());
        }
        if (dto.getParentId() != null) {
            fieldMap.put("parent_id", dto.getParentId());
        }
        if (dto.getOrgSort() != null) {
            fieldMap.put("org_sort", dto.getOrgSort());
        }
        if (dto.getOrgStatus() != null) {
            fieldMap.put("org_status", dto.getOrgStatus());
        }
        fieldMap.put("create_time", LocalDateTime.now());
        fieldMap.put("update_time", LocalDateTime.now());

        return R2dbcHelperUtil.insertAndReturnId(client, "sys_org", fieldMap);
    }

    /**
     * 更新组织，返回更新成功的记录数
     */
    public Mono<Long> updateOrg(SysOrgDTO dto) {
        Map<String, Object> fieldMap = new LinkedHashMap<>();

        if (StringUtils.isNotBlank(dto.getOrgName())) {
            fieldMap.put("org_name", dto.getOrgName());
        }
        if (StringUtils.isNotBlank(dto.getOrgCode())) {
            fieldMap.put("org_code", dto.getOrgCode());
        }
        if (dto.getParentId() != null) {
            fieldMap.put("parent_id", dto.getParentId());
        }
        if (dto.getOrgSort() != null) {
            fieldMap.put("org_sort", dto.getOrgSort());
        }

        if (dto.getOrgStatus() != null) {
            fieldMap.put("org_status", dto.getOrgStatus());
        }
        fieldMap.put("update_time", LocalDateTime.now());

        return R2dbcHelperUtil.update(client, "sys_org", fieldMap, "id", dto.getId())
                .map(Long::valueOf);
    }

    /**
     * 删除组织
     */
    public Mono<Long> deleteOrgById(Long id, boolean logicalDelete) {
        if (logicalDelete) {
            String sql = "UPDATE sys_org SET delete_flag = 1, update_by = 'system', update_time = CURRENT_TIMESTAMP WHERE id = ? AND delete_flag = 0";
            return client.sql(sql).bind(0, id).fetch().rowsUpdated().map(Long::valueOf);
        } else {
            String sql = "DELETE FROM sys_org WHERE id = ?";
            return client.sql(sql).bind(0, id).fetch().rowsUpdated().map(Long::valueOf);
        }
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

    public Mono<List<SysOrgTreeVO>> findAllOrgTreeVO() {
        String sql = "SELECT id, org_code , org_name , org_status , " +
                "parent_id , create_time , update_time  " +
                "FROM sys_org WHERE delete_flag = 0";
        return client.sql(sql)
                .map(row -> {
                    SysOrgTreeVO vo = new SysOrgTreeVO();
                    vo.setId(row.get("id", Long.class));
                    vo.setOrgCode(row.get("org_code", String.class));
                    vo.setOrgName(row.get("org_name", String.class));
                    vo.setOrgStatus(row.get("org_status", Integer.class));
                    vo.setParentId(row.get("parent_id", Long.class));
                    vo.setCreateTime(row.get("create_time", LocalDateTime.class));
                    vo.setUpdateTime(row.get("update_time", LocalDateTime.class));
                    return vo;
                })
                .all()
                .collectList();
    }

}
