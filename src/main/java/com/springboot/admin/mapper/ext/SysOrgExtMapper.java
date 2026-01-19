package com.springboot.admin.mapper.ext;

import com.springboot.admin.mapper.auto.SysOrgMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 组织表（高并发版，仅存自身信息） Mapper Ext接口
 * </p>
 *
 * @author system
 * @since 2026-01-18
 */
@Mapper
public interface SysOrgExtMapper extends SysOrgMapper  {
}
