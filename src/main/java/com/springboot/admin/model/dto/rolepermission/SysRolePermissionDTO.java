package com.springboot.admin.model.dto.rolepermission;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色权限关联 DTO
 *
 * 用途：
 * - 用于接收前端传入的角色与权限点关联数据
 * - 在服务层或持久层之间传递数据
 */
@Data
public class SysRolePermissionDTO {
    /** 主键ID */
    private Long id;

    /** 角色ID，对应 sys_role 表的主键 */
    private Long roleId;

    /** 权限点ID，对应 sys_permission 表的主键 */
    private Long permissionId;

    /** 删除标记：0=未删除，1=已删除 */
    private Integer deleteFlag;

    /** 创建者用户名 */
    private String createBy;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 修改者用户名 */
    private String updateBy;

    /** 修改时间 */
    private LocalDateTime updateTime;
}
