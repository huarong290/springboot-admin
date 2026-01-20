package com.springboot.admin.mapper.ext;

import com.springboot.admin.mapper.auto.SysOrgClosureMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 组织层级闭包表（替代 parent_ids LIKE） Mapper Ext接口
 * </p>
 *
 * @author system
 * @since 2026-01-18
 */
@Mapper
public interface SysOrgClosureExtMapper extends SysOrgClosureMapper {

    List<Long> selectDescendantIds(Long orgId);
}
