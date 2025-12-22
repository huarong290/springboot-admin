package com.springboot.admin.model.vo.dept;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 部门树形结构 VO
 * 用于前端展示部门层级关系（树形下拉、树形表格等）
 */
@Data
public class SysDeptTreeVO {

    /**
     * 部门主键 ID
     */
    private Long id;

    /**
     * 部门编码（唯一标识）
     */
    private String deptCode;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 部门状态（1：启用，0：禁用）
     */
    private Integer deptStatus;

    /**
     * 父部门 ID（顶级部门为 0）
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
     * 子部门集合（树形结构）
     */
    private List<SysDeptTreeVO> children;
}
