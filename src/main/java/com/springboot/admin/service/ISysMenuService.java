package com.springboot.admin.service;

import com.springboot.admin.model.dto.menu.SysMenuDTO;
import com.springboot.admin.model.vo.menu.SysMenuTreeVO;
import com.springboot.admin.model.vo.menu.SysMenuVO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 菜单表 Service 接口
 *
 * 提供菜单相关的业务逻辑方法。
 * 使用 SysMenuDTO 作为入参，SysMenuVO / SysMenuTreeVO 作为出参。
 */
public interface ISysMenuService {

    /**
     * 根据菜单ID查询菜单信息
     *
     * @param id 菜单ID
     * @return Mono<SysMenuVO> 响应式单对象
     */
    Mono<SysMenuVO> getMenuById(Long id);

    /**
     * 根据父菜单ID查询子菜单
     *
     * @param parentId 父菜单ID
     * @return Flux<SysMenuVO> 响应式流，返回多个菜单对象
     */
    Flux<SysMenuVO> getMenusByParentId(Long parentId);

    /**
     * 新增菜单
     *
     * @param menuDTO 菜单对象
     * @return Mono<Long> 响应式单对象，返回保存后的实体主键id
     */
    Mono<Long> addMenu(SysMenuDTO menuDTO);

    /**
     * 更新菜单信息
     *
     * @param menuDTO 菜单对象
     * @return Mono<SysMenuVO> 响应式单对象，返回更新后的实体
     */
    Mono<SysMenuVO> updateMenu(SysMenuDTO menuDTO);

    /**
     * 删除菜单
     *
     * @param id 菜单ID
     * @return Mono<Long> 响应式单对象，返回删除成功的记录数
     */
    Mono<Long> deleteMenu(Long id);

    /**
     * 查询所有菜单（平铺列表）
     * <p>
     * 返回 SysMenuVO 列表，字段与数据库结构对应。
     * 适合后台管理场景，例如菜单维护页面。
     *
     * @return Flux<SysMenuVO> 响应式流，返回多个菜单对象
     */
    Flux<SysMenuVO> getMenuList();

    /**
     * 查询所有菜单（树形结构）
     * <p>
     * 返回 SysMenuTreeVO 列表，包含 children 和 meta 字段。
     * 适合前端路由场景，例如动态生成 Vue/React 菜单树。
     *
     * @return Flux<SysMenuTreeVO> 响应式流，返回树形菜单集合
     */
    Flux<SysMenuTreeVO> getMenuTree();

    /**
     * 根据用户ID查询菜单列表
     *
     * @param userId 用户ID
     * @return Flux<SysMenuVO> 响应式流，返回该用户拥有的菜单集合
     */
    Flux<SysMenuVO> getMenuListByUserId(Long userId);

    /**
     * 根据角色ID查询菜单列表
     *
     * @param roleId 角色ID
     * @return Flux<SysMenuVO> 响应式流，返回该角色拥有的菜单集合
     */
    Flux<SysMenuVO> listMenusByRoleId(Long roleId);

    /**
     * 根据用户ID查询菜单树
     *
     * 用途：前端路由场景，返回用户拥有的树形菜单
     *
     * @param userId 用户ID
     * @return Flux<SysMenuTreeVO> 响应式流，返回该用户拥有的菜单树
     */
    Flux<SysMenuTreeVO> getMenuTreeByUserId(Long userId);

}
