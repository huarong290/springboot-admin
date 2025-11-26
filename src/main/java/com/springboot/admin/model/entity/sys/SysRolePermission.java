package com.springboot.admin.model.entity.sys;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * 角色与权限点关联表 (R2DBC 响应式版本)
 */
@Data
@Table("sys_role_permission")
public class SysRolePermission {

    @Id
    @Column("id")
    private Long id;

    /**
     * 角色ID
     */
    @Column("role_id")
    private Long roleId;

    /**
     * 权限点ID
     */
    @Column("permission_id")
    private Long permissionId;

    /**
     * 是否删除
     */
    @Column("delete_flag")
    private Integer deleteFlag;

    /**
     * 创建者
     */
    @Column("create_by")
    private String createBy;

    /**
     * 创建时间
     */
    @Column("create_time")
    private LocalDateTime createTime;

    /**
     * 修改者
     */
    @Column("update_by")
    private String updateBy;

    /**
     * 修改时间
     */
    @Column("update_time")
    private LocalDateTime updateTime;
}
