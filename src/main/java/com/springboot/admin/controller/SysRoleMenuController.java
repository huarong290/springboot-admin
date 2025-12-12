package com.springboot.admin.controller;

import com.springboot.admin.annotation.Logable;
import com.springboot.admin.common.ApiResult;
import com.springboot.admin.model.dto.rolemenu.SysRoleMenuDTO;
import com.springboot.admin.model.entity.sys.SysRoleMenu;
import com.springboot.admin.model.vo.rolemenu.SysRoleMenuVO;
import com.springboot.admin.service.ISysRoleMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 角色菜单关联 Controller
 *
 * 提供角色与菜单关联的 REST API：
 * - 查询角色绑定的菜单
 * - 新增角色菜单绑定
 * - 删除角色菜单绑定
 * - 查询所有角色菜单关联
 */
@RestController
@RequestMapping("/api/roleMenu")
@Tag(name = "角色菜单关联", description = "角色与菜单绑定接口")
public class SysRoleMenuController {

    @Autowired
    private ISysRoleMenuService roleMenuService;

    @GetMapping("/getMenusByRoleId/{roleId}")
    @Operation(summary = "根据角色ID查询菜单关联关系")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<List<SysRoleMenuVO>>> getMenusByRoleId(@PathVariable Long roleId) {
        return roleMenuService.getMenusByRoleId(roleId)
                .collectList()
                .map(ApiResult::successResult);
    }

    @PostMapping("/addRoleMenu")
    @Operation(summary = "新增角色菜单关联")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<Long>> addRoleMenu(@RequestBody SysRoleMenuDTO sysRoleMenuDTO) {
        return roleMenuService.addRoleMenu(sysRoleMenuDTO)
                .map(ApiResult::successResult);
    }

    @DeleteMapping("/deleteRoleMenu/{id}")
    @Operation(summary = "删除角色菜单关联")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<Long>> deleteRoleMenu(@PathVariable Long id) {
        return roleMenuService.deleteRoleMenu(id)
                .thenReturn(ApiResult.successResult("删除成功", null));
    }

    @GetMapping("/getRoleMenuList")
    @Operation(summary = "查询所有角色菜单关联")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<List<SysRoleMenuVO>>> getRoleMenuList() {
        return roleMenuService.getRoleMenuList()
                .collectList()
                .map(ApiResult::successResult);
    }
}

