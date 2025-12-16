package com.springboot.admin.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.springboot.admin.convert.SysRoleConvert;
import com.springboot.admin.model.dto.role.SysRoleDTO;
import com.springboot.admin.model.dto.role.SysRoleQueryDTO;
import com.springboot.admin.model.entity.sys.SysRole;
import com.springboot.admin.model.vo.PageResult;
import com.springboot.admin.model.vo.role.SysRoleVO;
import com.springboot.admin.repository.custom.SysRoleRepositoryCustom;
import com.springboot.admin.repository.single.SysRoleRepository;
import com.springboot.admin.service.ISysRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

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
    private final SysRoleConvert sysRoleConvert;


    public SysRoleServiceImpl(SysRoleRepository roleRepository,
                              SysRoleRepositoryCustom roleRepositoryCustom,
                              SysRoleConvert sysRoleConvert) {
        this.roleRepository = roleRepository;
        this.roleRepositoryCustom = roleRepositoryCustom;
        this.sysRoleConvert = sysRoleConvert;
    }


    @Override
    public Mono<PageResult<SysRoleVO>> pageRoleList(SysRoleQueryDTO query) {
        return roleRepositoryCustom.pageRoleList(query);
    }

    /** ---------------- 单表操作 ---------------- */

    @Override
    public Mono<SysRoleVO> getRoleById(Long id) {
        return roleRepository.findById(id).map(sysRoleConvert::toVO);
    }

    @Override
    public Mono<SysRoleVO> getRoleByCode(String roleCode) {
        return roleRepository.findByRoleCode(roleCode).map(sysRoleConvert::toVO);
    }

    @Override
    public Mono<Long> addRole(SysRoleDTO roleDTO) {
        SysRole sysRole =sysRoleConvert.toEntity(roleDTO);
        // 调用自定义仓库方法，返回主键 ID
        return roleRepositoryCustom.insertRole(sysRole);
    }

    @Override
    public Mono<Long> updateRole(SysRoleDTO roleDTO) {
        // 调用自定义仓库方法，返回更新的记录数
        return roleRepositoryCustom.updateRole(roleDTO);
    }

    @Override
    public Mono<Long> deleteRole(Long id) {
        // 调用自定义仓库方法，返回删除的记录数
        return roleRepositoryCustom.deleteRoleById(id,false);
    }

    /**
     * 批量逻辑删除角色
     *
     * 用途：
     * - 后台管理：一次性删除多个角色，但保留数据以便审计
     *
     * @param ids 角色ID集合
     * @return Mono<Long> 删除成功的记录数
     */
    @Override
    public Mono<Long> deleteRolesByIds(List<Long> ids) {
        return roleRepositoryCustom.deleteRolesByIds(ids,true)
                .doOnSuccess(rows -> log.info("逻辑删除角色成功，数量={}", rows))
                .doOnError(e -> log.error("逻辑删除角色失败: {}", e.getMessage(), e));
    }

    /**
     * 批量物理删除角色
     *
     * 用途：
     * - 特殊场景：需要彻底清除角色数据（例如测试数据清理）
     *
     * @param ids 角色ID集合
     * @return Mono<Long> 删除成功的记录数
     */
    @Override
    public Mono<Long> deleteRolesPhysicallyByIds(List<Long> ids) {
        return roleRepositoryCustom.deleteRolesByIds(ids,false)
                .doOnSuccess(rows -> log.info("物理删除角色成功，数量={}", rows))
                .doOnError(e -> log.error("物理删除角色失败: {}", e.getMessage(), e));
    }

    @Override
    public Flux<SysRoleVO> getRoleList() {
        return roleRepository.findAll()
                .doOnNext(role -> log.info("查询到角色: {}", JSONObject.toJSONString(role)))
                .map(role -> {
                    SysRoleVO vo = sysRoleConvert.toVO(role);
                    log.info("转换后的VO: {}", vo);
                    return vo;
                });
    }


    @Override
    public Mono<Boolean> existsByRoleCode(String roleCode) {
        return roleRepository.existsByRoleCode(roleCode);
    }



    /** ---------------- 多表关联 ---------------- */
    /**
     * 根据用户ID查询角色列表
     *
     * @param userId 用户ID
     * @return Flux<SysRoleVO> 响应式流，返回该用户拥有的角色集合
     */
    @Override
    public Flux<SysRoleVO> listRolesByUserId(Long userId) {
        return roleRepositoryCustom.findRolesByUserId(userId)
                .map(sysRoleConvert::toVO); // Entity → VO
    }

    /**
     * 根据权限ID查询角色列表
     *
     * @param permissionId 权限ID
     * @return Flux<SysRoleVO> 响应式流，返回拥有该权限的角色集合
     */
    @Override
    public Flux<SysRoleVO> findRolesByPermissionId(Long permissionId) {
        return roleRepositoryCustom.findRolesByPermissionId(permissionId)
                .map(sysRoleConvert::toVO); // Entity → VO
    }

    /**
     * 根据菜单D查询角色列表
     *
     * @param menuId 菜单D
     * @return Flux<SysRole> 响应式流，返回该菜单拥有的角色集合
     */
    @Override
    public Flux<SysRoleVO> listRolesByMenuId (Long menuId) {
        return roleRepositoryCustom.findRolesByMenuId(menuId).map(sysRoleConvert::toVO);
    }
}
