package com.springboot.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.admin.mapper.ext.SysRoleCustomDeptExtMapper;
import com.springboot.admin.model.entity.sys.SysRoleCustomDept;
import com.springboot.admin.service.ISysRoleCustomDeptService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色自定义部门数据权限表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-02-06
 */
@Service
public class SysRoleCustomDeptServiceImpl extends ServiceImpl<SysRoleCustomDeptExtMapper, SysRoleCustomDept> implements ISysRoleCustomDeptService {

}
