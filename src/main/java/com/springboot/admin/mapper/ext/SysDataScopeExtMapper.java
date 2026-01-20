package com.springboot.admin.mapper.ext;

import com.springboot.admin.mapper.auto.SysDataScopeMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 数据权限范围定义表 Mapper Ext接口
 * </p>
 *
 * @author system
 * @since 2026-01-18
 */
@Mapper
public interface SysDataScopeExtMapper extends SysDataScopeMapper {

    /**
     * 根据角色ID集合查询数据权限范围编码
     * @param roleIds 角色ID集合
     * @return 数据权限范围编码集合
     */
    List<String> selectScopeCodesByRoleIds(@Param("roleIds") List<Long> roleIds);




}
