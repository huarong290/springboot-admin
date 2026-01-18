package com.springboot.admin.service.impl;

import com.springboot.admin.model.entity.sys.SysPermission;
import com.springboot.admin.mapper.auto.SysPermissionMapper;
import com.springboot.admin.service.ISysPermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 系统权限点表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-01-18
 */
@Service
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements ISysPermissionService {

}
