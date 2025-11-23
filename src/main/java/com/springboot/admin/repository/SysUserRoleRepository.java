package com.springboot.admin.repository;

import com.springboot.admin.model.entity.sys.SysUserRole;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

/**
 * 用户角色关联表 Repository 接口
 */
public interface SysUserRoleRepository extends ReactiveCrudRepository<SysUserRole, Long> {

    /**
     * 根据用户ID查询角色关联关系
     *
     * @param userId 用户ID
     * @return Flux<SysUserRole> 响应式流，返回多个用户角色关联对象
     */
    Flux<SysUserRole> findByUserId(Long userId);
}

