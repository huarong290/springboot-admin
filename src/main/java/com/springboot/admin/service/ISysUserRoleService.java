package com.springboot.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.springboot.admin.model.entity.sys.SysUserRole;

import java.util.List;

/**
 * <p>
 * 用户-角色关联表 服务类
 * </p>
 *
 * @author system
 * @since 2026-01-18
 */
public interface ISysUserRoleService extends IService<SysUserRole> {

    /**
     * 根据用户ID查询关联的角色ID集合
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    List<Long> listRoleIdsByUserId(Long userId);
}

