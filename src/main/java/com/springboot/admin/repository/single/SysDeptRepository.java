package com.springboot.admin.repository.single;

import com.springboot.admin.model.entity.sys.SysDept;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 * 部门表 Repository 接口
 *
 * 用于封装部门表的单表操作：
 *   - 增删改查
 *   - 判断部门编码是否存在
 *
 * 继承 ReactiveCrudRepository，自动生成常见的 CRUD 方法。
 */
public interface SysDeptRepository extends ReactiveCrudRepository<SysDept, Long> {

    /**
     * 根据组织ID查询部门列表
     *
     * @param orgId 组织ID
     * @return Flux<SysDept> 响应式流，返回多个部门对象
     */
    Flux<SysDept> findByOrgId(Long orgId);

    /**
     * 判断部门编码是否存在
     *
     * @param deptCode 部门编码
     * @return Mono<Boolean> 响应式流，返回True或False
     */
    Mono<Boolean> existsByDeptCode(String deptCode);
}

