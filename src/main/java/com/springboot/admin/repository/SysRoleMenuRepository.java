package com.springboot.admin.repository;

import com.springboot.admin.model.entity.sys.SysRoleMenu;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

/**
 * 角色菜单关联表 Repository 接口
 *
 * 用于查询角色和菜单的关联关系。
 */
public interface SysRoleMenuRepository extends ReactiveCrudRepository<SysRoleMenu, Long> {

    /**
     * 根据角色ID查询菜单关联关系
     *
     * @param roleId 角色ID
     * @return Flux<SysRoleMenu> 响应式流，返回多个角色菜单关联对象
     */
    Flux<SysRoleMenu> findByRoleId(Long roleId);
}
