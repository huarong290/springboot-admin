package com.springboot.admin.controller;

import com.springboot.admin.annotation.Loggable;
import com.springboot.admin.common.ApiResult;
import com.springboot.admin.model.dto.menu.SysMenuDTO;
import com.springboot.admin.model.vo.menu.SysMenuTreeVO;
import com.springboot.admin.model.vo.menu.SysMenuVO;
import com.springboot.admin.service.ISysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理 Controller（非响应式 / Spring MVC）
 *
 * 提供菜单相关的 REST API：
 * - 菜单增删改查
 * - 菜单树查询
 * - 用户/角色菜单查询
 */
@RestController
@RequestMapping("/api/menu")
public class SysMenuController {

    @Autowired
    private ISysMenuService sysMenuService;

    /**
     * 新增菜单
     *
     * @param menuDTO 菜单信息
     * @return 新增菜单ID
     */
    @PostMapping("/addMenu")
    @Loggable(logRequest = true, logResponse = true)
    public ApiResult<Long> addMenu(@RequestBody SysMenuDTO menuDTO) {
        Long menuId = sysMenuService.addMenu(menuDTO);
        return ApiResult.successResult(menuId);
    }

    /**
     * 更新菜单信息
     *
     * @param menuDTO 菜单信息
     * @return 更新记录数
     */
    @PutMapping("/updateMenu")
    @Loggable(logRequest = true, logResponse = true)
    public ApiResult<Integer> updateMenu(@RequestBody SysMenuDTO menuDTO) {
        Integer result = sysMenuService.updateMenu(menuDTO);
        return ApiResult.successResult(result);
    }

    /**
     * 删除菜单
     *
     * @param id 菜单ID
     * @return 删除结果
     */
    @DeleteMapping("/deleteMenu/{id}")
    @Loggable(logRequest = true, logResponse = true)
    public ApiResult<Integer> deleteMenu(@PathVariable Long id) {
        Integer result = sysMenuService.deleteMenu(id,false);
        return ApiResult.successResult(result);
    }

    /**
     * 根据菜单ID查询菜单信息
     *
     * @param id 菜单ID
     * @return 菜单详情
     */
    @GetMapping("/getMenuById/{id}")
    @Loggable(logRequest = true, logResponse = true)
    public ApiResult<SysMenuVO> getMenuById(@PathVariable Long id) {
        SysMenuVO menuVO = sysMenuService.getMenuById(id);
        return ApiResult.successResult(menuVO);
    }

    /**
     * 根据父菜单ID查询子菜单
     *
     * @param parentId 父菜单ID
     * @return 子菜单列表
     */
    @GetMapping("/getMenusByParentId/{parentId}")
    @Loggable(logRequest = true, logResponse = true)
    public ApiResult<List<SysMenuVO>> getMenusByParentId(@PathVariable Long parentId) {
        List<SysMenuVO> list = sysMenuService.getMenusByParentId(parentId);
        return ApiResult.successResult(list);
    }

    /**
     * 查询所有菜单（平铺列表）
     *
     * @return 菜单列表
     */
    @GetMapping("/getMenuList")
    @Loggable(logRequest = true, logResponse = true)
    public ApiResult<List<SysMenuVO>> getMenuList() {
        List<SysMenuVO> list = sysMenuService.getMenuList();
        return ApiResult.successResult(list);
    }

    /**
     * 查询所有菜单（树形结构）
     *
     * @return 菜单树
     */
    @GetMapping("/getMenuTree")
    @Loggable(logRequest = true, logResponse = true)
    public ApiResult<List<SysMenuTreeVO>> getMenuTree() {
        List<SysMenuTreeVO> tree = sysMenuService.getMenuTree();
        return ApiResult.successResult(tree);
    }

    /**
     * 根据用户ID查询菜单列表
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    @GetMapping("/getMenuListByUserId/{userId}")
    @Loggable(logRequest = true, logResponse = true)
    public ApiResult<List<SysMenuVO>> getMenuListByUserId(@PathVariable Long userId) {
        List<SysMenuVO> list = sysMenuService.getMenuListByUserId(userId);
        return ApiResult.successResult(list);
    }

    /**
     * 根据角色ID查询菜单列表
     *
     * @param roleId 角色ID
     * @return 菜单列表
     */
    @GetMapping("/listMenusByRoleId/{roleId}")
    @Loggable(logRequest = true, logResponse = true)
    public ApiResult<List<SysMenuVO>> listMenusByRoleId(@PathVariable Long roleId) {
        List<SysMenuVO> list = sysMenuService.listMenusByRoleId(roleId);
        return ApiResult.successResult(list);
    }

    /**
     * 根据用户ID查询菜单树
     *
     * @param userId 用户ID
     * @return 菜单树
     */
    @GetMapping("/getMenuTreeByUserId/{userId}")
    @Loggable(logRequest = true, logResponse = true)
    public ApiResult<List<SysMenuTreeVO>> getMenuTreeByUserId(@PathVariable Long userId) {
        List<SysMenuTreeVO> tree = sysMenuService.getMenuTreeByUserId(userId);
        return ApiResult.successResult(tree);
    }
}
