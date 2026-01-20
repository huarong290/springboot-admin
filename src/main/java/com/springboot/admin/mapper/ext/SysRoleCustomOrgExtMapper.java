package com.springboot.admin.mapper.ext;

import com.springboot.admin.mapper.auto.SysRoleCustomOrgMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 角色自定义组织数据权限表 Mapper 接口
 * </p>
 *
 * @author system
 * @since 2026-01-18
 */
@Mapper
public interface SysRoleCustomOrgExtMapper extends SysRoleCustomOrgMapper {

    List<Long> selectOrgIdsByUserId(Long userId);
}
