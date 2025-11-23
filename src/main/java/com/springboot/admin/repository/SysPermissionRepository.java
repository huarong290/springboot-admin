package com.springboot.admin.repository;

import com.springboot.admin.model.entity.sys.SysPermission;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * 权限点表 Repository 接口
 *
 * 提供权限点相关的 CRUD 操作。
 */
public interface SysPermissionRepository extends ReactiveCrudRepository<SysPermission, Long> {
    // 可以扩展自定义查询方法，例如 findByPermissionCode(String code)
}
