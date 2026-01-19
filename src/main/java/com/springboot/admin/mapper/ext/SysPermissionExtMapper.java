package com.springboot.admin.mapper.ext;

import com.springboot.admin.mapper.auto.SysPermissionMapper;
import com.springboot.admin.model.entity.sys.SysMenu;
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
     * @param userId 用户ID
     * @return 权限点集合
     */
    List<SysPermission> selectPermissionsByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID查询菜单列表
     */
    List<SysMenu> selectMenusByUserId(@Param("userId") Long userId);

}
