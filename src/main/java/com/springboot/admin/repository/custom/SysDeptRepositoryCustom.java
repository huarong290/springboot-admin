package com.springboot.admin.repository.custom;

import com.springboot.admin.model.dto.dept.SysDeptDTO;
import com.springboot.admin.model.dto.dept.SysDeptQueryDTO;
import com.springboot.admin.model.entity.sys.SysDept;
import com.springboot.admin.model.vo.PageResult;
import com.springboot.admin.model.vo.dept.SysDeptVO;
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
 * 自定义部门仓库类
 * <p>
 * 封装部门相关的多表关联查询逻辑：
 * - 部门 → 用户
 * <p>
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
     * 分页查询部门列表
     *
     * @param query 部门查询参数（包含分页和条件）
     * @return Mono<PageResult<SysDeptVO>> 分页结果
     */
    public Mono<PageResult<SysDeptVO>> pageDeptList(SysDeptQueryDTO query) {
        StringBuilder baseSql = new StringBuilder("FROM sys_dept t WHERE t.delete_flag = 0 ");

        Map<String, Object> params = new HashMap<>();
        if (query.getDeptName() != null && !query.getDeptName().isBlank()) {
            baseSql.append("AND t.dept_name LIKE :deptName ");
            params.put("deptName", "%" + query.getDeptName() + "%");
        }
        if (query.getDeptCode() != null && !query.getDeptCode().isBlank()) {
            baseSql.append("AND t.dept_code LIKE :deptCode ");
            params.put("deptCode", "%" + query.getDeptCode() + "%");
        }
        if (query.getStatus() != null) {
            baseSql.append("AND t.status = :status ");
            params.put("status", query.getStatus());
        }

        return R2dbcHelperUtil.queryPage(
                client,
                baseSql.toString(),
                params,
                query.getPage(),
                query.getSize(),
                (row, meta) -> {
                    SysDeptVO dept = new SysDeptVO();
                    dept.setId(row.get("id", Long.class));
                    dept.setDeptName(row.get("dept_name", String.class));
                    dept.setDeptCode(row.get("dept_code", String.class));
                    dept.setParentId(row.get("parent_id", Long.class));
                    dept.setOrderNum(row.get("order_num", Integer.class));
                    dept.setLeader(row.get("leader", String.class));
                    dept.setPhone(row.get("phone", String.class));
                    dept.setEmail(row.get("email", String.class));
                    dept.setStatus(row.get("status", Integer.class));
                    dept.setCreateTime(
                            Optional.ofNullable(row.get("create_time", java.time.ZonedDateTime.class))
                                    .map(java.time.ZonedDateTime::toLocalDateTime)
                                    .orElse(null)
                    );
                    dept.setUpdateTime(
                            Optional.ofNullable(row.get("update_time", java.time.ZonedDateTime.class))
                                    .map(java.time.ZonedDateTime::toLocalDateTime)
                                    .orElse(null)
                    );
                    return dept;
                }
        );
    }
    /**
     * 新增部门，返回生成的主键 ID
     */
    public Mono<Long> insertDept(SysDeptDTO dto) {
        Map<String, Object> fieldMap = new LinkedHashMap<>();

        if (StringUtils.isNotBlank(dto.getDeptName())) {
            fieldMap.put("dept_name", dto.getDeptName());
        }
        if (StringUtils.isNotBlank(dto.getDeptCode())) {
            fieldMap.put("dept_code", dto.getDeptCode());
        }
        if (dto.getParentId() != null) {
            fieldMap.put("parent_id", dto.getParentId());
        }
        if (dto.getOrderNum() != null) {
            fieldMap.put("order_num", dto.getOrderNum());
        }
        if (StringUtils.isNotBlank(dto.getLeader())) {
            fieldMap.put("leader", dto.getLeader());
        }
        if (StringUtils.isNotBlank(dto.getPhone())) {
            fieldMap.put("phone", dto.getPhone());
        }
        if (StringUtils.isNotBlank(dto.getEmail())) {
            fieldMap.put("email", dto.getEmail());
        }
        if (dto.getStatus() != null) {
            fieldMap.put("status", dto.getStatus());
        }
        fieldMap.put("create_time", LocalDateTime.now());
        fieldMap.put("update_time", LocalDateTime.now());

        return R2dbcHelperUtil.insertAndReturnId(client, "sys_dept", fieldMap);
    }

    /**
     * 更新部门，返回更新成功的记录数
     */
    public Mono<Long> updateDept(SysDeptDTO dto) {
        Map<String, Object> fieldMap = new LinkedHashMap<>();

        if (StringUtils.isNotBlank(dto.getDeptName())) {
            fieldMap.put("dept_name", dto.getDeptName());
        }
        if (StringUtils.isNotBlank(dto.getDeptCode())) {
            fieldMap.put("dept_code", dto.getDeptCode());
        }
        if (dto.getParentId() != null) {
            fieldMap.put("parent_id", dto.getParentId());
        }
        if (dto.getOrderNum() != null) {
            fieldMap.put("order_num", dto.getOrderNum());
        }
        if (StringUtils.isNotBlank(dto.getLeader())) {
            fieldMap.put("leader", dto.getLeader());
        }
        if (StringUtils.isNotBlank(dto.getPhone())) {
            fieldMap.put("phone", dto.getPhone());
        }
        if (StringUtils.isNotBlank(dto.getEmail())) {
            fieldMap.put("email", dto.getEmail());
        }
        if (dto.getStatus() != null) {
            fieldMap.put("status", dto.getStatus());
        }
        fieldMap.put("update_time", LocalDateTime.now());

        return R2dbcHelperUtil.update(client, "sys_dept", fieldMap, "id", dto.getId())
                .map(Long::valueOf);
    }

    /**
     * 删除部门
     */
    public Mono<Long> deleteDeptById(Long id, boolean logicalDelete) {
        if (logicalDelete) {
            String sql = "UPDATE sys_dept SET delete_flag = 1, update_by = 'system', update_time = CURRENT_TIMESTAMP WHERE id = ? AND delete_flag = 0";
            return client.sql(sql).bind(0, id).fetch().rowsUpdated().map(Long::valueOf);
        } else {
            String sql = "DELETE FROM sys_dept WHERE id = ?";
            return client.sql(sql).bind(0, id).fetch().rowsUpdated().map(Long::valueOf);
        }
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
