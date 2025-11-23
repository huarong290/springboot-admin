package com.springboot.admin.model.entity.sys;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * 组织表 (R2DBC 响应式版本)
 */
@Data
@Table("sys_org")
public class SysOrg {

    @Id
    private Long id;

    /**
     * 上级组织ID，顶级组织为0
     */
    private Long parentId;

    /**
     * 组织名称
     */
    private String orgName;

    /**
     * 组织编码，唯一
     */
    private String orgCode;

    /**
     * 组织类型（集团/公司/事业部等）
     */
    private String orgType;

    /**
     * 排序值
     */
    private Integer orgSort;

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
