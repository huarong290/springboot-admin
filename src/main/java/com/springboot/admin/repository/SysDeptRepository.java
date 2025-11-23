package com.springboot.admin.repository;

import com.springboot.admin.model.entity.sys.SysDept;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

/**
 * 部门表 Repository 接口
 */
public interface SysDeptRepository extends ReactiveCrudRepository<SysDept, Long> {

    /**
     * 根据组织ID查询部门列表
     *
     * @param orgId 组织ID
     * @return Flux<SysDept> 响应式流，返回多个部门对象
     */
    Flux<SysDept> findByOrgId(Long orgId);
}
