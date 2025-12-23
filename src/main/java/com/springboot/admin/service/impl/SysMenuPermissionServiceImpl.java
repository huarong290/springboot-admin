package com.springboot.admin.service.impl;

import com.springboot.admin.model.dto.BindResultDTO;
import com.springboot.admin.repository.custom.SysMenuPermissionRepositoryCustom;
import com.springboot.admin.repository.single.SysMenuPermissionRepository;
import com.springboot.admin.service.ISysMenuPermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 菜单权限表 Service 实现类
 *
 * 封装菜单与权限绑定的业务逻辑：
 * 1. 单表操作：增删改查、查询、判断存在 —— 使用 Repository（Spring Data R2DBC 自动生成）
 * 2. 多表/复杂操作：例如批量插入、增量更新 —— 使用 DatabaseClient 封装在 RepositoryCustom
 *
 * 企业级 MySQL 风格：
 * - 简单 CRUD 用 Repository
 * - 复杂 SQL 用 RepositoryCustom
 */
@Service
@Slf4j
public class SysMenuPermissionServiceImpl implements ISysMenuPermissionService {

    /** Spring Data R2DBC 自动生成的 Repository，用于单表简单操作 */
    private final SysMenuPermissionRepository sysMenuPermissionRepository;

    /** 自定义 Repository，用于复杂 SQL 操作（批量插入、增量更新等） */
    private final SysMenuPermissionRepositoryCustom sysMenuPermissionRepositoryCustom;

    /**
     * 构造函数注入依赖
     *
     * @param sysMenuPermissionRepository 单表操作 Repository
     * @param sysMenuPermissionRepositoryCustom 自定义复杂操作 Repository
     */
    public SysMenuPermissionServiceImpl(SysMenuPermissionRepository sysMenuPermissionRepository,
                                        SysMenuPermissionRepositoryCustom sysMenuPermissionRepositoryCustom) {
        this.sysMenuPermissionRepository = sysMenuPermissionRepository;
        this.sysMenuPermissionRepositoryCustom = sysMenuPermissionRepositoryCustom;
    }

    /**
     * 增量绑定菜单权限
     *
     * 核心逻辑：
     * 1. 查询该菜单当前已有的权限集合
     * 2. 计算需要新增的权限（前端传入但数据库没有）
     * 3. 计算需要删除的权限（数据库有但前端没传入）
     * 4. 执行删除和新增操作
     * 5. 返回 BindResultDTO，包含新增和删除的数量
     *
     * @param menuId 菜单ID
     * @param permissionIds 前端传入的最新权限ID集合
     * @return Mono<BindResultDTO> 响应式单对象，包含新增和删除的数量
     */
    @Override
    public Mono<BindResultDTO> bindMenuPermissions(Long menuId, List<Long> permissionIds) {
        // 第一步：查询该菜单当前已有的权限集合
        return sysMenuPermissionRepositoryCustom.findPermissionIdsByMenuId(menuId)
                .collectList() // 收集为 List<Long>
                .flatMap(existing -> {
                    // 第二步：计算需要新增的权限（前端传入但数据库没有）
                    List<Long> toAdd = permissionIds.stream()
                            .filter(p -> !existing.contains(p))
                            .toList();
                    log.info("Menu {} toAdd: {}", menuId, toAdd);

                    // 第三步：计算需要删除的权限（数据库有但前端没传入）
                    List<Long> toRemove = existing.stream()
                            .filter(p -> !permissionIds.contains(p))
                            .toList();
                    log.info("Menu {} toRemove: {}", menuId, toRemove);

                    // 第四步：执行删除和新增操作
                    return sysMenuPermissionRepositoryCustom.deleteByMenuIdAndPermissionIds(menuId, toRemove)
                            .flatMap(removeCount ->
                                    sysMenuPermissionRepositoryCustom.insertMenuPermissions(menuId, toAdd)
                                            // 第五步：返回 BindResultDTO，包含新增和删除的数量
                                            .map(addCount -> new BindResultDTO(addCount, removeCount))
                            );
                });
    }
}
