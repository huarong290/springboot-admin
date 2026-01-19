package com.springboot.admin.mapper.ext;

import com.springboot.admin.mapper.auto.SysUserRoleMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * <p>
 * 用户-角色关联表 Mapper Ext接口
 * </p>
 *
 * @author system
 * @since 2026-01-18
 */
@Mapper
public interface SysUserRoleExtMapper extends SysUserRoleMapper {
    /**
     * 根据用户ID查询角色ID集合
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);
}


