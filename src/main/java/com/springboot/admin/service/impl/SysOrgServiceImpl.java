package com.springboot.admin.service.impl;

import com.springboot.admin.model.entity.sys.SysOrg;
import com.springboot.admin.model.entity.sys.SysDept;
import com.springboot.admin.repository.single.SysOrgRepository;
import com.springboot.admin.repository.custom.SysOrgRepositoryCustom;
import com.springboot.admin.service.ISysOrgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 组织表 Service 实现类
 *
 * 封装组织相关业务逻辑：
 *   - 单表操作：增删改查、查询、判断存在
 *   - 多表操作：组织 → 部门
 *
 * 企业级 MySQL 风格：
 *   - 单表用 Repository
 *   - 多表用 DatabaseClient 封装在 RepositoryCustom
 */
@Service
@Slf4j
public class SysOrgServiceImpl implements ISysOrgService {

    private final SysOrgRepository orgRepository;
    private final SysOrgRepositoryCustom orgRepositoryCustom;

    public SysOrgServiceImpl(SysOrgRepository orgRepository,
                             SysOrgRepositoryCustom orgRepositoryCustom) {
        this.orgRepository = orgRepository;
        this.orgRepositoryCustom = orgRepositoryCustom;
    }

    /** ---------------- 单表操作 ---------------- */

    /**
     * 根据ID获取组织
     *
     * @param id 组织ID
     * @return 组织对象 (Mono<SysOrg>)
     */
    @Override
    public Mono<SysOrg> getOrgById(Long id) {
        return orgRepository.findById(id);
    }

    /**
     * 新增组织
     *
     * @param org 组织对象
     * @return 保存后的组织对象 (Mono<SysOrg>)
     */
    @Override
    public Mono<SysOrg> addOrg(SysOrg org) {
        return orgRepository.save(org);
    }

    /**
     * 更新组织
     *
     * @param org 组织对象
     * @return 更新后的组织对象 (Mono<SysOrg>)
     */
    @Override
    public Mono<SysOrg> updateOrg(SysOrg org) {
        return orgRepository.save(org);
    }

    /**
     * 删除组织
     *
     * @param id 组织ID
     * @return Mono<Void>
     */
    @Override
    public Mono<Void> deleteOrg(Long id) {
        return orgRepository.deleteById(id);
    }

    /**
     * 查询所有组织
     *
     * @return 组织列表 (Flux<SysOrg>)
     */
    @Override
    public Flux<SysOrg> getOrgList() {
        return orgRepository.findAll();
    }

    /**
     * 判断组织编码是否存在
     *
     * @param orgCode 组织编码
     * @return true/false (Mono<Boolean>)
     */
    @Override
    public Mono<Boolean> existsByOrgCode(String orgCode) {
        return orgRepository.existsByOrgCode(orgCode);
    }

    /** ---------------- 多表操作 ---------------- */

    /**
     * 根据组织ID查询部门列表
     *
     * @param orgId 组织ID
     * @return 部门集合 (Flux<SysDept>)
     */
    @Override
    public Flux<SysDept> listDeptsByOrgId(Long orgId) {
        return orgRepositoryCustom.findDeptsByOrgId(orgId);
    }
}
