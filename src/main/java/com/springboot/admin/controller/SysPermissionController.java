package com.springboot.admin.controller;

import com.springboot.admin.annotation.Logable;
import com.springboot.admin.common.ApiResult;
import com.springboot.admin.model.entity.sys.SysPermission;
import com.springboot.admin.service.ISysPermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 权限点管理 Controller
 *
 * 提供权限点相关的 REST API：
 * - 增删改查
 * - 校验权限编码是否存在
 * - 根据用户/角色查询权限点
 */
@RestController
@RequestMapping("/api/permission")
@Tag(name = "权限点管理", description = "权限点增删改查及关联查询接口")
public class SysPermissionController {

    @Autowired
    private ISysPermissionService permissionService;

    @GetMapping("/getPermissionById/{id}")
    @Operation(summary = "根据权限点ID查询权限信息")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<SysPermission>> getPermissionById(@PathVariable Long id) {
        return permissionService.getPermissionById(id)
                .map(ApiResult::successResult);
    }

    @PostMapping("/addPermission")
    @Operation(summary = "新增权限点")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<SysPermission>> addPermission(@RequestBody SysPermission permission) {
        return permissionService.addPermission(permission)
                .map(ApiResult::successResult);
    }

    @PutMapping("/updatePermission")
    @Operation(summary = "更新权限点信息")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<SysPermission>> updatePermission(@RequestBody SysPermission permission) {
        return permissionService.updatePermission(permission)
                .map(ApiResult::successResult);
    }

    @DeleteMapping("/deletePermission/{id}")
    @Operation(summary = "删除权限点")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<Void>> deletePermission(@PathVariable Long id) {
        return permissionService.deletePermission(id)
                .thenReturn(ApiResult.successResult("删除成功", null));
    }

    @GetMapping("/getPermissionList")
    @Operation(summary = "查询所有权限点")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<List<SysPermission>>> getPermissionList() {
        return permissionService.getPermissionList()
                .collectList()
                .map(ApiResult::successResult);
    }

    @GetMapping("/existsByPermissionCode/{permissionCode}")
    @Operation(summary = "判断权限编码是否存在")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<Boolean>> existsByPermissionCode(@PathVariable String permissionCode) {
        return permissionService.existsByPermissionCode(permissionCode)
                .map(ApiResult::successResult);
    }

    @GetMapping("/listPermissionsByUserId/{userId}")
    @Operation(summary = "根据用户ID查询权限点列表")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<List<SysPermission>>> listPermissionsByUserId(@PathVariable Long userId) {
        return permissionService.listPermissionsByUserId(userId)
                .collectList()
                .map(ApiResult::successResult);
    }

    @GetMapping("/listPermissionsByRoleId/{roleId}")
    @Operation(summary = "根据角色ID查询权限点列表")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<List<SysPermission>>> listPermissionsByRoleId(@PathVariable Long roleId) {
        return permissionService.listPermissionsByRoleId(roleId)
                .collectList()
                .map(ApiResult::successResult);
    }
}

