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
 * 用于在用户登录成功后，返回用户的基本信息、角色、权限、菜单等数据
 */
@Data
public class UserInfoDTO {

    /**
     * 用户ID
     * 对应 sys_user 表的主键 id
     */
    private Long userId;

    /**
     * 用户名
     * 对应 sys_user.username
     */
    private String username;

    /**
     * 用户昵称
     * 对应 sys_user.nickname
     */
    private String nickname;

    /**
     * 用户头像 URL
     * 对应 sys_user.avatar
     */
    private String avatar;

    /**
     * 用户角色列表（完整对象，包含编码、名称、描述等）
     * */
    private List<SysRoleVO> roles = new ArrayList<>();

    /** 用户权限列表（完整对象，包含编码、名称、类型等） */
    private List<SysPermissionVO> permissions = new ArrayList<>();
    /**
     * 用户菜单列表（树形结构）
     * 从 sys_menu 表中查询，返回树形结构
     * 示例：系统管理 -> 用户管理、角色管理
     */
    @Schema(description = "用户菜单列表", implementation = SysMenuTreeVO.class)
    private List<SysMenuTreeVO> menus = new ArrayList<>();
}
