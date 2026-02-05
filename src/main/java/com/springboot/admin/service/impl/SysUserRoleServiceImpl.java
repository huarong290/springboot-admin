package com.springboot.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.admin.mapper.ext.SysUserRoleExtMapper;
import com.springboot.admin.model.entity.sys.SysUserRole;
import com.springboot.admin.service.ISysUserRoleService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户-角色关联表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-02-06
 */
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleExtMapper, SysUserRole> implements ISysUserRoleService {

}
