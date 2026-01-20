package com.springboot.admin.mapper.ext;

import com.springboot.admin.mapper.auto.SysPermissionMapper;
import com.springboot.admin.model.entity.sys.SysPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 系统权限点表 Mapper Ext接口
 * </p>
 *
 * @author system
 * @since 2026-01-18
 */
@Mapper
public interface SysPermissionExtMapper extends SysPermissionMapper {

    /**
     * 根据用户ID查询权限点列表
     *
     * SQL逻辑：
     * 1. 从 sys_user_role 表获取用户的角色
     * 2. 通过 sys_role_permission 表找到角色对应的权限
     * 3. 关联 sys_permission 表，过滤出有效权限
     *
     * @param userId 用户ID
     * @return 权限点实体集合
     */
    List<SysPermission> selectPermissionsByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID集合查询权限点code列表
     *
     * SQL逻辑：
     * 1. 从 sys_role_permission 表获取角色对应的权限
     * 2. 关联 sys_permission 表，过滤出有效权限
     * 3. 返回权限编码（permission_code）
     *
     * @param roleIds 角色ID集合
     * @return 权限点code集合
     */
    List<String> selectPermissionCodesByRoleIds(@Param("roleIds") List<Long> roleIds);
}
