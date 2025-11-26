package com.springboot.admin.service.impl;

import com.springboot.admin.model.entity.sys.SysRole;
import com.springboot.admin.repository.custom.SysRoleRepositoryCustom;
import com.springboot.admin.repository.single.SysRoleRepository;
import com.springboot.admin.service.ISysRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 角色表 Service 实现类
 * 基于响应式编程，封装角色相关业务逻辑
 * 企业级 MySQL 风格：单表用 R2dbcEntityTemplate，多表用 DatabaseClient
 */
@Service
@Slf4j
public class SysRoleServiceImpl implements ISysRoleService {

    private final SysRoleRepository roleRepository;
    private final SysRoleRepositoryCustom roleRepositoryCustom;

    public SysRoleServiceImpl(SysRoleRepository roleRepository,
                              SysRoleRepositoryCustom roleRepositoryCustom) {
        this.roleRepository = roleRepository;
        this.roleRepositoryCustom = roleRepositoryCustom;
    }
    /** ---------------- 单表操作 ---------------- */

    @Override
    public Mono<SysRole> getRoleById(Long id) {
        return roleRepository.findById(id);
    }

    @Override
    public Mono<SysRole> getRoleByCode(String roleCode) {
        return roleRepository.findByRoleCode(roleCode);
    }

    @Override
    public Mono<SysRole> addRole(SysRole role) {
        return roleRepository.save(role);
    }

    @Override
    public Mono<SysRole> updateRole(SysRole role) {
        return roleRepository.save(role);
    }

    @Override
    public Mono<Void> deleteRole(Long id) {
        return roleRepository.deleteById(id);
    }

    @Override
    public Mono<Void> deleteRoles(Iterable<Long> ids) {
        return roleRepository.deleteAllById(ids);
    }


    @Override
    public Flux<SysRole> listRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Mono<Boolean> existsByRoleCode(String roleCode) {
        return roleRepository.existsByRoleCode(roleCode);
    }



    /** ---------------- 多表关联 ---------------- */

    @Override
    public Flux<SysRole> listRolesByUserId(Long userId) {
        return roleRepositoryCustom.findRolesByUserId(userId);
    }

    /**
     * 根据权限ID查询角色列表
     *
     * @param permissionId 权限ID
     * @return Flux<SysRole> 响应式流，返回拥有该权限的角色集合
     */
    @Override
    public Flux<SysRole> findRolesByPermissionId(Long permissionId) {
        return roleRepositoryCustom.findRolesByPermissionId(permissionId);
    }
    /**
     * 根据菜单D查询角色列表
     *
     * @param menuId 菜单D
     * @return Flux<SysRole> 响应式流，返回该菜单拥有的角色集合
     */
    @Override
    public Flux<SysRole> listPermissionsByRoleId(Long menuId) {
        return roleRepositoryCustom.findRolesByMenuId(menuId);
    }
}
