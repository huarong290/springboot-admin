package com.springboot.admin.model.dto.userrole;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户角色关联 DTO
 *
 * 用途：
 * - 用于接收前端传入的用户与角色关联数据
 * - 在服务层或持久层之间传递数据
 */
@Data
public class SysUserRoleDTO {
    /** 主键ID */
    private Long id;

    /** 用户ID，对应 sys_user 表的主键 */
    private Long userId;

    /** 角色ID，对应 sys_role 表的主键 */
    private Long roleId;

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
