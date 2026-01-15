package com.springboot.admin.service.impl;

import com.springboot.admin.model.entity.sys.SysUser;
import com.springboot.admin.mapper.SysUserMapper;
import com.springboot.admin.service.ISysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 系统用户表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-01-15
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

}
