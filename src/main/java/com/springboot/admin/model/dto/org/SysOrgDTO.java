package com.springboot.admin.model.dto.org;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 组织 DTO（Data Transfer Object）
 * <p>
 * 用于接收前端传输的组织数据，主要用于新增和编辑操作。
 * 与 VO 区分开，避免返回敏感信息。
 */
@Data
public class SysOrgDTO {

    /**
     * 组织ID
     */
    @Schema(description = "组织ID")
    private Long id;

    /**
     * 组织名称
     */
    @Schema(description = "组织名称")
    private String orgName;

    /**
     * 组织编码
     */
    @Schema(description = "组织编码")
    private String orgCode;

    /**
     * 上级组织ID
     */
    @Schema(description = "上级组织ID")
    private Long parentId;

    /**
     * 排序号
     */
    @Schema(description = "排序号")
    private Integer orderNum;

    /**
     * 组织负责人
     */
    @Schema(description = "组织负责人")
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
}
