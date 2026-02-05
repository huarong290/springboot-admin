package com.springboot.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.admin.mapper.ext.SysRolePermissionExtMapper;
import com.springboot.admin.model.entity.sys.SysRolePermission;
import com.springboot.admin.service.ISysRolePermissionService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色-权限关联表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-02-06
 */
@Service
public class SysRolePermissionServiceImpl extends ServiceImpl<SysRolePermissionExtMapper, SysRolePermission> implements ISysRolePermissionService {

}
