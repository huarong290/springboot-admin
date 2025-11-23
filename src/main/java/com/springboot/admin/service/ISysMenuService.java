package com.springboot.admin.service;


import com.springboot.admin.model.entity.sys.SysMenu;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 菜单表 Service 接口
 *
 * 提供菜单相关的业务逻辑方法。
 */
public interface ISysMenuService {

    /**
     * 根据菜单ID查询菜单信息
     *
     * @param id 菜单ID
     * @return Mono<SysMenu> 响应式单对象
     */
    Mono<SysMenu> getMenuById(Long id);

    /**
     * 根据父菜单ID查询子菜单
     *
     * @param parentId 父菜单ID
     * @return Flux<SysMenu> 响应式流，返回多个菜单对象
     */
    Flux<SysMenu> getMenusByParentId(Long parentId);

    /**
     * 新增菜单
     *
     * @param menu 菜单对象
     * @return Mono<SysMenu> 响应式单对象，返回保存后的实体
     */
    Mono<SysMenu> addMenu(SysMenu menu);

    /**
     * 更新菜单信息
     *
     * @param menu 菜单对象
     * @return Mono<SysMenu> 响应式单对象，返回更新后的实体
     */
    Mono<SysMenu> updateMenu(SysMenu menu);

    /**
     * 删除菜单
     *
     * @param id 菜单ID
     * @return Mono<Void> 响应式空对象，表示删除完成
     */
    Mono<Void> deleteMenu(Long id);

    /**
     * 查询所有菜单
     *
     * @return Flux<SysMenu> 响应式流，返回多个菜单对象
     */
    Flux<SysMenu> listMenus();
}
