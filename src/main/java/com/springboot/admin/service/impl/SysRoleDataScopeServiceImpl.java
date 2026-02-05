package com.springboot.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.admin.mapper.ext.SysRoleDataScopeExtMapper;
import com.springboot.admin.model.entity.sys.SysRoleDataScope;
import com.springboot.admin.service.ISysRoleDataScopeService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色-数据权限范围关联表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-02-06
 */
@Service
public class SysRoleDataScopeServiceImpl extends ServiceImpl<SysRoleDataScopeExtMapper, SysRoleDataScope> implements ISysRoleDataScopeService {

}
