package com.springboot.admin.controller;

import com.springboot.admin.annotation.Logable;
import com.springboot.admin.common.ApiResult;
import com.springboot.admin.model.dto.menu.SysMenuDTO;
import com.springboot.admin.model.vo.menu.SysMenuTreeVO;
import com.springboot.admin.model.vo.menu.SysMenuVO;
import com.springboot.admin.service.ISysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 菜单管理 Controller
 *
 * 提供菜单相关的 REST API：
 * - 增删改查
 * - 查询菜单树、用户菜单、角色菜单
 */
@RestController
@RequestMapping("/api/menu")
public class SysMenuController {

    @Autowired
    private ISysMenuService iSysMenuService;

    /**
     * 新增菜单
     */
    @PostMapping("/addMenu")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<Long>> addMenu(@RequestBody SysMenuDTO menuDTO) {
        return iSysMenuService.addMenu(menuDTO)
                .map(ApiResult::successResult);
    }
    /**
     * 更新菜单信息
     */
    @PutMapping("/updateMenu")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<Long>> updateMenu(@RequestBody SysMenuDTO menuDTO) {
        return iSysMenuService.updateMenu(menuDTO)
                .map(ApiResult::successResult);
    }
    /**
     * 删除菜单
     */
    @DeleteMapping("/deleteMenu/{id}")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<Long>> deleteMenu(@PathVariable Long id) {
        return iSysMenuService.deleteMenu(id)
                .map(ApiResult::successResult);
    }
    /**
     * 根据菜单ID查询菜单信息
     */
    @GetMapping("/getMenuById/{id}")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<SysMenuVO>> getMenuById(@PathVariable Long id) {
        return iSysMenuService.getMenuById(id)
                .map(ApiResult::successResult);
    }

    /**
     * 根据父菜单ID查询子菜单
     */
    @GetMapping("/getMenusByParentId/{parentId}")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<List<SysMenuVO>>> getMenusByParentId(@PathVariable Long parentId) {
        return iSysMenuService.getMenusByParentId(parentId)
                .collectList()
                .map(ApiResult::successResult);
    }







    /**
     * 查询所有菜单（平铺列表）
     */
    @GetMapping("/getMenuList")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<List<SysMenuVO>>> getMenuList() {
        return iSysMenuService.getMenuList()
                .collectList()
                .map(ApiResult::successResult);
    }

    /**
     * 查询所有菜单（树形结构）
     */
    @GetMapping("/getMenuTree")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<List<SysMenuTreeVO>>> getMenuTree() {
        return iSysMenuService.getMenuTree()
                .collectList()
                .map(ApiResult::successResult);
    }

    /**
     * 根据用户ID查询菜单列表
     */
    @GetMapping("/getMenuListByUserId/{userId}")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<List<SysMenuVO>>> getMenuListByUserId(@PathVariable Long userId) {
        return iSysMenuService.getMenuListByUserId(userId)
                .collectList()
                .map(ApiResult::successResult);
    }

    /**
     * 根据角色ID查询菜单列表
     */
    @GetMapping("/listMenusByRoleId/{roleId}")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<List<SysMenuVO>>> listMenusByRoleId(@PathVariable Long roleId) {
        return iSysMenuService.listMenusByRoleId(roleId)
                .collectList()
                .map(ApiResult::successResult);
    }

    /**
     * 根据用户ID查询菜单树
     */
    @GetMapping("/getMenuTreeByUserId/{userId}")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<List<SysMenuTreeVO>>> getMenuTreeByUserId(@PathVariable Long userId) {
        return iSysMenuService.getMenuTreeByUserId(userId)
                .collectList()
                .map(ApiResult::successResult);
    }
}
