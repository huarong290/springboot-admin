package com.springboot.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.admin.mapper.ext.SysMenuExtMapper;
import com.springboot.admin.model.entity.sys.SysMenu;
import com.springboot.admin.service.ISysMenuService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 系统菜单表，存储前端菜单和权限路由 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-02-06
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuExtMapper, SysMenu> implements ISysMenuService {

}
