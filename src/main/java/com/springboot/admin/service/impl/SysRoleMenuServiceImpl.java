package com.springboot.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.admin.mapper.ext.SysRoleMenuExtMapper;
import com.springboot.admin.model.entity.sys.SysRoleMenu;
import com.springboot.admin.service.ISysRoleMenuService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色与菜单关联表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-02-06
 */
@Service
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuExtMapper, SysRoleMenu> implements ISysRoleMenuService {

}
