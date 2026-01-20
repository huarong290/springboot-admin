package com.springboot.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.admin.enums.DataScopeTypeEnum;
import com.springboot.admin.mapper.ext.SysRoleDataScopeExtMapper;
import com.springboot.admin.model.entity.sys.SysRoleDataScope;
import com.springboot.admin.service.ISysRoleDataScopeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 角色-数据权限范围关联表 服务实现类
 * </p>
 *
 * 说明：
 * - 继承 MyBatis-Plus 的 ServiceImpl，自动具备通用 CRUD 能力
 * - 注入扩展 Mapper（SysRoleDataScopeExtMapper），用于执行复杂查询
 * - 实现 ISysRoleDataScopeService 接口中定义的业务方法
 *
 * 使用场景：
 * - 在业务逻辑中调用 Service 方法，获取角色对应的数据范围
 * - 常用于用户登录后加载数据权限，或在查询时做数据隔离
 *
 * @author system
 * @since 2026-01-18
 */
@Service
public class SysRoleDataScopeServiceImpl extends ServiceImpl<SysRoleDataScopeExtMapper, SysRoleDataScope> implements ISysRoleDataScopeService {

    /**
     * 注入扩展 Mapper
     * - SysRoleDataScopeExtMapper 包含自定义 SQL 查询
     * - 通过它来执行角色与数据范围的关联查询
     */
    @Autowired
    private SysRoleDataScopeExtMapper sysRoleDataScopeExtMapper;

    /**
     * 根据角色ID集合查询指定类型的数据范围ID
     *
     * 实现逻辑：
     * 1. 如果 roleIds 为空，直接返回空集合，避免 SQL 报错
     * 2. 调用 Mapper 层方法执行数据库查询
     *
     * @param roleIds   角色ID集合
     * @param scopeType 数据范围类型枚举值（如 DataScopeTypeEnum.ORG.getValue()）
     * @return 数据范围ID集合
     */
    @Override
    public List<Long> selectScopeIdsByRoleIds(List<Long> roleIds, long scopeType) {
        if (roleIds == null || roleIds.isEmpty()) {
            return Collections.emptyList();
        }
        return sysRoleDataScopeExtMapper.selectScopeIdsByRoleIds(roleIds, scopeType);
    }

    @Override
    public DataScopeTypeEnum getMaxScopeByRoleIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return DataScopeTypeEnum.DEPT; // 默认最小权限
        }

        // 查询角色对应的数据权限范围（从数据库获取 SysRoleDataScope 列表）
        List<SysRoleDataScope> roleScopes = list(
                new LambdaQueryWrapper<SysRoleDataScope>()
                        .in(SysRoleDataScope::getRoleId, roleIds)
                        .eq(SysRoleDataScope::getDeleteFlag, 0)
        );


        if (roleScopes.isEmpty()) {
            return DataScopeTypeEnum.DEPT; // 默认最小权限
        }

        // 取最大范围
        long maxValue = roleScopes.stream()
                .mapToLong(SysRoleDataScope::getScopeId) // scopeId 对应 DataScopeTypeEnum.value
                .max()
                .orElse(DataScopeTypeEnum.DEPT.getValue());

        return DataScopeTypeEnum.ofValue(maxValue);
    }

}
