package com.springboot.admin.model.vo.userrole;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户角色关联 VO
 *
 * 用途：
 * - 返回给前端的用户与角色关联数据
 * - 包含用户名和角色名称，便于展示
 */
@Data
public class SysUserRoleVO {
    /** 主键ID */
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 用户名（冗余字段，便于前端展示） */
    private String username;

    /** 角色ID */
    private Long roleId;

    /** 角色名称（冗余字段，便于前端展示） */
    private String roleName;

    /** 删除标记：0=未删除，1=已删除 */
    private Integer deleteFlag;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 修改时间 */
    private LocalDateTime updateTime;
}