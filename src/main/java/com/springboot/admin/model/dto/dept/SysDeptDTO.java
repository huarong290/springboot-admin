package com.springboot.admin.model.dto.dept;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 部门 DTO（Data Transfer Object）
 * <p>
 * 用于接收前端传输的部门数据，主要用于新增和编辑操作。
 * 与 VO 区分开，避免返回敏感信息。
 */
@Data
public class SysDeptDTO {

    /**
     * 部门ID
     */
    @Schema(description = "部门ID")
    private Long id;

    /**
     * 部门名称
     */
    @Schema(description = "部门名称")
    private String deptName;

    /**
     * 部门编码
     */
    @Schema(description = "部门编码")
    private String deptCode;

    /**
     * 上级部门ID
     */
    @Schema(description = "上级部门ID")
    private Long parentId;

    /**
     * 排序号
     */
    @Schema(description = "排序号")
    private Integer deptSort;

    /**
     * 部门负责人
     */
    @Schema(description = "部门负责人")
    private String leader;

    /**
     * 联系电话
     */
    @Schema(description = "联系电话")
    private String phone;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    private String email;

    /**
     * 状态
     * 1 = 正常，0 = 停用
     */
    @Schema(description = "状态:1-正常 0-停用")
    private Integer deptStatus;
}
