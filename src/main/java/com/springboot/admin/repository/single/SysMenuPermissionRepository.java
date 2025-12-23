package com.springboot.admin.repository.single;

import com.springboot.admin.model.entity.sys.SysMenuPermission;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
/**
 * 菜单权限点关联表 Repository 接口
 *
 * 用于查询菜单和权限点的关联关系。
 */
public interface SysMenuPermissionRepository extends ReactiveCrudRepository<SysMenuPermission, Long> {
}
