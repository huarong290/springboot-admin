package com.springboot.admin.repository.single;

import com.springboot.admin.model.entity.sys.SysUser;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

/**
 * 用户表 Repository
 *
 * 用于封装用户表的单表操作：
 *   - 增删改查
 *   - 根据用户名查询
 *   - 判断用户名是否存在
 *
 * 继承 ReactiveCrudRepository，自动生成常见的 CRUD 方法。
 */
public interface SysUserRepository extends ReactiveCrudRepository<SysUser, Long> {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户对象 (Mono<SysUser>)
     */
    Mono<SysUser> findByUsername(String username);

    /**
     * 判断用户名是否存在
     *
     * @param username 用户名
     * @return true/false (Mono<Boolean>)
     */
    Mono<Boolean> existsByUsername(String username);
}

