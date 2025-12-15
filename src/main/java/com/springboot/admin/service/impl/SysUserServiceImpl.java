package com.springboot.admin.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.springboot.admin.convert.SysUserConvert;
import com.springboot.admin.model.dto.user.SysUserDTO;
import com.springboot.admin.model.dto.user.SysUserQueryDTO;
import com.springboot.admin.model.entity.sys.SysUser;
import com.springboot.admin.model.vo.PageResult;
import com.springboot.admin.model.vo.user.SysUserVO;
import com.springboot.admin.repository.custom.SysUserRepositoryCustom;
import com.springboot.admin.repository.single.SysUserRepository;
import com.springboot.admin.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

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
    private final SysUserConvert userConvert;
    private final PasswordEncoder passwordEncoder; // 注入
    public SysUserServiceImpl(SysUserRepository userRepository,
                              SysUserRepositoryCustom userRepositoryCustom,
                              SysUserConvert userConvert,
                              PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userRepositoryCustom = userRepositoryCustom;
        this.userConvert = userConvert;
        this.passwordEncoder = passwordEncoder;
    }

    /** ---------------- 单表操作 ---------------- */

    @Override
    public Mono<PageResult<SysUserVO>> pageUserList(SysUserQueryDTO query) {
        return userRepositoryCustom.pageUserList(query);
    }

    /**
     * 根据用户ID获取用户信息
     *
     * @param id 用户ID
     * @return 用户对象 (Mono<SysUser>)
     */
    @Override
    public Mono<SysUserVO> getUserById(Long id) {
        return userRepository.findById(id).map(userConvert::toVO);
    }

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 用户对象 (Mono<SysUser>)
     */
    @Override
    public Mono<SysUserDTO> getUserByUsername(String username) {
        return userRepositoryCustom.findByUsername(username).map(userConvert::toDTO);
    }

    /**
     * 新增用户
     *
     * @param dto 用户对象
     * @return 保存后的用户对象 (Mono<SysUser>)
     */
    @Override
    public Mono<Long> addUser(SysUserDTO dto) {
        // DTO 转换成实体
        SysUser user = userConvert.toEntity(dto);
        // 特殊处理密码
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        // 调用自定义仓库方法，返回主键 ID
        return userRepositoryCustom.insertUser(user);
    }
    /**
     * 更新用户
     *
     * @param sysUserDTO 用户对象
     * @return 更新后的用户对象 (Mono<SysUser>)
     */
    @Override

    public Mono<Long> updateUser(SysUserDTO sysUserDTO) {
        // 判断密码是否为空并加密
        String encodedPassword = null;
        if (sysUserDTO.getPassword() != null && !sysUserDTO.getPassword().isBlank()) {
            encodedPassword = passwordEncoder.encode(sysUserDTO.getPassword());
        }
        // 调用自定义仓库方法，返回更新条数
        return userRepositoryCustom.updateUser(sysUserDTO, encodedPassword);
    }



    /**
     * 查询所有用户
     *
     * @return 用户列表 (Flux<SysUser>)
     */
    @Override
    public Flux<SysUserVO> getSysUserList() {
        return userRepository.findAll()
                .doOnNext(user -> log.info("查询到用户: {}", JSONObject.toJSONString(user)))
                .map(user -> {
                    SysUserVO vo = userConvert.toVO(user);
                    log.info("转换后的VO: {}", vo);
                    return vo;
                });
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

    /** ---------------- 自定义单表操作 ---------------- */

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return Mono<Long>
     */
    @Override
    public Mono<Long> deleteUser(Long id) {
        return userRepositoryCustom.deleteUserById(id);
    }

    /**
     * 批量删除用户
     *
     * @param ids 用户ID集合
     * @return Mono<Long>
     */
    @Override
    public Mono<Long> deleteUsers(Iterable<Long> ids) {
        return userRepositoryCustom.deleteUsersByIds(ids);
    }
    /** ---------------- 自定义多表操作 ---------------- */
    /**
     * 根据部门ID查询用户列表
     *
     * @param deptId 部门ID
     * @return 用户集合 (Flux<SysUser>)
     */
    @Override
    public Flux<SysUserVO> listUsersByDeptId(Long deptId) {
        return userRepositoryCustom.findUsersByDeptId(deptId).map(userConvert::toVO);
    }

    @Override
    public Mono<Long> updateLastLoginTime(Long userId, LocalDateTime lastLoginTime) {
        log.info("updateLastLoginTime, userId = {}, lastLoginTime = {}", userId, lastLoginTime);
        // 调用自定义仓库方法更新 last_login_time 字段
        return userRepositoryCustom.updateLastLoginTime(userId, lastLoginTime)
                .doOnNext(rows -> {
                    if (rows > 0) {
                        log.info("用户 {} 登录时间已更新为 {}", userId, lastLoginTime);
                    } else {
                        log.warn("更新用户 {} 登录时间失败，未找到记录", userId);
                    }
                });
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
