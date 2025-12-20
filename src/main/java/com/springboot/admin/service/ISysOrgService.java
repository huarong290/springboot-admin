package com.springboot.admin.service;

import com.springboot.admin.model.dto.org.SysOrgDTO;
import com.springboot.admin.model.dto.org.SysOrgQueryDTO;
import com.springboot.admin.model.entity.sys.SysDept;
import com.springboot.admin.model.entity.sys.SysOrg;
import com.springboot.admin.model.vo.PageResult;
import com.springboot.admin.model.vo.org.SysOrgVO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 组织表 Service 接口
 * <p>
 * 提供组织相关的业务逻辑方法。
 */
public interface ISysOrgService {
    /**
     * 根据条件分页查询部门信息
     *
     * @param query 分页查询参数
     * @return Mono<SysOrgVO> 响应式单对象，可能为空
     */
    Mono<PageResult<SysOrgVO>> pageOrgList(SysOrgQueryDTO query);
    /**
     * 新增组织
     *
     * @param sysOrgDTO 组织对象
     * @return Mono<Long> 响应式单对象，返回保存后的实体
     */
    Mono<Long> addOrg(SysOrgDTO sysOrgDTO);

    /**
     * 更新组织信息
     *
     * @param sysOrgDTO 组织对象
     * @return Mono<Long> 响应式单对象，返回更新后的实体
     */
    Mono<Long> updateOrg(SysOrgDTO sysOrgDTO);

    /**
     * 删除组织
     *
     * @param id 组织ID
     * @return Mono<Long> 响应式空对象，表示删除完成
     */
    Mono<Long> deleteOrg(Long id);
    /**
     * 根据组织ID查询组织信息
     *
     * @param id 组织ID
     * @return Mono<SysOrg> 响应式单对象
     */
    Mono<SysOrg> getOrgById(Long id);

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
