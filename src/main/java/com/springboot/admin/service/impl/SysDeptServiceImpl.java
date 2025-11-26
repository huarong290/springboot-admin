package com.springboot.admin.service.impl;

import com.springboot.admin.model.entity.sys.SysDept;
import com.springboot.admin.model.entity.sys.SysUser;
import com.springboot.admin.repository.single.SysDeptRepository;
import com.springboot.admin.repository.custom.SysDeptRepositoryCustom;
import com.springboot.admin.service.ISysDeptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 部门表 Service 实现类
 * <p>
 * 封装部门相关业务逻辑：
 * - 单表操作：增删改查、查询、判断存在
 * - 多表操作：部门 → 用户
 * <p>
 * 企业级 MySQL 风格：
 * - 单表用 Repository
 * - 多表用 DatabaseClient 封装在 RepositoryCustom
 */
@Service
@Slf4j
public class SysDeptServiceImpl implements ISysDeptService {

    private final SysDeptRepository deptRepository;
    private final SysDeptRepositoryCustom deptRepositoryCustom;

    public SysDeptServiceImpl(SysDeptRepository deptRepository,
                              SysDeptRepositoryCustom deptRepositoryCustom) {
        this.deptRepository = deptRepository;
        this.deptRepositoryCustom = deptRepositoryCustom;
    }

    /** ---------------- 单表操作 ---------------- */

    /**
     * 根据ID获取部门
     *
     * @param id 部门ID
     * @return 部门对象 (Mono<SysDept>)
     */
    @Override
    public Mono<SysDept> getDeptById(Long id) {
        return deptRepository.findById(id);
    }

    /**
     * 新增部门
     *
     * @param dept 部门对象
     * @return 保存后的部门对象 (Mono<SysDept>)
     */
    @Override
    public Mono<SysDept> addDept(SysDept dept) {
        return deptRepository.save(dept);
    }

    /**
     * 更新部门
     *
     * @param dept 部门对象
     * @return 更新后的部门对象 (Mono<SysDept>)
     */
    @Override
    public Mono<SysDept> updateDept(SysDept dept) {

        return deptRepository.save(dept);
    }

    /**
     * 删除部门
     *
     * @param id 部门ID
     * @return Mono<Void>
     */
    @Override
    public Mono<Void> deleteDept(Long id) {

        return deptRepository.deleteById(id);
    }

    /**
     * 查询所有部门
     *
     * @return 部门列表 (Flux<SysDept>)
     */
    @Override
    public Flux<SysDept> getDeptList() {

        return deptRepository.findAll();
    }

    /**
     * 判断部门编码是否存在
     *
     * @param deptCode 部门编码
     * @return true/false (Mono<Boolean>)
     */
    @Override
    public Mono<Boolean> existsByDeptCode(String deptCode) {

        return deptRepository.existsByDeptCode(deptCode);
    }


    /** ---------------- 多表操作 ---------------- */

    /**
     * 根据组织ID查询部门列表
     *
     * @param orgId 组织ID
     * @return Flux<SysDept> 响应式流，返回该组织下的部门集合
     */
    @Override
    public Flux<SysDept> findDeptListByOrgId(Long orgId) {

        return deptRepositoryCustom.findDeptListByOrgId(orgId);
    }
}
