package com.springboot.admin.service.impl;

import com.springboot.admin.model.entity.sys.SysUserRole;
import com.springboot.admin.mapper.SysUserRoleMapper;
import com.springboot.admin.service.ISysUserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户与角色关联表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-01-15
 */
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements ISysUserRoleService {

}
