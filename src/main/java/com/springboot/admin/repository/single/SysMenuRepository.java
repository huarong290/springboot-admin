package com.springboot.admin.repository.single;

import com.springboot.admin.model.entity.sys.SysMenu;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;


/**
 * 菜单表 Repository 接口
 * <p>
 * 用于封装菜单表的单表操作：
 * - 增删改查
 */
public interface SysMenuRepository extends ReactiveCrudRepository<SysMenu, Long> {

    /**
     * 根据父菜单ID查询子菜单
     *
     * @param parentId 父菜单ID
     * @return Flux<SysMenu> 响应式流，返回多个菜单对象
     */
    Flux<SysMenu> findByMenuParentId(Long parentId);
}

