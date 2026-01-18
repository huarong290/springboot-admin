package com.springboot.admin.model.dto.user;

import com.springboot.admin.model.vo.menu.SysMenuTreeVO;
import com.springboot.admin.model.vo.permission.SysPermissionVO;
import com.springboot.admin.model.vo.role.SysRoleVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户信息 DTO
 * <p>
 * 用于在用户登录成功后返回前端的用户信息
 * 包含：用户基本信息、角色列表、权限列表、菜单树
 */
@Data
public class UserInfoDTO {

    /**
     * 用户ID
     * 对应 sys_user 表的主键 id
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 用户名
     * 对应 sys_user.username
     */
    @Schema(description = "用户名")
    private String username;

    /**
     * 用户昵称
     * 对应 sys_user.nickname
     */
    @Schema(description = "用户昵称")
    private String nickname;

    /**
     * 用户头像 URL
     * 对应 sys_user.avatar
     */
    @Schema(description = "用户头像")
    private String avatar;

    /**
     * 用户邮箱
     * 对应 sys_user.email
     */
    @Schema(description = "用户邮箱")
    private String email;

    /**
     * 用户手机号
     * 对应 sys_user.phone
     */
    @Schema(description = "用户手机号")
    private String phone;

    /**
     * 用户状态
     * 1 = 启用，0 = 禁用
     */
    @Schema(description = "是否启用:1-启用 0-禁用")
    private byte status;

    /**
     * 登录设备ID
     * 对应用户本次登录使用的设备标识，用于刷新令牌绑定
     */
    @Schema(description = "登录设备ID")
    private String loginDeviceId;

    /**
     * 客户端类型
     * WEB / APP / MINI / OTHER
     */
    @Schema(description = "客户端类型")
    private String loginClientType;

    /**
     * 登录IP
     * 用于风控、登录日志
     */
    @Schema(description = "登录IP")
    private String loginIp;

    /**
     * 租户ID
     * 多租户场景下使用，对应 sys_user.tenant_id
     */
    @Schema(description = "租户ID")
    private Long tenantId;

    /**
     * 用户角色列表（完整对象）
     * 每个角色包含编码、名称、描述等信息
     * 对应 sys_role 表，通过 sys_user_role 关联
     */
    @Schema(description = "角色列表")
    private List<SysRoleVO> roles = new ArrayList<>();

    /**
     * 用户权限列表（完整对象）
     * 每个权限包含编码、名称、类型等信息
     * 对应 sys_permission 表，通过角色或直接用户关联获取
     */
    @Schema(description = "权限列表")
    private List<SysPermissionVO> permissions = new ArrayList<>();

    /**
     * 用户菜单列表（树形结构）
     * 从 sys_menu 表中查询，构建父子关系
     * 前端可直接渲染导航菜单
     */
    @Schema(description = "用户菜单列表", implementation = SysMenuTreeVO.class)
    private List<SysMenuTreeVO> menus = new ArrayList<>();
}
