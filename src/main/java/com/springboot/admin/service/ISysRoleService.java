package com.springboot.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.springboot.admin.model.entity.sys.SysRole;
import com.springboot.admin.model.vo.role.SysRoleVO;

import java.util.List;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author system
 * @since 2026-01-18
 */
public interface ISysRoleService extends IService<SysRole> {

    /**
     * 根据用户ID查询角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<SysRoleVO> selectRolesByUserId(Long userId);

}

