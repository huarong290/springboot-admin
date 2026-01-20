package com.springboot.admin.model.dto.datascope;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 数据权限 DTO
 * <p>
 * 由后端计算用户可访问的数据范围。
 * 常用于 SQL 层动态拦截 + 前端按钮/页面权限裁剪。
 */
@Data
public class DataScopeDTO {

    /**
     * 用户全量可访问组织ID列表
     */
    @Schema(description = "可访问组织ID列表")
    private List<Long> orgIds;

    /**
     * 用户全量可访问部门ID列表
     */
    @Schema(description = "可访问部门ID列表")
    private List<Long> deptIds;

    /**
     * 权限范围类型：
     * ALL - 全量
     * ORG - 仅本组织
     * ORG_AND_CHILD - 本组织及下级
     * DEPT - 仅本部门
     * CUSTOM- 自定义
     */
    @Schema(description = "数据权限范围类型")
    private String scopeType;

}
