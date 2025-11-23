package com.springboot.admin.model.entity.sys;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * 部门表 (R2DBC 响应式版本)
 */
@Data
@Table("sys_dept")
public class SysDept {

    @Id
    private Long id;

    /**
     * 上级部门ID，顶级部门为0
     */
    private Long parentId;

    /**
     * 所属组织ID
     */
    private Long orgId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 部门编码，唯一
     */
    private String deptCode;

    /**
     * 排序值
     */
    private Integer deptSort;

    /**
     * 部门负责人
     */
    private String leader;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 部门邮箱
     */
    private String email;

    /**
     * 状态：1=启用，0=禁用
     */
    private Integer status;

    /**
     * 是否删除
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
