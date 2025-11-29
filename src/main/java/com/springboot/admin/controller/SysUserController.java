package com.springboot.admin.controller;

import com.springboot.admin.common.ApiResult;
import com.springboot.admin.common.ApiResultCode;
import com.springboot.admin.model.entity.sys.SysUser;
import com.springboot.admin.service.ISysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 用户管理 Controller
 * 提供用户的增删改查接口
 */
@RestController
@RequestMapping("/api/user")
@Tag(name = "用户管理", description = "用户增删改查接口")
@Slf4j
public class SysUserController {

    @Autowired
    private ISysUserService userService;

    /**
     * 根据用户ID查询用户信息
     *
     * @param id 用户ID
     * @return 返回用户对象，如果不存在则返回 NOT_FOUND
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据用户ID查询用户信息")
    public Mono<ApiResult<SysUser>> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ApiResult::successResult)
                .switchIfEmpty(Mono.just(ApiResult.failResult(ApiResultCode.NOT_FOUND, "用户不存在")));
    }

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 返回用户对象，如果不存在则返回 NOT_FOUND
     */
    @GetMapping("/username/{username}")
    @Operation(summary = "根据用户名查询用户信息")
    public Mono<ApiResult<SysUser>> getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username)
                .map(ApiResult::successResult)
                .switchIfEmpty(Mono.just(ApiResult.failResult(ApiResultCode.NOT_FOUND, "用户不存在")));
    }

    /**
     * 新增用户
     *
     * @param user 用户对象（JSON）
     * @return 返回保存后的用户对象
     */
    @PostMapping("/create")
    @Operation(summary = "新增用户")
    public Mono<ApiResult<SysUser>> addUser(@RequestBody SysUser user) {
        return userService.addUser(user)
                .map(ApiResult::successResult)
                .onErrorResume(e -> {
                    log.error("新增用户失败: {}", e.getMessage(), e);
                    return Mono.just(ApiResult.failResult(ApiResultCode.FAILED, "新增用户失败"));
                });
    }

    /**
     * 更新用户信息
     *
     * @param user 用户对象（JSON）
     * @return 返回更新后的用户对象
     */
    @PutMapping("/update")
    @Operation(summary = "更新用户信息")
    public Mono<ApiResult<SysUser>> updateUser(@RequestBody SysUser user) {
        return userService.updateUser(user)
                .map(ApiResult::successResult)
                .onErrorResume(e -> {
                    log.error("更新用户失败: {}", e.getMessage(), e);
                    return Mono.just(ApiResult.failResult(ApiResultCode.FAILED, "更新用户失败"));
                });
    }



    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除用户")
    public Mono<ApiResult<Long>> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id)
                .map(rows -> ApiResult.successResult("删除成功", rows))
                .onErrorResume(e -> {
                    log.error("删除用户失败: {}", e.getMessage(), e);
                    return Mono.just(ApiResult.failResult(ApiResultCode.FAILED, "删除用户失败"));
                });
    }

    @DeleteMapping("/deleteBatch")
    @Operation(summary = "批量删除用户")
    public Mono<ApiResult<Long>> deleteUsers(@RequestBody Iterable<Long> ids) {
        return userService.deleteUsers(ids)
                .map(rows -> ApiResult.successResult("批量删除成功", rows))
                .onErrorResume(e -> {
                    log.error("批量删除用户失败: {}", e.getMessage(), e);
                    return Mono.just(ApiResult.failResult(ApiResultCode.FAILED, "批量删除用户失败"));
                });
    }


    /**
     * 查询所有用户
     *
     * @return 返回用户列表（Flux 流）
     */
    @GetMapping("/list")
    @Operation(summary = "查询所有用户")
    public Flux<SysUser> getSysUserList() {
        return userService.getSysUserList();
    }

    /**
     * 判断用户名是否存在
     *
     * @param username 用户名
     * @return 返回 true/false
     */
    @GetMapping("/exists/{username}")
    @Operation(summary = "判断用户名是否存在")
    public Mono<ApiResult<Boolean>> existsByUsername(@PathVariable String username) {
        return userService.existsByUsername(username)
                .map(ApiResult::successResult);
    }

    /**
     * 根据部门ID查询用户列表
     *
     * @param deptId 部门ID
     * @return 返回该部门下的用户集合
     */
    @GetMapping("/dept/{deptId}")
    @Operation(summary = "根据部门ID查询用户列表")
    public Flux<SysUser> listUsersByDeptId(@PathVariable Long deptId) {
        return userService.listUsersByDeptId(deptId);
    }
}
