package com.springboot.admin.mapper.ext;

import com.springboot.admin.mapper.auto.SysRoleCustomDeptMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 角色自定义部门数据权限表 Mapper Ext接口
 * </p>
 *
 * @author system
 * @since 2026-01-18
 */
@Mapper
public interface SysRoleCustomDeptExtMapper extends SysRoleCustomDeptMapper {
    List<Long> selectDeptIdsByUserId(Long userId);
}
