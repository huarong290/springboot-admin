package com.springboot.admin.controller;

import com.springboot.admin.annotation.Logable;
import com.springboot.admin.common.ApiResult;
import com.springboot.admin.model.dto.permission.SysPermissionDTO;
import com.springboot.admin.model.entity.sys.SysPermission;
import com.springboot.admin.model.vo.permission.SysPermissionVO;
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
    private ISysPermissionService sysPermissionService;

    @GetMapping("/getPermissionById/{id}")
    @Operation(summary = "根据权限点ID查询权限信息")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<SysPermissionVO>> getPermissionById(@PathVariable Long id) {
        return sysPermissionService.getPermissionById(id)
                .map(ApiResult::successResult);
    }

    @PostMapping("/addPermission")
    @Operation(summary = "新增权限点")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<Long>> addPermission(@RequestBody SysPermissionDTO sysPermissionDTO) {
        return sysPermissionService.addPermission(sysPermissionDTO)
                .map(ApiResult::successResult);
    }

    @PutMapping("/updatePermission")
    @Operation(summary = "更新权限点信息")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<Long>> updatePermission(@RequestBody SysPermissionDTO sysPermissionDTO) {
        return sysPermissionService.updatePermission(sysPermissionDTO)
                .map(ApiResult::successResult);
    }

    @DeleteMapping("/deletePermission/{id}")
    @Operation(summary = "删除权限点")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<Long>> deletePermission(@PathVariable Long id) {
        return sysPermissionService.deletePermission(id)
                .map(ApiResult::successResult);
    }

    @GetMapping("/getPermissionList")
    @Operation(summary = "查询所有权限点")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<List<SysPermissionVO>>> getPermissionList() {
        return sysPermissionService.getPermissionList()
                .collectList()
                .map(ApiResult::successResult);
    }

    @GetMapping("/existsByPermissionCode/{permissionCode}")
    @Operation(summary = "判断权限编码是否存在")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<Boolean>> existsByPermissionCode(@PathVariable String permissionCode) {
        return sysPermissionService.existsByPermissionCode(permissionCode)
                .map(ApiResult::successResult);
    }

    @GetMapping("/listPermissionsByUserId/{userId}")
    @Operation(summary = "根据用户ID查询权限点列表")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<List<SysPermissionVO>>> listPermissionsByUserId(@PathVariable Long userId) {
        return sysPermissionService.listPermissionsByUserId(userId)
                .collectList()
                .map(ApiResult::successResult);
    }

    @GetMapping("/listPermissionsByRoleId/{roleId}")
    @Operation(summary = "根据角色ID查询权限点列表")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<List<SysPermissionVO>>> listPermissionsByRoleId(@PathVariable Long roleId) {
        return sysPermissionService.listPermissionsByRoleId(roleId)
                .collectList()
                .map(ApiResult::successResult);
    }
}

