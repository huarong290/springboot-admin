package com.springboot.admin.service;

import com.springboot.admin.model.dto.permission.SysPermissionDTO;
import com.springboot.admin.model.dto.permission.SysPermissionQueryDTO;
import com.springboot.admin.model.vo.PageResult;
import com.springboot.admin.model.vo.permission.SysPermissionVO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 权限点表 Service 接口
 * <p>
 * 提供权限点相关的业务逻辑方法。
 */
public interface ISysPermissionService {
    /**
     * Service 层接口：分页查询权限列表
     *
     * @param query 权限查询参数（包含分页和条件）
     * @return Mono<PageResult<SysPermissionVO>> 分页结果
     */
    Mono<PageResult<SysPermissionVO>> pagePermissionList(SysPermissionQueryDTO query);

    /**
     * 新增权限点
     *
     * @param sysPermissionDTO 权限点对象
     * @return Mono<SysPermission> 响应式单对象，返回保存后的实体
     */
    Mono<Long> addPermission(SysPermissionDTO sysPermissionDTO);

    /**
     * 更新权限点信息
     *
     * @param sysPermissionDTO 权限点对象
     * @return Mono<SysPermission> 响应式单对象，返回更新后的实体
     */
    Mono<Long> updatePermission(SysPermissionDTO sysPermissionDTO);

    /**
     * 删除权限点
     *
     * @param id 权限点ID
     * @return Mono<Void> 响应式空对象，表示删除完成
     */
    Mono<Long> deletePermission(Long id);

    /**
     * 根据权限点ID查询权限信息
     *
     * @param id 权限点ID
     * @return Mono<SysPermission> 响应式单对象
     */
    Mono<SysPermissionVO> getPermissionById(Long id);


    /**
     * 查询所有权限点
     *
     * @return Flux<SysPermission> 响应式流，返回多个权限点对象
     */
    Flux<SysPermissionVO> getPermissionList();

    /**
     * 判断权限编码是否存在
     *
     * @param permissionCode 权限编码
     * @return Mono<Boolean> 响应式单对象，返回True或False
     */
    Mono<Boolean> existsByPermissionCode(String permissionCode);

    /**
     * 查询所有权限点
     *
     * @param userId 用户id
     * @return Flux<SysPermission> 响应式流，返回多个权限点对象
     */
    Flux<SysPermissionVO> listPermissionsByUserId(Long userId);

    /**
     * 查询所有权限点
     *
     * @param roleId 用户id
     * @return Flux<SysPermission> 响应式流，返回多个权限点对象
     */
    Flux<SysPermissionVO> listPermissionsByRoleId(Long roleId);


}

