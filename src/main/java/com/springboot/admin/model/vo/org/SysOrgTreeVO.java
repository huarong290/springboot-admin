package com.springboot.admin.model.vo.org;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 组织树形结构 VO
 * 用于前端展示组织层级关系（树形下拉、树形表格等）
 */
@Data
public class SysOrgTreeVO {

    /**
     * 组织主键 ID
     */
    private Long id;

    /**
     * 组织编码（唯一标识）
     */
    private String orgCode;

    /**
     * 组织名称
     */
    private String orgName;

    /**
     * 组织状态（1：启用，0：禁用）
     */
    private Integer orgStatus;

    /**
     * 父组织 ID（顶级组织为 0）
     */
    private Long parentId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 子组织集合（树形结构）
     */
    private List<SysOrgTreeVO> children;
}
