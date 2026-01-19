package com.springboot.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.admin.convert.SysUserConvert;
import com.springboot.admin.mapper.ext.SysUserExtMapper;
import com.springboot.admin.model.dto.user.SysUserDTO;
import com.springboot.admin.model.entity.sys.SysUser;
import com.springboot.admin.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
@Slf4j
public class SysUserServiceImpl extends ServiceImpl<SysUserExtMapper, SysUser> implements ISysUserService {
    @Autowired
    private SysUserExtMapper sysUserExtMapper;
    @Autowired
    private  SysUserConvert sysUserConvert;


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
        // 1️⃣ 防御性校验
        if (StringUtils.isBlank(username)) {
            return null;
        }

        // 2️⃣ 构建查询条件
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username)
                .eq(SysUser::getDeleteFlag, 0);

        // 3️⃣ 查询实体
        SysUser sysUser = this.getOne(queryWrapper);
        if (sysUser == null) {
            return null;
        }

        // 4️⃣ Entity → DTO（统一交给 Convert）
        return sysUserConvert.entityToDTO(sysUser);
    }

    @Override
    public SysUserDTO getSysUserDtoByUserId(long useId) {
        // 1️⃣  查询实体
        SysUser sysUser = this.getById(useId);
        // 2️⃣ Entity → DTO（统一交给 Convert）
        return sysUserConvert.entityToDTO(sysUser);
    }
}

