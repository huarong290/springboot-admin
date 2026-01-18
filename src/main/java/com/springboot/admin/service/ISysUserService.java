package com.springboot.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.springboot.admin.model.dto.user.SysUserDTO;
import com.springboot.admin.model.entity.sys.SysUser;

/**
 * <p>
 * 系统用户表 服务类
 * </p>
 *
 * @author system
 * @since 2026-01-18
 */
public interface ISysUserService extends IService<SysUser> {

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return SysUserDTO 响应式单对象
     */
    SysUserDTO getUserByUsername(String username);

}
