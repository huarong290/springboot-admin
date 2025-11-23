package com.springboot.admin.repository;

import com.springboot.admin.model.entity.sys.SysUser;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

/**
 * 用户表 Repository 接口
 *
 * 使用 Spring Data R2DBC 提供的 ReactiveCrudRepository，
 * 可以直接进行响应式的 CRUD 操作。
 *
 * 泛型参数：
 * - SysUser：实体类
 * - Long：主键类型
 */
public interface SysUserRepository extends ReactiveCrudRepository<SysUser, Long> {

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return Mono<SysUser> 响应式单对象，可能为空
     */
    Mono<SysUser> findByUsername(String username);
}
