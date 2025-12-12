package com.springboot.admin.service.impl;

import com.springboot.admin.convert.SysUserRoleConvert;
import com.springboot.admin.model.dto.userrole.SysUserRoleDTO;
import com.springboot.admin.model.entity.sys.SysUserRole;
import com.springboot.admin.model.vo.rolemenu.SysRoleMenuVO;
import com.springboot.admin.model.vo.userrole.SysUserRoleVO;
import com.springboot.admin.repository.custom.SysUserRoleRepositoryCustom;
import com.springboot.admin.repository.single.SysRoleMenuRepository;
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

    private final SysUserRoleRepositoryCustom sysUserRoleRepositoryCustom;

    private final SysUserRoleConvert sysUserRoleConvert;

    public SysUserRoleServiceImpl(SysUserRoleRepository sysUserRoleRepository,SysUserRoleRepositoryCustom sysUserRoleRepositoryCustom,SysUserRoleConvert sysUserRoleConvert) {
        this.sysUserRoleRepository = sysUserRoleRepository;
        this.sysUserRoleRepositoryCustom = sysUserRoleRepositoryCustom;
        this.sysUserRoleConvert = sysUserRoleConvert;
    }

    /**
     * 根据用户ID查询角色关联关系
     *
     * @param userId 用户ID
     * @return Flux<SysUserRole> 响应式流，返回该用户绑定的角色列表
     */
    @Override
    public Flux<SysUserRoleVO> getRolesByUserId(Long userId) {
        log.info("查询用户ID={} 的角色关联关系", userId);
        return sysUserRoleRepository.findByUserId(userId)
                .map(sysUserRoleConvert::toVO)
                .doOnComplete(() -> log.info("用户ID={} 的角色查询完成", userId))
                .doOnError(e -> log.error("查询用户角色失败: {}", e.getMessage(), e));
    }

    /**
     * 新增用户角色关联
     *
     * @param sysUserRoleDTO 用户角色对象
     * @return Mono<SysUserRole> 响应式单对象，返回保存后的实体
     */
    @Override
    public Mono<Long> addUserRole(SysUserRoleDTO sysUserRoleDTO) {
        log.info("新增用户角色关联: {}", sysUserRoleDTO);
        SysUserRole sysUserRole = sysUserRoleConvert.toEntity(sysUserRoleDTO);
        return sysUserRoleRepository.save(sysUserRole).map(SysUserRole::getId);
    }

    /**
     * 删除用户角色关联
     *
     * @param id 主键ID
     * @return Mono<Void> 响应式空对象，表示删除完成
     */
    @Override
    public Mono<Long> deleteUserRole(Long id) {
        log.info("删除用户角色关联，ID={}", id);
        return sysUserRoleRepositoryCustom.deleteUserRoleById(id,true);

    }
}

