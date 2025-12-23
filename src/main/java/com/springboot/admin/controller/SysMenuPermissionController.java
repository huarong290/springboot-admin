package com.springboot.admin.controller;

import com.springboot.admin.annotation.Logable;
import com.springboot.admin.common.ApiResult;
import com.springboot.admin.model.dto.BindResultDTO;
import com.springboot.admin.service.ISysMenuPermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 菜单权限点关联 Controller
 * <p>
 * 提供菜单与权限点关联的 REST API：
 * - 查询菜单绑定的权限点
 * - 新增菜单权限点绑定
 * - 删除菜单权限点绑定
 */
@RestController
@RequestMapping("/api/menuPermission")
@Tag(name = "菜单权限点关联", description = "菜单与权限点绑定接口")
public class SysMenuPermissionController {

    @Autowired
    private ISysMenuPermissionService sysMenuPermissionService;

    @PostMapping("/bindMenuPermissions/{menuId}")
    @Operation(summary = "增量绑定菜单权限")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<BindResultDTO>> bindMenuPermissions(@PathVariable Long menuId,
                                                              @RequestBody List<Long> permissionIds) {
        return sysMenuPermissionService.bindMenuPermissions(menuId, permissionIds)
                .map(result -> ApiResult.successResult(
                        "菜单权限绑定成功，新增 " + result.getAddedCount() + " 条，删除 " + result.getRemovedCount() + " 条",
                        result
                ));
    }
}
