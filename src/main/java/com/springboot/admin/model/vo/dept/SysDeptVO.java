package com.springboot.admin.model.vo.dept;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 部门 VO（View Object）
 * <p>
 * 用于返回给前端的部门数据，包含展示所需的字段。
 * 与 DTO 区分开，避免返回敏感信息。
 *
 * 特点：
 * - 包含部门的基本信息
 * - 可扩展关联的上级部门、用户信息
 */
@Data
public class SysDeptVO {

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
     * 上级部门名称
     */
    @Schema(description = "上级部门名称")
    private String parentName;

    /**
     * 排序号
     */
    @Schema(description = "排序号")
    private Integer orderNum;

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
    private Integer status;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
