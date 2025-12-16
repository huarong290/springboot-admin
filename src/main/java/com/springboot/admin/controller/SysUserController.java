package com.springboot.admin.controller;

import com.springboot.admin.annotation.Logable;
import com.springboot.admin.common.ApiResult;
import com.springboot.admin.common.ApiResultCode;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

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
     * 分页查询用户列表
     *
     * @param query 用户查询参数（继承 PageQuery，包含分页和条件）
     * @return 返回分页结果：用户列表 + 总数 + 页码信息
     */
    @GetMapping("/pageUserList")
    @Operation(summary = "分页查询用户列表")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<PageResult<SysUserVO>>> pageUserList(SysUserQueryDTO query) {
        return userService.pageUserList(query).map(ApiResult::successResult);

    }
    /**
     * 新增用户
     *
     * @param sysUserDTO 用户传输对象
     * @return 返回新增用户对象ID
     */
    @PostMapping("/addUser")
    @Operation(summary = "新增用户")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<Long>> addUser(@RequestBody SysUserDTO sysUserDTO) {
        return userService.addUser(sysUserDTO).map(ApiResult::successResult);
    }
    /**
     * 编辑用户
     *
     * @param sysUserDTO 用户传输对象
     * @return 返回编辑用户影响条数
     */
    @PutMapping("/updateUser")
    @Operation(summary = "更新用户信息")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<Long>> updateUser(@RequestBody SysUserDTO sysUserDTO) {
        return userService.updateUser(sysUserDTO).map(ApiResult::successResult);
    }
    /**
     * 删除用户
     *
     * @param id 用户主键ID
     * @return 返回删除用户影响条数
     */
    @DeleteMapping("/deleteUser/{id}")
    @Operation(summary = "删除用户")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<Long>> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id).map(rows -> ApiResult.successResult("删除成功", rows));
    }

    /**
     * 根据用户ID查询用户信息
     *
     * @param id 用户ID
     * @return 返回用户对象，如果不存在则返回 NOT_FOUND
     */
    @GetMapping("/getUserById/{id}")
    @Operation(summary = "根据用户ID查询用户信息")
    public Mono<ApiResult<SysUserVO>> getUserById(@PathVariable Long id) {
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
    @GetMapping("/getUserByUsername/{username}")
    @Operation(summary = "根据用户名查询用户信息")
    public Mono<ApiResult<SysUserDTO>> getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username)
                .map(ApiResult::successResult)
                .switchIfEmpty(Mono.just(ApiResult.failResult(ApiResultCode.NOT_FOUND, "用户不存在")));
    }

    @DeleteMapping("/deleteUsers")
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
    @GetMapping("/getSysUserList")
    @Operation(summary = "查询所有用户")
    public Mono<ApiResult<List<SysUserVO>>> getSysUserList() {
        return userService.getSysUserList().collectList().map(ApiResult::successResult);
    }

    /**
     * 判断用户名是否存在
     *
     * @param username 用户名
     * @return 返回 true/false
     */
    @GetMapping("/existsByUsername/{username}")
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
    @GetMapping("/listUsersByDeptId/{deptId}")
    @Operation(summary = "根据部门ID查询用户列表")
    public Mono<ApiResult<List<SysUserVO>>> listUsersByDeptId(@PathVariable Long deptId) {
        return userService.listUsersByDeptId(deptId).collectList().map(ApiResult::successResult);
    }
}
