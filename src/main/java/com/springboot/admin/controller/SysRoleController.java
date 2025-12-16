package com.springboot.admin.controller;

import com.springboot.admin.annotation.Logable;
import com.springboot.admin.common.ApiResult;
import com.springboot.admin.model.dto.role.SysRoleDTO;
import com.springboot.admin.model.dto.role.SysRoleQueryDTO;
import com.springboot.admin.model.vo.PageResult;
import com.springboot.admin.model.vo.role.SysRoleVO;
import com.springboot.admin.service.ISysRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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
@Tag(name = "角色管理", description = "角色增删改查及关联查询接口")
public class SysRoleController {
    @Autowired
    private  ISysRoleService roleService;

    /**
     * 分页查询角色列表
     *
     * @param query 用户查询参数（继承 PageQuery，包含分页和条件）
     * @return 返回分页结果：用户列表 + 总数 + 页码信息
     */
    @GetMapping("/pageRoleList")
    @Operation(summary = "分页查询角色列表")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<PageResult<SysRoleVO>>> pageRoleList(SysRoleQueryDTO query) {
        return roleService.pageRoleList(query).map(ApiResult::successResult);
    }
    /**
     * 新增角色
     */
    @PostMapping("/addRole")
    @Operation(summary = "新增角色")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<Long>> addRole(@RequestBody SysRoleDTO roleDTO) {
        return roleService.addRole(roleDTO)
                .map(ApiResult::successResult);
    }
    /**
     * 更新角色信息
     */
    @PutMapping("/updateRole")
    @Operation(summary = "更新角色信息")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<Long>> updateRole(@RequestBody SysRoleDTO roleDTO) {
        return roleService.updateRole(roleDTO)
                .map(ApiResult::successResult);
    }

    /**
     * 删除角色
     */
    @DeleteMapping("deleteRole/{id}")
    @Operation(summary = "删除角色")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<Long>> deleteRole(@PathVariable Long id) {
        return roleService.deleteRole(id)
                .map(ApiResult::successResult);
    }

    /**
     * 根据角色ID查询角色信息
     */
    @GetMapping("getRoleById/{id}")
    @Operation(summary = "根据角色ID查询角色信息")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<SysRoleVO>> getRoleById(@PathVariable Long id) {
        return roleService.getRoleById(id)
                .map(ApiResult::successResult);
    }

    /**
     * 根据角色编码查询角色信息
     */
    @GetMapping("/getRoleByCode/{roleCode}")
    @Operation(summary = "根据角色编码查询角色信息")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<SysRoleVO>> getRoleByCode(@PathVariable String roleCode) {
        return roleService.getRoleByCode(roleCode)
                .map(ApiResult::successResult);
    }




    /**
     * 批量删除角色（逻辑删除）
     */
    @DeleteMapping("/deleteRoles")
    @Operation(summary = "批量逻辑删除角色")
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
    @Operation(summary = "批量物理删除角色")
    public Mono<ApiResult<Long>> deleteRolesPhysically(@RequestBody List<Long> ids) {
        return roleService.deleteRolesPhysicallyByIds(ids)
                .map(ApiResult::successResult);
    }

    /**
     * 查询所有角色
     */
    @GetMapping("/getRoleList")
    @Operation(summary = "查询所有角色")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<List<SysRoleVO>>> getRoleList() {
        return roleService.getRoleList()
                .collectList() // 将 Flux<SysRoleVO> 收集成 List<SysRoleVO>
                .map(ApiResult::successResult); // 用统一的 ApiResult 包裹整个列表
    }


    /**
     * 根据用户ID查询角色列表
     */
    @GetMapping("/listRolesByUserId/{userId}")
    @Operation(summary = "根据用户ID查询角色列表")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<List<SysRoleVO>>> listRolesByUserId(@PathVariable Long userId) {
        return roleService.listRolesByUserId(userId).collectList().map(ApiResult::successResult);
    }

    /**
     * 判断角色是否存在
     */
    @GetMapping("/existsByRoleCode/{roleCode}")
    @Operation(summary = "判断角色是否存在")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<Boolean>> existsByRoleCode(@PathVariable String roleCode) {
        return roleService.existsByRoleCode(roleCode)
                .map(ApiResult::successResult);
    }

    /**
     * 根据权限ID查询角色列表
     */
    @GetMapping("/findRolesByPermissionId/{permissionId}")
    @Operation(summary = "根据权限ID查询角色列表")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<List<SysRoleVO>>> findRolesByPermissionId(@PathVariable Long permissionId) {
        return roleService.findRolesByPermissionId(permissionId).collectList().map(ApiResult::successResult);
    }

    /**
     * 根据菜单ID查询角色列表
     */
    @GetMapping("/listRolesByMenuId/{menuId}")
    @Operation(summary = "根据菜单ID查询角色列表")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<List<SysRoleVO>>> listRolesByMenuId(@PathVariable Long menuId) {
        return roleService.listRolesByMenuId(menuId).collectList().map(ApiResult::successResult);
    }
}

