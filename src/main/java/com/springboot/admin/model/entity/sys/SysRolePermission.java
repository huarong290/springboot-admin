package com.springboot.admin.model.entity.sys;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * 角色与权限点关联表 (R2DBC 响应式版本)
 */
@Data
@Table("sys_role_permission")
public class SysRolePermission {

    /**
     * 主键ID
     */
    @Id
    private Long id;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 权限点ID
     */
    private Long permissionId;

    /**
     * 是否删除：0=未删除，1=已删除
     */
    private Integer deleteFlag;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改者
     */
    private String updateBy;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;
}
