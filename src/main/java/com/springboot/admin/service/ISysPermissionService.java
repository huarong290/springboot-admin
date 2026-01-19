package com.springboot.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.springboot.admin.model.entity.sys.SysPermission;
import com.springboot.admin.model.vo.permission.SysPermissionVO;

import java.util.List;

/**
 * <p>
 * 系统权限点表 服务类
 * </p>
 *
 * @author system
 * @since 2026-01-18
 */
public interface ISysPermissionService extends IService<SysPermission> {

    /**
     * 根据用户ID查询权限点列表
     *
     * @param userId 用户ID
     * @return 权限点VO集合
     */
    List<SysPermissionVO> listPermissionsByUserId(Long userId);
}
