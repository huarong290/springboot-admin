package com.springboot.admin.service;

import com.springboot.admin.model.dto.BindResultDTO;
import com.springboot.admin.model.dto.rolemenu.SysRoleMenuDTO;
import com.springboot.admin.model.vo.rolemenu.SysRoleMenuVO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 角色菜单关联表 Service 接口
 *
 * 提供角色与菜单关联的业务逻辑方法，包括：
 * - 根据角色查询菜单集合（用于前端路由控制）
 * - 新增单条角色菜单绑定
 * - 删除角色菜单绑定
 * - 查询所有角色菜单绑定
 * - 批量绑定角色菜单（增量绑定）
 *
 * 使用 Reactor 响应式类型（Flux/Mono）作为返回值，支持异步非阻塞调用。
 */
public interface ISysRoleMenuService {

    /**
     * 根据角色ID查询菜单关联关系
     *
     * 用途：在用户登录后，根据其角色查询可访问的菜单集合，
     *       决定前端路由的可见性。
     *
     * @param roleId 角色ID
     * @return Flux<SysRoleMenuVO> 响应式流，返回该角色绑定的菜单集合
     */
    Flux<SysRoleMenuVO> getMenusByRoleId(Long roleId);

    /**
     * 新增单条角色菜单关联
     *
     * 用途：为某个角色新增一个菜单绑定关系。
     *
     * @param sysRoleMenuDTO 角色菜单对象（包含 roleId 和 menuId）
     * @return Mono<Long> 响应式单对象，返回新增记录的主键ID
     */
    Mono<Long> addRoleMenu(SysRoleMenuDTO sysRoleMenuDTO);

    /**
     * 删除角色菜单关联
     *
     * 用途：解除某个角色与某个菜单的绑定关系。
     *
     * @param id 主键ID（角色菜单关联表的记录ID）
     * @return Mono<Long> 响应式单对象，返回删除的记录ID或受影响的行数
     */
    Mono<Long> deleteRoleMenu(Long id);

    /**
     * 查询所有角色菜单关联
     *
     * 用途：获取系统中所有角色与菜单的绑定关系，
     *       常用于后台管理或审计。
     *
     * @return Flux<SysRoleMenuVO> 响应式流，返回所有角色菜单关联对象
     */
    Flux<SysRoleMenuVO> getRoleMenuList();

    /**
     * 批量绑定角色菜单（增量绑定）
     *
     * 用途：一次性为某个角色绑定多个菜单，支持增量更新：
     *       - 新增未绑定的菜单
     *       - 删除已解绑的菜单
     *
     * @param roleId 角色ID
     * @param menuIds 菜单ID集合
     * @return Mono<BindResultDTO> 返回绑定结果（可封装为 BindResultDTO，包含新增/删除数量）
     */
    Mono<BindResultDTO> bindRoleMenus(Long roleId, List<Long> menuIds);
}
