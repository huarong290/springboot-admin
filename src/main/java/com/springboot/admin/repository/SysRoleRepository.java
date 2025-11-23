package com.springboot.admin.repository;

import com.springboot.admin.model.entity.sys.SysRole;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * 角色表 Repository 接口
 */
public interface SysRoleRepository extends ReactiveCrudRepository<SysRole, Long> {
    // 可以在这里扩展角色相关的查询方法
}
