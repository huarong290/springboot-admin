package com.springboot.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.admin.mapper.ext.SysMenuPermissionExtMapper;
import com.springboot.admin.model.entity.sys.SysMenuPermission;
import com.springboot.admin.service.ISysMenuPermissionService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 菜单与权限关联表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-02-06
 */
@Service
public class SysMenuPermissionServiceImpl extends ServiceImpl<SysMenuPermissionExtMapper, SysMenuPermission> implements ISysMenuPermissionService {

}
