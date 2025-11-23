package com.springboot.admin.service.impl;

import com.springboot.admin.model.entity.sys.SysRolePermission;
import com.springboot.admin.repository.SysRolePermissionRepository;
import com.springboot.admin.service.ISysRolePermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 角色权限点关联 Service 实现类
 */
@Service
@RequiredArgsConstructor
public class SysRolePermissionServiceImpl implements ISysRolePermissionService {

    private final SysRolePermissionRepository rolePermissionRepository;

    @Override
    public Flux<SysRolePermission> getPermissionsByRoleId(Long roleId) {
        return rolePermissionRepository.findByRoleId(roleId);
    }

    @Override
    public Mono<SysRolePermission> addRolePermission(SysRolePermission rolePermission) {
        return rolePermissionRepository.save(rolePermission);
    }

    @Override
    public Mono<Void> deleteRolePermission(Long id) {
        return rolePermissionRepository.deleteById(id);
    }
}
