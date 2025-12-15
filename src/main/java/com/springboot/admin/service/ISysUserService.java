package com.springboot.admin.service;

import com.springboot.admin.model.dto.user.SysUserDTO;
import com.springboot.admin.model.dto.user.SysUserQueryDTO;
import com.springboot.admin.model.vo.PageResult;
import com.springboot.admin.model.vo.user.SysUserVO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * 用户表 Service 接口
 * <p>
 * 提供用户相关的业务逻辑方法。
 */
public interface ISysUserService {
    /**
     * 根据条件分页查询用户信息
     *
     * @param query 分页查询参数
     * @return Mono<SysUserVO> 响应式单对象，可能为空
     */
    Mono<PageResult<SysUserVO>> pageUserList(SysUserQueryDTO query);
    /**
     * 根据用户ID查询用户信息
     *
     * @param id 用户ID
     * @return Mono<SysUserVO> 响应式单对象，可能为空
     */
    Mono<SysUserVO> getUserById(Long id);

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return Mono<SysUserDTO> 响应式单对象，可能为空
     */
    Mono<SysUserDTO> getUserByUsername(String username);

    /**
     * 新增用户
     *
     * @param sysUserDTO 用户对象
     * @return Mono<Long> 响应式单对象，返回保存后的实体主键id
     */
    Mono<Long> addUser(SysUserDTO sysUserDTO);

    /**
     * 更新用户信息
     *
     * @param sysUserDTO 用户对象
     * @return Mono<Long> 响应式单对象，返回更新后的记录数
     */
    Mono<Long> updateUser(SysUserDTO sysUserDTO);

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return Mono<Long> 响应式空对象，表示删除完成的数量
     */
    Mono<Long> deleteUser(Long id);

    /**
     * 批量删除用户
     *
     * @param ids 用户ID集合
     * @return Mono<Long>
     */
    Mono<Long> deleteUsers(Iterable<Long> ids);

    /**
     * 查询所有用户
     *
     * @return Flux<SysUser> 响应式流，返回多个用户对象
     */
    Flux<SysUserVO> getSysUserList();

    /**
     * 判断用户名是否存在
     *
     * @param username 用户名
     * @return true/false (Mono<Boolean>)
     */
    Mono<Boolean> existsByUsername(String username);

    /**
     * 根据部门ID查询用户列表
     *
     * @param deptId 部门ID
     * @return 用户集合 (Flux<SysUser>)
     */

    Flux<SysUserVO> listUsersByDeptId(Long deptId);
    /**
     * 更新用户的最近登录时间
     *
     * @param userId 用户ID
     * @param lastLoginTime 最近登录时间
     * @return Mono<Long> 响应式单对象，返回更新后的记录数
     */
    Mono<Long> updateLastLoginTime(Long userId, LocalDateTime lastLoginTime);

}

