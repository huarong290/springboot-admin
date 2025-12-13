package com.springboot.admin.model.entity.sys;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * 部门表 (R2DBC 响应式版本)
 */
@Data
@Table("sys_dept")
public class SysDept {

    @Id
    @Column("id")
    private Long id;

    /**
     * 上级部门ID，顶级部门为0
     */
    @Column("parent_id")
    private Long parentId;

    /**
     * 所属组织ID
     */
    @Column("org_id")
    private Long orgId;

    /**
     * 部门名称
     */
    @Column("dept_name")
    private String deptName;

    /**
     * 部门编码，唯一
     */
    @Column("dept_code")
    private String deptCode;

    /**
     * 排序值
     */
    @Column("dept_sort")
    private Integer deptSort;

    /**
     * 部门负责人
     */
    @Column("leader")
    private String leader;

    /**
     * 联系电话
     */
    @Column("phone")
    private String phone;

    /**
     * 部门邮箱
     */
    @Column("email")
    private String email;

    /**
     * 状态：1=启用，0=禁用
     */
    @Column("dept_status")
    private Integer deptStatus;

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
