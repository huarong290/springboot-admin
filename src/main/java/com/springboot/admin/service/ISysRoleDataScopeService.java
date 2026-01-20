package com.springboot.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.springboot.admin.enums.DataScopeTypeEnum;
import com.springboot.admin.model.entity.sys.SysRoleDataScope;

import java.util.List;

/**
 * <p>
 * 角色-数据权限范围关联表 服务接口
 * </p>
 *
 * 说明：
 * - 定义角色与数据权限范围的业务操作方法
 * - 继承 MyBatis-Plus 的 IService，具备通用 CRUD 能力
 * - 在此基础上扩展业务相关的查询方法
 *
 * 使用场景：
 * - 在业务逻辑中调用，获取某个角色对应的数据范围
 * - 常用于权限校验、数据隔离
 *
 * @author system
 * @since 2026-01-18
 */
public interface ISysRoleDataScopeService extends IService<SysRoleDataScope> {

    /**
     * 根据角色ID集合查询指定类型的数据范围ID
     *
     * @param roleIds   角色ID集合
     * @param scopeType 数据范围类型枚举值（如 DataScopeTypeEnum.ORG.getValue()）
     * @return 数据范围ID集合
     */
    List<Long> selectScopeIdsByRoleIds(List<Long> roleIds, long scopeType);

    /**
     * 根据角色ID集合获取最大的数据权限范围类型
     *
     * 例如：
     * - SUPER_ADMIN -> ALL
     * - 普通角色 -> ORG_AND_CHILD
     *
     * @param roleIds 角色ID集合
     * @return 最大的数据权限范围枚举
     */
    DataScopeTypeEnum getMaxScopeByRoleIds(List<Long> roleIds);
}
