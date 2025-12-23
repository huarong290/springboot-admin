package com.springboot.admin.model.entity.sys;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * 菜单-权限关联表实体 (R2DBC 响应式版本)
 * 用于建立菜单和权限点之间的映射关系
 */
@Data
@Table("sys_menu_permission")
public class SysMenuPermission {

    @Id
    @Column("id")
    private Long id;

    /**
     * 菜单ID（对应 sys_menu 表的主键）
     */
    @Column("menu_id")
    private Long menuId;

    /**
     * 权限ID（对应 sys_permission 表的主键）
     */
    @Column("permission_id")
    private Long permissionId;

    /**
     * 是否删除：0=未删除，1=已删除
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

