package com.springboot.admin.model.entity.sys;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * 系统权限点表 (R2DBC 响应式版本)
 */
@Data
@Table("sys_permission")
public class SysPermission {

    @Id
    @Column("id")
    private Long id;

    /**
     * 权限编码
     */
    @Column("permission_code")
    private String permissionCode;

    /**
     * 权限名称
     */
    @Column("permission_name")
    private String permissionName;

    /**
     * 权限类型：1=接口权限，2=数据权限
     */
    @Column("permission_type")
    private Integer permissionType;
    /**
     * 是否启用
     * <p>
     * 控制权限是否可用。
     * 1 = 启用，0 = 禁用。
     * 示例：1（启用）、0（禁用）
     */
    @Column("status")
    private Integer status;
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
