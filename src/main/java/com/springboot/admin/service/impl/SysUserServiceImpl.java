package com.springboot.admin.service.impl;

import com.springboot.admin.model.entity.sys.SysUser;
import com.springboot.admin.model.entity.sys.SysRole;
import com.springboot.admin.model.entity.sys.SysPermission;
import com.springboot.admin.model.entity.sys.SysMenu;
import com.springboot.admin.repository.single.SysUserRepository;
import com.springboot.admin.repository.custom.SysUserRepositoryCustom;
import com.springboot.admin.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 用户表 Service 实现类
 *
 * 封装用户相关业务逻辑：
 *   - 单表操作：增删改查、查询、判断存在
 *   - 多表操作：用户 → 角色、权限、菜单
 *
 * 企业级 MySQL 风格：
 *   - 单表用 Repository
 *   - 多表用 DatabaseClient 封装在 RepositoryCustom
 */
@Service
@Slf4j
public class SysUserServiceImpl implements ISysUserService {

    private final SysUserRepository userRepository;
    private final SysUserRepositoryCustom userRepositoryCustom;

    public SysUserServiceImpl(SysUserRepository userRepository,
                              SysUserRepositoryCustom userRepositoryCustom) {
        this.userRepository = userRepository;
        this.userRepositoryCustom = userRepositoryCustom;
    }

    /** ---------------- 单表操作 ---------------- */

    /**
     * 根据用户ID获取用户信息
     *
     * @param id 用户ID
     * @return 用户对象 (Mono<SysUser>)
     */
    @Override
    public Mono<SysUser> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 用户对象 (Mono<SysUser>)
     */
    @Override
    public Mono<SysUser> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * 新增用户
     *
     * @param user 用户对象
     * @return 保存后的用户对象 (Mono<SysUser>)
     */
    @Override
    public Mono<SysUser> addUser(SysUser user) {
        return userRepository.save(user);
    }

    /**
     * 更新用户
     *
     * @param user 用户对象
     * @return 更新后的用户对象 (Mono<SysUser>)
     */
    @Override
    public Mono<SysUser> updateUser(SysUser user) {
        return userRepository.save(user);
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return Mono<Void>
     */
    @Override
    public Mono<Void> deleteUser(Long id) {
        return userRepository.deleteById(id);
    }

    /**
     * 批量删除用户
     *
     * @param ids 用户ID集合
     * @return Mono<Void>
     */
    @Override
    public Mono<Void> deleteUsers(Iterable<Long> ids) {
        return userRepository.deleteAllById(ids);
    }

    /**
     * 查询所有用户
     *
     * @return 用户列表 (Flux<SysUser>)
     */
    @Override
    public Flux<SysUser> getSysUserList() {
        return userRepository.findAll();
    }

    /**
     * 判断用户名是否存在
     *
     * @param username 用户名
     * @return true/false (Mono<Boolean>)
     */
    @Override
    public Mono<Boolean> existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /** ---------------- 多表操作 ---------------- */

    /**
     * 根据部门ID查询用户列表
     *
     * @param deptId 部门ID
     * @return 用户集合 (Flux<SysUser>)
     */
    @Override
    public Flux<SysUser> listUsersByDeptId(Long deptId) {
        return userRepositoryCustom.findUsersByDeptId(deptId);
    }

    //    /**
//     * 根据用户ID查询角色列表
//     *
//     * @param userId 用户ID
//     * @return 用户所拥有的角色集合 (Flux<SysRole>)
//     */
//    @Override
//    public Flux<SysRole> listRolesByUserId(Long userId) {
//        return userRepositoryCustom.findRolesByUserId(userId);
//    }

//    /**
//     * 根据用户ID查询权限列表
//     *
//     * @param userId 用户ID
//     * @return 用户所拥有的权限集合 (Flux<SysPermission>)
//     */
//    @Override
//    public Flux<SysPermission> listPermissionsByUserId(Long userId) {
//        return userRepositoryCustom.findPermissionsByUserId(userId);
//    }

//    /**
//     * 根据用户ID查询菜单列表
//     *
//     * @param userId 用户ID
//     * @return 用户所拥有的菜单集合 (Flux<SysMenu>)
//     */
//    @Override
//    public Flux<SysMenu> listMenusByUserId(Long userId) {
//        return userRepositoryCustom.findMenusByUserId(userId);
//    }
}
