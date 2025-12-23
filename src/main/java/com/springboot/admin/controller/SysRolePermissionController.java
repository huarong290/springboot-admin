package com.springboot.admin.controller;

import com.springboot.admin.annotation.Logable;
import com.springboot.admin.common.ApiResult;
import com.springboot.admin.model.dto.BindResultDTO;
import com.springboot.admin.model.entity.sys.SysRolePermission;
import com.springboot.admin.service.ISysRolePermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 角色权限点关联 Controller
 * <p>
 * 提供角色与权限点关联的 REST API：
 * - 查询角色绑定的权限点
 * - 新增角色权限点绑定
 * - 删除角色权限点绑定
 */
@RestController
@RequestMapping("/api/rolePermission")
@Tag(name = "角色权限点关联", description = "角色与权限点绑定接口")
public class SysRolePermissionController {

    @Autowired
    private ISysRolePermissionService rolePermissionService;


    @PostMapping("/bindRolePermissions/{roleId}")
    @Operation(summary = "增量绑定角色权限点")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<BindResultDTO>> bindRolePermissions(@PathVariable Long roleId,
                                                              @RequestBody List<Long> permissionIds) {
        return rolePermissionService.bindRolePermissions(roleId, permissionIds)
                .map(result -> ApiResult.successResult(
                        "增量更新成功，新增 " + result.getAddedCount() + " 条，删除 " + result.getRemovedCount() + " 条",
                        result
                ));
    }


    @GetMapping("/getPermissionsByRoleId/{roleId}")
    @Operation(summary = "根据角色ID查询权限点关联关系")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<List<SysRolePermission>>> getPermissionsByRoleId(@PathVariable Long roleId) {
        return rolePermissionService.getPermissionsByRoleId(roleId)
                .collectList()
                .map(ApiResult::successResult);
    }

    @PostMapping("/addRolePermission")
    @Operation(summary = "新增角色权限点关联")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<SysRolePermission>> addRolePermission(@RequestBody SysRolePermission rolePermission) {
        return rolePermissionService.addRolePermission(rolePermission)
                .map(ApiResult::successResult);
    }

    @DeleteMapping("/deleteRolePermission/{id}")
    @Operation(summary = "删除角色权限点关联")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<Void>> deleteRolePermission(@PathVariable Long id) {
        return rolePermissionService.deleteRolePermission(id)
                .thenReturn(ApiResult.successResult("删除成功", null));
    }
}

