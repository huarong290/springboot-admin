package com.springboot.admin.repository.single;

import com.springboot.admin.model.entity.sys.SysOrg;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

/**
 * 组织表 Repository 接口 提供组织相关的 CRUD 操作。
 *
 * 用于封装组织表的单表操作：
 *   - 增删改查
 *   - 判断组织编码是否存在
 *
 * 继承 ReactiveCrudRepository，自动生成常见的 CRUD 方法。
 */
public interface SysOrgRepository extends ReactiveCrudRepository<SysOrg, Long> {

    // 可以扩展自定义查询方法，例如 findByOrgCode(String orgCode)

    /**
     * 判断组织编码是否存在
     *
     * @param orgCode 组织编码
     * @return true/false (Mono<Boolean>)
     */
    Mono<Boolean> existsByOrgCode(String orgCode);
}

