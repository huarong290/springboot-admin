package com.springboot.admin.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.springboot.admin.common.ApiResultCode;
import com.springboot.admin.convert.SysPermissionConvert;
import com.springboot.admin.exception.BusinessException;
import com.springboot.admin.model.dto.permission.SysPermissionDTO;
import com.springboot.admin.model.dto.permission.SysPermissionQueryDTO;
import com.springboot.admin.model.vo.PageResult;
import com.springboot.admin.model.vo.permission.SysPermissionVO;
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
    public Mono<PageResult<SysPermissionVO>> pagePermissionList(SysPermissionQueryDTO query) {
        return sysPermissionRepositoryCustom.pagePermissionList(query);
    }
    @Override
    public Mono<SysPermissionVO> getPermissionById(Long id) {
        return sysPermissionRepository.findById(id).map(sysPermissionConvert::toVO);
    }

    @Override
    public Mono<Long> addPermission(SysPermissionDTO permissionDTO) {

        // 先检查 permissionCode 是否已存在
        return sysPermissionRepository.existsByPermissionCode(permissionDTO.getPermissionCode())
                .hasElement()
                .flatMap(exists -> {
                    if (exists) {
                        // 如果已存在，抛出业务异常
                        return Mono.error(new BusinessException(
                                ApiResultCode.CONFLICT.getCode(),
                                "权限编码已存在：" + permissionDTO.getPermissionCode()
                        ));
                    }
                    // 如果不存在，执行插入
                    return sysPermissionRepositoryCustom.insertPermission(permissionDTO);
                });
    }


    @Override
    public Mono<Long> updatePermission(SysPermissionDTO sysPermissionDTO) {

        return sysPermissionRepositoryCustom.updatePermission(sysPermissionDTO);
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
