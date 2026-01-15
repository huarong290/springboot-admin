package com.springboot.admin.service.impl;

import com.springboot.admin.model.entity.sys.SysRolePermission;
import com.springboot.admin.mapper.SysRolePermissionMapper;
import com.springboot.admin.service.ISysRolePermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色与权限点关联表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-01-15
 */
@Service
public class SysRolePermissionServiceImpl extends ServiceImpl<SysRolePermissionMapper, SysRolePermission> implements ISysRolePermissionService {

}
