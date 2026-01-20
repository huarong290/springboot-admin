package com.springboot.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.springboot.admin.model.dto.user.SysUserDTO;
import com.springboot.admin.model.entity.sys.SysUser;

/**
 * <p>
 * 系统用户表 服务类
 * </p>
 *
 * 说明：
 * - 提供用户信息查询、用户所属组织/部门等业务方法
 * - 继承 MyBatis-Plus 的 IService，具备通用 CRUD 功能
 *
 * 使用场景：
 * - 登录、获取用户详情
 * - 数据权限计算中获取用户所属组织或部门
 *
 * @author system
 * @since 2026-01-18
 */
public interface ISysUserService extends IService<SysUser> {

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 用户 DTO 对象，封装基本信息和角色/权限信息
     */
    SysUserDTO getUserByUsername(String username);

    /**
     * 根据用户ID获取用户 DTO
     *
     * @param userId 用户ID
     * @return SysUserDTO
     */
    SysUserDTO getSysUserDtoByUserId(long userId);

    /**
     * 获取用户所属组织ID
     *
     * @param userId 用户ID
     * @return 组织ID
     */
    Long getUserOrgId(Long userId);

    /**
     * 获取用户所属部门ID
     *
     * @param userId 用户ID
     * @return 部门ID
     */
    Long getUserDeptId(Long userId);
}
