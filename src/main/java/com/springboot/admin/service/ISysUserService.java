package com.springboot.admin.service;

import com.springboot.admin.model.entity.sys.SysUser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 用户表 Service 接口
 *
 * 提供用户相关的业务逻辑方法。
 */
public interface ISysUserService {

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return Mono<SysUser> 响应式单对象，可能为空
     */
    Mono<SysUser> getUserByUsername(String username);

    /**
     * 新增用户
     *
     * @param user 用户对象
     * @return Mono<SysUser> 响应式单对象，返回保存后的实体
     */
    Mono<SysUser> addUser(SysUser user);

    /**
     * 更新用户信息
     *
     * @param user 用户对象
     * @return Mono<SysUser> 响应式单对象，返回更新后的实体
     */
    Mono<SysUser> updateUser(SysUser user);

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return Mono<Void> 响应式空对象，表示删除完成
     */
    Mono<Void> deleteUser(Long id);

    /**
     * 查询所有用户
     *
     * @return Flux<SysUser> 响应式流，返回多个用户对象
     */
    Flux<SysUser> listUsers();
}

