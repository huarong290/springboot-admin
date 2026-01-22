package com.springboot.admin.controller;

import com.springboot.admin.annotation.Loggable;
import com.springboot.admin.common.ApiResult;
import com.springboot.admin.model.dto.user.SysUserDTO;
import com.springboot.admin.model.dto.user.SysUserQueryDTO;
import com.springboot.admin.model.vo.PageResult;
import com.springboot.admin.model.vo.user.SysUserVO;
import com.springboot.admin.service.ISysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统用户表 前端控制器
 *
 * 提供用户增删改查接口：
 * - 分页查询
 * - 新增用户
 * - 更新用户信息
 * - 单个删除（逻辑/物理）
 * - 批量删除（逻辑/物理）
 * - 根据用户名查询用户信息
 *
 * @author system
 * @since 2026-01-18
 */
@RestController
@RequestMapping("/sys-user")
@Tag(name = "用户管理", description = "用户增删改查接口")
@Slf4j
public class SysUserController {

    @Autowired
    private ISysUserService sysUserService;

    /**
     * 分页查询用户列表
     *
     * @param query 用户查询参数（继承 PageQuery，包含分页和条件）
     * @return 分页结果：用户列表 + 总数 + 页码信息
     */
    @GetMapping("/pageUserList")
    @Operation(summary = "分页查询用户列表")
    @Loggable(logRequest = true, logResponse = true)
    public ApiResult<PageResult<SysUserVO>> pageUserList(SysUserQueryDTO query) {
        return ApiResult.successResult(sysUserService.pageUserList(query));
    }

    /**
     * 新增用户
     *
     * @param dto 用户信息 DTO
     * @return 新增用户 ID
     */
    @PostMapping("/add")
    @Operation(summary = "新增用户")
    @Loggable(logRequest = true, logResponse = true)
    public ApiResult<Long> addUser(@RequestBody SysUserDTO dto) {
        Long id = sysUserService.addUser(dto);
        return ApiResult.successResult(id);
    }

    /**
     * 更新用户信息
     *
     * @param dto 用户信息 DTO
     * @return 更新的用户 ID
     */
    @PutMapping("/update")
    @Operation(summary = "更新用户信息")
    @Loggable(logRequest = true, logResponse = true)
    public ApiResult<Long> updateUser(@RequestBody SysUserDTO dto) {
        Long id = sysUserService.updateUser(dto);
        return ApiResult.successResult(id);
    }

    /**
     * 单个删除用户（逻辑/物理）
     *
     * @param id 用户 ID
     * @return 删除成功记录数（0 或 1）
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除单个用户（逻辑/物理）")
    @Loggable(logRequest = true, logResponse = true)
    public ApiResult<Integer> deleteUser(@RequestParam Long id) {
        int deletedCount = sysUserService.deleteUser(id, false);
        return ApiResult.successResult(deletedCount);
    }

    /**
     * 批量删除用户（逻辑/物理）
     *
     * @param ids 用户 ID 列表
     * @return 删除成功记录数
     */
    @DeleteMapping("/deleteBatch")
    @Operation(summary = "批量删除用户（逻辑/物理）")
    @Loggable(logRequest = true, logResponse = true)
    public ApiResult<Integer> deleteUsers(@RequestParam List<Long> ids) {
        int deletedCount = sysUserService.deleteUsers(ids, false);
        return ApiResult.successResult(deletedCount);
    }

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户 DTO
     */
    @GetMapping("/getByUsername")
    @Operation(summary = "根据用户名查询用户信息")
    @Loggable(logRequest = true, logResponse = true)
    public ApiResult<SysUserDTO> getUserDTOByUsername(@RequestParam String username) {
        SysUserDTO dto = sysUserService.getUserDTOByUsername(username);
        return ApiResult.successResult(dto);
    }

    /**
     * 根据用户ID查询用户信息
     *
     * @param userId 用户ID
     * @return 用户 DTO
     */
    @GetMapping("/getById")
    @Operation(summary = "根据用户ID查询用户信息")
    @Loggable(logRequest = true, logResponse = true)
    public ApiResult<SysUserDTO> getSysUserDtoByUserId(@RequestParam Long userId) {
        SysUserDTO dto = sysUserService.getSysUserDtoByUserId(userId);
        return ApiResult.successResult(dto);
    }

}
