package com.springboot.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.admin.mapper.ext.SysPermissionExtMapper;
import com.springboot.admin.model.entity.sys.SysPermission;
import com.springboot.admin.service.ISysPermissionService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 系统权限点表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-02-06
 */
@Service
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionExtMapper, SysPermission> implements ISysPermissionService {

}
