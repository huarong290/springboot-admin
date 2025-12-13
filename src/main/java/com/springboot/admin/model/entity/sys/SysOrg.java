package com.springboot.admin.model.entity.sys;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * 组织表 (R2DBC 响应式版本)
 */
@Data
@Table("sys_org")
public class SysOrg {

    @Id
    @Column("id")
    private Long id;

    /**
     * 上级组织ID，顶级组织为0
     */
    @Column("parent_id")
    private Long parentId;

    /**
     * 组织名称
     */
    @Column("org_name")
    private String orgName;

    /**
     * 组织编码，唯一
     */
    @Column("org_code")
    private String orgCode;

    /**
     * 组织类型（集团/公司/事业部等）
     */
    @Column("org_type")
    private String orgType;

    /**
     * 排序值
     */
    @Column("org_sort")
    private Integer orgSort;

    /**
     * 是否启用
     * <p>
     * 控制是否可用。
     * 1 = 启用，0 = 禁用。
     * 示例：1（启用）、0（禁用）
     */
    @Column("org_status")
    private Integer orgStatus;

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
