package com.springboot.admin.service.impl;

import com.springboot.admin.model.entity.sys.SysUserRole;
import com.springboot.admin.repository.single.SysUserRoleRepository;
import com.springboot.admin.service.ISysUserRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 用户角色关联 Service 实现类
 *
 * 提供用户与角色关联的业务逻辑实现：
 * - 根据用户ID查询角色关联关系
 * - 新增用户角色关联
 * - 删除用户角色关联
 *
 * 使用响应式编程（Mono/Flux）返回结果，保证非阻塞。
 */
@Service
@Slf4j
public class SysUserRoleServiceImpl implements ISysUserRoleService {

    private final SysUserRoleRepository sysUserRoleRepository;

    public SysUserRoleServiceImpl(SysUserRoleRepository sysUserRoleRepository) {
        this.sysUserRoleRepository = sysUserRoleRepository;
    }

    /**
     * 根据用户ID查询角色关联关系
     *
     * @param userId 用户ID
     * @return Flux<SysUserRole> 响应式流，返回该用户绑定的角色列表
     */
    @Override
    public Flux<SysUserRole> getRolesByUserId(Long userId) {
        log.info("查询用户ID={} 的角色关联关系", userId);
        return sysUserRoleRepository.findByUserId(userId)
                .doOnComplete(() -> log.info("用户ID={} 的角色查询完成", userId))
                .doOnError(e -> log.error("查询用户角色失败: {}", e.getMessage(), e));
    }

    /**
     * 新增用户角色关联
     *
     * @param userRole 用户角色对象
     * @return Mono<SysUserRole> 响应式单对象，返回保存后的实体
     */
    @Override
    public Mono<SysUserRole> addUserRole(SysUserRole userRole) {
        log.info("新增用户角色关联: {}", userRole);
        return sysUserRoleRepository.save(userRole)
                .doOnSuccess(saved -> log.info("用户角色关联保存成功: {}", saved))
                .doOnError(e -> log.error("保存用户角色关联失败: {}", e.getMessage(), e));
    }

    /**
     * 删除用户角色关联
     *
     * @param id 主键ID
     * @return Mono<Void> 响应式空对象，表示删除完成
     */
    @Override
    public Mono<Void> deleteUserRole(Long id) {
        log.info("删除用户角色关联，ID={}", id);
        return sysUserRoleRepository.deleteById(id)
                .doOnSuccess(v -> log.info("用户角色关联删除成功，ID={}", id))
                .doOnError(e -> log.error("删除用户角色关联失败: {}", e.getMessage(), e));
    }
}

