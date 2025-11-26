package com.springboot.admin.service;

import com.springboot.admin.model.entity.sys.SysDept;
import com.springboot.admin.model.entity.sys.SysOrg;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 组织表 Service 接口
 * <p>
 * 提供组织相关的业务逻辑方法。
 */
public interface ISysOrgService {

    /**
     * 根据组织ID查询组织信息
     *
     * @param id 组织ID
     * @return Mono<SysOrg> 响应式单对象
     */
    Mono<SysOrg> getOrgById(Long id);

    /**
     * 新增组织
     *
     * @param org 组织对象
     * @return Mono<SysOrg> 响应式单对象，返回保存后的实体
     */
    Mono<SysOrg> addOrg(SysOrg org);

    /**
     * 更新组织信息
     *
     * @param org 组织对象
     * @return Mono<SysOrg> 响应式单对象，返回更新后的实体
     */
    Mono<SysOrg> updateOrg(SysOrg org);

    /**
     * 删除组织
     *
     * @param id 组织ID
     * @return Mono<Void> 响应式空对象，表示删除完成
     */
    Mono<Void> deleteOrg(Long id);

    /**
     * 查询所有组织
     *
     * @return Flux<SysOrg> 响应式流，返回多个组织对象
     */
    Flux<SysOrg> getOrgList();


    /**
     * 判断组织编码是否存在
     * @return Mono<Boolean> 响应式流，返回True或False
     */
    Mono<Boolean> existsByOrgCode(String orgCode);

    /**
     * 根据组织ID查询部门列表
     */
    Flux<SysDept> listDeptsByOrgId(Long orgId);
}
