package com.springboot.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.springboot.admin.model.entity.sys.SysRoleCustomDept;

import java.util.List;

/**
 * <p>
 * 角色自定义部门数据权限表 服务类
 * </p>
 *
 * @author system
 * @since 2026-01-18
 */
public interface ISysRoleCustomDeptService extends IService<SysRoleCustomDept> {
    /**
     * 查询用户自定义部门ID列表
     *
     * @param userId 用户ID
     * @return 自定义部门ID列表
     */
    List<Long> selectDeptIdsByUserId(Long userId);
}
