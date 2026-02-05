package com.springboot.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.admin.mapper.ext.SysRoleCustomOrgExtMapper;
import com.springboot.admin.model.entity.sys.SysRoleCustomOrg;
import com.springboot.admin.service.ISysRoleCustomOrgService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色自定义组织数据权限表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-02-06
 */
@Service
public class SysRoleCustomOrgServiceImpl extends ServiceImpl<SysRoleCustomOrgExtMapper, SysRoleCustomOrg> implements ISysRoleCustomOrgService {

}
