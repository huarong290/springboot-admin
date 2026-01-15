package com.springboot.admin.service.impl;

import com.springboot.admin.model.entity.sys.SysMenu;
import com.springboot.admin.mapper.SysMenuMapper;
import com.springboot.admin.service.ISysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 系统菜单表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-01-15
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements ISysMenuService {

}
