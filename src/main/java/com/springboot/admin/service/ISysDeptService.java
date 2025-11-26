package com.springboot.admin.service;

import com.springboot.admin.model.entity.sys.SysDept;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 部门表 Service 接口
 * <p>
 * 提供部门相关的业务逻辑方法。
 */
public interface ISysDeptService {

    /**
     * 根据部门ID查询部门信息
     *
     * @param id 部门ID
     * @return Mono<SysDept> 响应式单对象
     */
    Mono<SysDept> getDeptById(Long id);

    /**
     * 新增部门
     *
     * @param dept 部门对象
     * @return Mono<SysDept> 响应式单对象，返回保存后的实体
     */
    Mono<SysDept> addDept(SysDept dept);

    /**
     * 更新部门信息
     *
     * @param dept 部门对象
     * @return Mono<SysDept> 响应式单对象，返回更新后的实体
     */
    Mono<SysDept> updateDept(SysDept dept);

    /**
     * 删除部门
     *
     * @param id 部门ID
     * @return Mono<Void> 响应式空对象，表示删除完成
     */
    Mono<Void> deleteDept(Long id);

    /**
     * 查询所有部门
     *
     * @return Flux<SysDept> 响应式流，返回多个部门对象
     */
    Flux<SysDept> getDeptList();

    /**
     * 判断部门编码是否存在
     *
     * @param deptCode 部门编码
     * @return Mono<Boolean> 响应式流，返回True或False
     */
    Mono<Boolean> existsByDeptCode(String deptCode);

    /**
     * 根据组织ID查询部门列表
     *
     * @param orgId 组织ID
     * @return Flux<SysDept> 响应式流，返回多个部门对象
     */
    Flux<SysDept> findDeptListByOrgId(Long orgId);
}

