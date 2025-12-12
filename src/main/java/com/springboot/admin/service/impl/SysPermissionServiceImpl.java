package com.springboot.admin.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.springboot.admin.convert.SysPermissionConvert;
import com.springboot.admin.model.dto.permission.SysPermissionDTO;
import com.springboot.admin.model.entity.sys.SysPermission;
import com.springboot.admin.model.entity.sys.SysRole;
import com.springboot.admin.model.vo.permission.SysPermissionVO;
import com.springboot.admin.repository.custom.SysPermissionRepositoryCustom;
import com.springboot.admin.repository.single.SysPermissionRepository;
import com.springboot.admin.repository.single.SysRoleRepository;
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


    private final SysPermissionRepository sysPermissionRepository;
    private final SysPermissionRepositoryCustom sysPermissionRepositoryCustom;
    private final SysPermissionConvert sysPermissionConvert;

    public SysPermissionServiceImpl(SysPermissionRepository sysPermissionRepository,
                                    SysPermissionRepositoryCustom sysPermissionRepositoryCustom,SysPermissionConvert sysPermissionConvert) {
        this.sysPermissionRepository = sysPermissionRepository;
        this.sysPermissionRepositoryCustom = sysPermissionRepositoryCustom;
        this.sysPermissionConvert = sysPermissionConvert;
    }

    /** ---------------- 单表操作 ---------------- */

    @Override
    public Mono<SysPermissionVO> getPermissionById(Long id) {
        return sysPermissionRepository.findById(id).map(sysPermissionConvert::toVO);
    }

    @Override
    public Mono<Long> addPermission(SysPermissionDTO sysPermissionDTO) {
        SysPermission sysPermission = sysPermissionConvert.toEntity(sysPermissionDTO);
        // 调用自定义仓库方法，返回主键 ID
        return sysPermissionRepositoryCustom.insertPermission(sysPermission);
    }

    @Override
    public Mono<Long> updatePermission(SysPermissionDTO sysPermissionDTO) {
        SysPermission sysPermission = sysPermissionConvert.toEntity(sysPermissionDTO);
        return sysPermissionRepositoryCustom.updatePermission(sysPermission);
    }

    @Override
    public Mono<Long> deletePermission(Long id) {
        return sysPermissionRepositoryCustom.deletePermissionById(id,false);
    }

    @Override
    public Flux<SysPermissionVO> getPermissionList() {
        return sysPermissionRepository.findAll()
                .collectList() // 收集成 List<SysPermission>
                .doOnNext(list -> log.info("查询到权限列表: {}", JSONObject.toJSONString(list)))
                .flatMapMany(list -> Flux.fromIterable(list)
                        .map(sysPermissionConvert::toVO));
    }


    @Override
    public Mono<Boolean> existsByPermissionCode(String permissionCode) {
        return sysPermissionRepository.existsByPermissionCode(permissionCode);
    }



    /** ---------------- 多表操作 ---------------- */

    @Override
    public Flux<SysPermissionVO> listPermissionsByUserId(Long userId) {
        return sysPermissionRepositoryCustom.listPermissionsByUserId(userId).collectList().doOnNext(list -> log.info("查询到权限列表: {}", JSONObject.toJSONString(list)))
                .flatMapMany(list -> Flux.fromIterable(list)
                        .map(sysPermissionConvert::toVO));
    }

    @Override
    public Flux<SysPermissionVO> listPermissionsByRoleId(Long roleId) {
        return sysPermissionRepositoryCustom.listPermissionsByRoleId(roleId).collectList().doOnNext(list -> log.info("查询到权限列表: {}", JSONObject.toJSONString(list)))
                .flatMapMany(list -> Flux.fromIterable(list)
                        .map(sysPermissionConvert::toVO));
    }
}
