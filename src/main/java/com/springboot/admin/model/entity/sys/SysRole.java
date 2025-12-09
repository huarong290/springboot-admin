package com.springboot.admin.model.entity.sys;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * 系统角色表 (R2DBC 响应式版本)
 */
@Data
@Table("sys_role")
public class SysRole {

    @Id
    @Column("id")
    private Long id;

    /**
     * 角色名称
     */
    @Column("role_name")
    private String roleName;

    /**
     * 角色编码
     */
    @Column("role_code")
    private String roleCode;

    /**
     * 角色描述
     */
    @Column("role_description")
    private String roleDescription;
    /**
     * 是否启用
     * 1 = 启用，0 = 禁用
     */
    @Column("role_status")
    private Integer roleStatus;

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
