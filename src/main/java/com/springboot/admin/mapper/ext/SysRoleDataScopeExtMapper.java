package com.springboot.admin.mapper.ext;

import com.springboot.admin.mapper.auto.SysRoleDataScopeMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 角色-数据权限范围关联表 Mapper 扩展接口
 * </p>
 *
 * 说明：
 * - 继承自动生成的 SysRoleDataScopeMapper，具备基础的 CRUD 能力
 * - 在此基础上扩展自定义的 SQL 查询方法
 * - 用于根据角色ID集合和数据范围类型，查询对应的数据范围ID集合
 *
 * 使用场景：
 * - 数据权限控制：判断某个角色可以访问哪些组织或部门的数据
 * - 常用于用户登录后，加载其角色对应的数据权限范围
 *
 * @author system
 * @since 2026-01-18
 */
@Mapper
public interface SysRoleDataScopeExtMapper extends SysRoleDataScopeMapper {

    /**
     * 根据角色ID集合查询指定类型的数据范围ID
     *
     * SQL逻辑：
     * 1. 从 sys_role_data_scope 表中找到角色与数据范围的关联关系
     * 2. 关联 sys_data_scope 表，过滤出指定类型（scope_type）的数据范围
     * 3. 返回符合条件的数据范围ID集合
     *
     * @param roleIds   角色ID集合（通常由用户的角色列表传入）
     * @param scopeType 数据范围类型枚举值（如 DataScopeTypeEnum.ORG.getValue()）
     *                  - 1: ALL（全部数据）
     *                  - 2: ORG（本组织）
     *                  - 3: ORG_AND_CHILD（本组织及下级）
     *                  - 4: DEPT_ONLY（仅本部门）
     *                  - 5: CUSTOM（自定义范围）
     * @return 数据范围ID集合（如果没有匹配结果，返回空集合）
     */
    List<Long> selectScopeIdsByRoleIds(@Param("roleIds") List<Long> roleIds,
                                       @Param("scopeType") long scopeType);

}

