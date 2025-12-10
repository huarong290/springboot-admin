package com.springboot.admin.service.impl;

import com.springboot.admin.model.entity.sys.SysRolePermission;
import com.springboot.admin.repository.custom.SysRolePermissionRepositoryCustom;
import com.springboot.admin.repository.single.SysRolePermissionRepository;
import com.springboot.admin.service.ISysRolePermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 角色权限点关联 Service 实现类
 *
 * 提供角色与权限点关联的业务逻辑实现：
 * - 根据角色ID查询权限点关联关系
 * - 新增角色权限点关联
 * - 删除角色权限点关联
 *
 * 使用响应式编程（Mono/Flux）返回结果，保证非阻塞。
 */
@Service
@Slf4j
public class SysRolePermissionServiceImpl implements ISysRolePermissionService {

    private final SysRolePermissionRepository sysRolePermissionRepository;
    private final SysRolePermissionRepositoryCustom sysRolePermissionRepositoryCustom;

    public SysRolePermissionServiceImpl(SysRolePermissionRepository sysRolePermissionRepository,
                                        SysRolePermissionRepositoryCustom sysRolePermissionRepositoryCustom) {
        this.sysRolePermissionRepository = sysRolePermissionRepository;
        this.sysRolePermissionRepositoryCustom = sysRolePermissionRepositoryCustom;
    }

    /**
     * 根据角色ID查询权限点关联关系
     *
     * @param roleId 角色ID
     * @return Flux<SysRolePermission> 响应式流，返回该角色绑定的权限点列表
     */
    @Override
    public Flux<SysRolePermission> getPermissionsByRoleId(Long roleId) {
        log.info("查询角色ID={} 的权限点关联关系", roleId);
        return sysRolePermissionRepositoryCustom.findByRoleId(roleId)
                .doOnComplete(() -> log.info("角色ID={} 的权限点查询完成", roleId))
                .doOnError(e -> log.error("查询角色权限点失败: {}", e.getMessage(), e));
    }

    /**
     * 新增角色权限点关联
     *
     * @param rolePermission 角色权限点关联对象
     * @return Mono<SysRolePermission> 响应式单对象，返回保存后的实体
     */
    @Override
    public Mono<SysRolePermission> addRolePermission(SysRolePermission rolePermission) {
        log.info("新增角色权限点关联: {}", rolePermission);
        return sysRolePermissionRepository.save(rolePermission)
                .doOnSuccess(saved -> log.info("角色权限点关联保存成功: {}", saved))
                .doOnError(e -> log.error("保存角色权限点关联失败: {}", e.getMessage(), e));
    }

    /**
     * 删除角色权限点关联
     *
     * @param id 主键ID
     * @return Mono<Void> 响应式空对象，表示删除完成
     */
    @Override
    public Mono<Void> deleteRolePermission(Long id) {
        log.info("删除角色权限点关联，ID={}", id);
        return sysRolePermissionRepository.deleteById(id)
                .doOnSuccess(v -> log.info("角色权限点关联删除成功，ID={}", id))
                .doOnError(e -> log.error("删除角色权限点关联失败: {}", e.getMessage(), e));
    }
}
