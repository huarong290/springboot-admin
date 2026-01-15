package com.springboot.admin.service.impl;

import com.springboot.admin.model.entity.sys.SysMenuPermission;
import com.springboot.admin.mapper.SysMenuPermissionMapper;
import com.springboot.admin.service.ISysMenuPermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 菜单与权限关联表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-01-15
 */
@Service
public class SysMenuPermissionServiceImpl extends ServiceImpl<SysMenuPermissionMapper, SysMenuPermission> implements ISysMenuPermissionService {

}
