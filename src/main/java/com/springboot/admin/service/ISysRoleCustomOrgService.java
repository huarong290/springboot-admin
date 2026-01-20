package com.springboot.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.springboot.admin.model.entity.sys.SysRoleCustomOrg;

import java.util.List;

/**
 * <p>
 * 角色自定义组织数据权限表 服务类
 * </p>
 * 说明：
 * - 用于 CUSTOM 类型的数据权限
 * - 提供根据用户ID获取自定义组织或部门ID列表
 * @author system
 * @since 2026-01-18
 */
public interface ISysRoleCustomOrgService extends IService<SysRoleCustomOrg> {
    /**
     * 查询用户自定义组织ID列表
     *
     * @param userId 用户ID
     * @return 自定义组织ID列表
     */
    List<Long> selectOrgIdsByUserId(Long userId);
}
