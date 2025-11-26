package com.springboot.admin.service.impl;

import com.springboot.admin.model.entity.sys.SysPermission;
import com.springboot.admin.model.entity.sys.SysRole;
import com.springboot.admin.repository.custom.SysPermissionRepositoryCustom;
import com.springboot.admin.repository.single.SysPermissionRepository;
import com.springboot.admin.service.ISysPermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 权限表 Service 实现类
 * <p>
 * 封装权限相关业务逻辑：
 * - 单表操作：增删改查、查询、判断存在
 * - 多表操作：权限 → 角色
 */
@Service
@Slf4j
public class SysPermissionServiceImpl implements ISysPermissionService {


    private final SysPermissionRepository permissionRepository;
    private final SysPermissionRepositoryCustom permissionRepositoryCustom;

    public SysPermissionServiceImpl(SysPermissionRepository permissionRepository,
                                    SysPermissionRepositoryCustom permissionRepositoryCustom) {
        this.permissionRepository = permissionRepository;
        this.permissionRepositoryCustom = permissionRepositoryCustom;
    }

    /** ---------------- 单表操作 ---------------- */

    @Override
    public Mono<SysPermission> getPermissionById(Long id) {
        return permissionRepository.findById(id);
    }

    @Override
    public Mono<SysPermission> addPermission(SysPermission permission) {
        return permissionRepository.save(permission);
    }

    @Override
    public Mono<SysPermission> updatePermission(SysPermission permission) {
        return permissionRepository.save(permission);
    }

    @Override
    public Mono<Void> deletePermission(Long id) {
        return permissionRepository.deleteById(id);
    }

    @Override
    public Flux<SysPermission> getPermissionList() {
        return permissionRepository.findAll();
    }

    @Override
    public Mono<Boolean> existsByPermissionCode(String permissionCode) {
        return permissionRepository.existsByPermissionCode(permissionCode);
    }



    /** ---------------- 多表操作 ---------------- */

    @Override
    public Flux<SysPermission> listPermissionsByUserId(Long userId) {
        return permissionRepositoryCustom.listPermissionsByUserId(userId);
    }

    @Override
    public Flux<SysPermission> listPermissionsByRoleId(Long roleId) {
        return permissionRepositoryCustom.listPermissionsByRoleId(roleId);
    }
}
