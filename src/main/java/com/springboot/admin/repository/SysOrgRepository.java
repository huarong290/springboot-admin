package com.springboot.admin.repository;



import com.springboot.admin.model.entity.sys.SysOrg;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * 组织表 Repository 接口
 *
 * 提供组织相关的 CRUD 操作。
 */
public interface SysOrgRepository extends ReactiveCrudRepository<SysOrg, Long> {
    // 可以扩展自定义查询方法，例如 findByOrgCode(String orgCode)
}

