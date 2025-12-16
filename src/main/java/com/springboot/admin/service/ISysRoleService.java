package com.springboot.admin.service;

import com.springboot.admin.model.dto.role.SysRoleDTO;
import com.springboot.admin.model.dto.role.SysRoleQueryDTO;
import com.springboot.admin.model.vo.PageResult;
import com.springboot.admin.model.vo.role.SysRoleVO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 角色表 Service 接口
 *
 * 提供角色相关的业务逻辑方法。
 * 使用 SysRoleDTO 作为入参，SysRoleVO 作为出参。
 */
public interface ISysRoleService {
    /**
     * 根据角色编码查询角色信息
     *
     * @param query 角色查询DTO
     * @return Mono<SysRoleVO> 响应式单对象
     */
    Mono<PageResult<SysRoleVO>> pageRoleList(SysRoleQueryDTO query);

    /**
     * 根据角色ID查询角色信息
     *
     * @param id 角色ID
     * @return Mono<SysRoleVO> 响应式单对象
     */
    Mono<SysRoleVO> getRoleById(Long id);

    /**
     * 根据角色编码查询角色信息
     *
     * @param roleCode 角色编码
     * @return Mono<SysRoleVO> 响应式单对象
     */
    Mono<SysRoleVO> getRoleByCode(String roleCode);

    /**
     * 新增角色
     *
     * @param roleDTO 角色对象
     * @return Mono<Long> 响应式单对象，返回保存后的实体主键id
     */
    Mono<Long> addRole(SysRoleDTO roleDTO);

    /**
     * 更新角色信息
     *
     * @param roleDTO 角色对象
     * @return Mono<SysRoleVO> 响应式单对象，返回更新后的实体
     */
    Mono<Long> updateRole(SysRoleDTO roleDTO);

    /**
     * 删除角色
     *
     * @param id 角色ID
     * @return Mono<Long> 响应式单对象，返回删除成功的记录数
     */
    Mono<Long> deleteRole(Long id);

    /**
     * 批量删除角色
     *
     * @param ids 角色ID集合
     * @return Mono<Integer> 响应式单对象，返回删除成功的记录数
     */
    Mono<Long> deleteRolesByIds(List<Long> ids);


    /**
     * 批量物理删除角色
     *
     * @param ids 角色ID集合
     * @return Mono<Long> 删除成功的记录数
     */
    Mono<Long> deleteRolesPhysicallyByIds(List<Long> ids);

    /**
     * 查询所有角色
     *
     * @return Flux<SysRoleVO> 响应式流，返回多个角色对象
     */
    Flux<SysRoleVO> getRoleList();

    /**
     * 根据用户ID查询用户的角色列表
     *
     * @param userId 用户ID
     * @return Flux<SysRoleVO> 响应式流，返回该用户的角色对象
     */
    Flux<SysRoleVO> listRolesByUserId(Long userId);

    /**
     * 判断角色是否存在
     *
     * @param roleCode 角色编码
     * @return Mono<Boolean> 响应式布尔值
     */
    Mono<Boolean> existsByRoleCode(String roleCode);

    /**
     * 根据权限ID查询角色列表
     *
     * @param permissionId 权限ID
     * @return Flux<SysRoleVO> 响应式流，返回拥有该权限的角色集合
     */
    Flux<SysRoleVO> findRolesByPermissionId(Long permissionId);

    /**
     * 根据菜单ID查询角色列表
     *
     * @param menuId 菜单ID
     * @return Flux<SysRoleVO> 响应式流，返回该菜单拥有的角色集合
     */
    Flux<SysRoleVO> listRolesByMenuId(Long menuId);


}
