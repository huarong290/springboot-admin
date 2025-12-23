package com.springboot.admin.service.impl;

import com.springboot.admin.convert.SysUserRoleConvert;
import com.springboot.admin.model.dto.BindResultDTO;
import com.springboot.admin.model.dto.userrole.SysUserRoleDTO;
import com.springboot.admin.model.entity.sys.SysUserRole;
import com.springboot.admin.model.vo.userrole.SysUserRoleVO;
import com.springboot.admin.repository.custom.SysUserRoleRepositoryCustom;
import com.springboot.admin.repository.single.SysUserRoleRepository;
import com.springboot.admin.service.ISysUserRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

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
     * 增量绑定用户角色
     *
     * 核心逻辑：
     * 1. 查询该用户已有的角色集合
     * 2. 计算需要新增的角色（前端传入但数据库没有）
     * 3. 计算需要删除的角色（数据库有但前端没传入）
     * 4. 执行删除和新增操作
     * 5. 返回 BindResultDTO，包含新增和删除的数量
     *
     * @param userId 用户ID
     * @param roleIds 前端传入的最新角色ID集合
     * @return Mono<BindResultDTO> 响应式单对象，包含新增和删除的数量
     */
    @Override
    public Mono<BindResultDTO> bindUserRoles(Long userId, List<Long> roleIds) {
        return sysUserRoleRepositoryCustom.findRoleIdsByUserId(userId)
                .collectList()
                .flatMap(existing -> {
                    // 需要新增的角色：前端传入但数据库没有
                    List<Long> toAdd = roleIds.stream().filter(r -> !existing.contains(r)).toList();
                    log.info("User {} toAdd: {}", userId, toAdd);

                    // 需要删除的角色：数据库有但前端没传入
                    List<Long> toRemove = existing.stream().filter(r -> !roleIds.contains(r)).toList();
                    log.info("User {} toRemove: {}", userId, toRemove);

                    // 执行删除和新增，并返回 BindResultDTO
                    return sysUserRoleRepositoryCustom.deleteByUserIdAndRoleIds(userId, toRemove)
                            .flatMap(removeCount ->
                                    sysUserRoleRepositoryCustom.insertUserRoles(userId, toAdd)
                                            .map(addCount -> new BindResultDTO(addCount, removeCount))
                            );
                });
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

