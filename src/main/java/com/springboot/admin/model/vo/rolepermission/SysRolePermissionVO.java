package com.springboot.admin.model.vo.rolepermission;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色权限关联 VO
 *
 * 用途：
 * - 返回给前端的角色与权限点关联数据
 * - 包含角色名称和权限名称，便于展示
 */
@Data
public class SysRolePermissionVO {
    /** 主键ID */
    private Long id;

    /** 角色ID */
    private Long roleId;

    /** 角色名称（冗余字段，便于前端展示） */
    private String roleName;

    /** 权限点ID */
    private Long permissionId;

    /** 权限点名称（冗余字段，便于前端展示） */
    private String permissionName;

    /** 删除标记：0=未删除，1=已删除 */
    private Integer deleteFlag;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 修改时间 */
    private LocalDateTime updateTime;
}
