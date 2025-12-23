package com.springboot.admin.controller;

import com.springboot.admin.annotation.Logable;
import com.springboot.admin.common.ApiResult;
import com.springboot.admin.model.dto.BindResultDTO;
import com.springboot.admin.model.dto.userrole.SysUserRoleDTO;
import com.springboot.admin.model.vo.userrole.SysUserRoleVO;
import com.springboot.admin.service.ISysUserRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 用户角色关联 Controller
 * <p>
 * 提供用户与角色关联的 REST API：
 * - 查询用户绑定的角色
 * - 新增用户角色绑定
 * - 删除用户角色绑定
 */
@RestController
@RequestMapping("/api/userRole")
@Tag(name = "用户角色关联", description = "用户与角色绑定接口")
public class SysUserRoleController {

    @Autowired
    private ISysUserRoleService userRoleService;

    @PostMapping("/bindUserRoles/{userId}")
    @Operation(summary = "增量绑定用户角色")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<BindResultDTO>> bindUserRoles(@PathVariable Long userId, @RequestBody List<Long> roleIds) {
        return userRoleService.bindUserRoles(userId, roleIds).map(result -> ApiResult.successResult("用户角色绑定成功，新增 " + result.getAddedCount() + " 条，删除 " + result.getRemovedCount() + " 条", result));
    }

    @GetMapping("/getRolesByUserId/{userId}")
    @Operation(summary = "根据用户ID查询角色关联关系")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<List<SysUserRoleVO>>> getRolesByUserId(@PathVariable Long userId) {
        return userRoleService.getRolesByUserId(userId)
                .collectList()
                .map(ApiResult::successResult);
    }

    @PostMapping("/addUserRole")
    @Operation(summary = "新增用户角色关联")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<Long>> addUserRole(@RequestBody SysUserRoleDTO sysUserRoleDTO) {
        return userRoleService.addUserRole(sysUserRoleDTO)
                .map(ApiResult::successResult);
    }

    @DeleteMapping("/deleteUserRole/{id}")
    @Operation(summary = "删除用户角色关联")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<Long>> deleteUserRole(@PathVariable Long id) {
        return userRoleService.deleteUserRole(id).map(ApiResult::successResult);

    }
}
