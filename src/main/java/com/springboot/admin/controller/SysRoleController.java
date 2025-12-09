package com.springboot.admin.controller;

import com.springboot.admin.annotation.Logable;
import com.springboot.admin.common.ApiResult;
import com.springboot.admin.model.dto.role.SysRoleDTO;
import com.springboot.admin.model.vo.role.SysRoleVO;
import com.springboot.admin.service.ISysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 角色管理 Controller
 *
 * 提供角色相关的 REST API：
 * - 增删改查
 * - 根据用户/权限/菜单查询角色
 */
@RestController
@RequestMapping("/api/role")

public class SysRoleController {
    @Autowired
    private  ISysRoleService roleService;

    /**
     * 根据角色ID查询角色信息
     */
    @GetMapping("getRoleById/{id}")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<SysRoleVO>> getRoleById(@PathVariable Long id) {
        return roleService.getRoleById(id)
                .map(ApiResult::successResult);
    }

    /**
     * 根据角色编码查询角色信息
     */
    @GetMapping("/getRoleByCode/{roleCode}")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<SysRoleVO>> getRoleByCode(@PathVariable String roleCode) {
        return roleService.getRoleByCode(roleCode)
                .map(ApiResult::successResult);
    }

    /**
     * 新增角色
     */
    @PostMapping("/addRole")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<Long>> addRole(@RequestBody SysRoleDTO roleDTO) {
        return roleService.addRole(roleDTO)
                .map(ApiResult::successResult);
    }

    /**
     * 更新角色信息
     */
    @PutMapping("/updateRole")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<Long>> updateRole(@RequestBody SysRoleDTO roleDTO) {
        return roleService.updateRole(roleDTO)
                .map(ApiResult::successResult);
    }

    /**
     * 删除角色
     */
    @DeleteMapping("deleteRole/{id}")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<Long>> deleteRole(@PathVariable Long id) {
        return roleService.deleteRole(id)
                .map(ApiResult::successResult);
    }

    /**
     * 批量删除角色（逻辑删除）
     */
    @DeleteMapping("/deleteRoles")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<Long>> deleteRoles(@RequestBody List<Long> ids) {
        return roleService.deleteRolesByIds(ids)
                .map(ApiResult::successResult);
    }

    /**
     * 批量物理删除角色
     */
    @DeleteMapping("/deleteRolesPhysically")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<Long>> deleteRolesPhysically(@RequestBody List<Long> ids) {
        return roleService.deleteRolesPhysicallyByIds(ids)
                .map(ApiResult::successResult);
    }

    /**
     * 查询所有角色
     */
    @GetMapping("/getRoleList")
    @Logable(logRequest = true, logResponse = true)
    public Flux<SysRoleVO> getRoleList() {
        return roleService.getRoleList();
    }

    /**
     * 根据用户ID查询角色列表
     */
    @GetMapping("/listRolesByUserId/{userId}")
    @Logable(logRequest = true, logResponse = true)
    public Flux<SysRoleVO> listRolesByUserId(@PathVariable Long userId) {
        return roleService.listRolesByUserId(userId);
    }

    /**
     * 判断角色是否存在
     */
    @GetMapping("/existsByRoleCode/{roleCode}")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<Boolean>> existsByRoleCode(@PathVariable String roleCode) {
        return roleService.existsByRoleCode(roleCode)
                .map(ApiResult::successResult);
    }

    /**
     * 根据权限ID查询角色列表
     */
    @GetMapping("/findRolesByPermissionId/{permissionId}")
    @Logable(logRequest = true, logResponse = true)
    public Flux<SysRoleVO> findRolesByPermissionId(@PathVariable Long permissionId) {
        return roleService.findRolesByPermissionId(permissionId);
    }

    /**
     * 根据菜单ID查询角色列表
     */
    @GetMapping("/listRolesByMenuId/{menuId}")
    @Logable(logRequest = true, logResponse = true)
    public Flux<SysRoleVO> listRolesByMenuId(@PathVariable Long menuId) {
        return roleService.listRolesByMenuId(menuId);
    }
}

