package com.springboot.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.admin.mapper.ext.SysMenuClosureExtMapper;
import com.springboot.admin.model.entity.sys.SysMenuClosure;
import com.springboot.admin.service.ISysMenuClosureService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 菜单层级闭包表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-02-06
 */
@Service
public class SysMenuClosureServiceImpl extends ServiceImpl<SysMenuClosureExtMapper, SysMenuClosure> implements ISysMenuClosureService {

}
