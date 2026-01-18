package com.springboot.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.admin.mapper.ext.SysUserExtMapper;
import com.springboot.admin.model.dto.user.SysUserDTO;
import com.springboot.admin.model.entity.sys.SysUser;
import com.springboot.admin.service.ISysUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;




/**
 * <p>
 * 系统用户表 服务实现类
 * </p>
 * 提供用户相关业务方法，例如根据用户名获取用户信息
 * 返回 DTO 用于业务逻辑处理，Controller 层可再转换为 VO
 *
 * @author system
 * @since 2026-01-18
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserExtMapper, SysUser> implements ISysUserService {

    /**
     * 根据用户名查询用户信息
     * <p>
     * 1. 使用 MyBatis-Plus LambdaQueryWrapper 进行条件查询
     * 2. 返回 DTO，包含业务处理所需完整字段
     * 3. 如果用户不存在，返回 null
     *
     * @param username 用户名
     * @return SysUserDTO 用户信息 DTO，如果不存在返回 null
     */
    @Override
    public SysUserDTO getUserByUsername(String username) {
        // 防御性编程：用户名为空直接返回 null
        if (StringUtils.isBlank(username)) {
            return null;
        }

        // 构建查询条件：username 等于传入值，且 delete_flag = 0（未删除）
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username)
                .eq(SysUser::getDeleteFlag, 0);

        // 查询数据库
        SysUser sysUser = this.getOne(queryWrapper);
        if (sysUser == null) {
            return null;
        }

        // 构建 DTO 并拷贝属性
        SysUserDTO dto = new SysUserDTO();
        dto.setId(sysUser.getId());
        dto.setUsername(sysUser.getUsername());
        dto.setPassword(sysUser.getPassword()); // 注意：密码敏感，返回 DTO 供业务处理，不要直接暴露给前端
        dto.setNickname(sysUser.getNickname());
        dto.setEmail(sysUser.getEmail());
        dto.setPhone(sysUser.getPhone());
        dto.setDeptId(sysUser.getDeptId());
        dto.setOrgId(sysUser.getOrgId());
        dto.setUserStatus(sysUser.getUserStatus());
        dto.setAvatar(sysUser.getAvatar());
        dto.setLastLoginTime(sysUser.getLastLoginTime());

        return dto;
    }
}

