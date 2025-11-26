package com.springboot.admin.repository.single;

import com.springboot.admin.model.entity.sys.SysRole;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 * 角色表 Repository 接口
 * 提供基础的响应式数据库访问方法
 */
public interface SysRoleRepository extends ReactiveCrudRepository<SysRole, Long> {
    // 可以在这里扩展角色相关的查询方法

    /**
     * 根据角色编码查询角色
     */
    Mono<SysRole> findByRoleCode(String roleCode);

    /**
     * 判断角色是否存在
     */
    Mono<Boolean> existsByRoleCode(String roleCode);

}
