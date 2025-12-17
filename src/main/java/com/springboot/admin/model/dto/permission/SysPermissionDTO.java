package com.springboot.admin.model.dto.permission;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 权限 DTO（Data Transfer Object）
 * <p>
 * 用于接收前端传递的权限数据，主要用于新增和更新操作。
 * 与数据库实体 SysPermission 区分开，避免直接暴露数据库结构。
 */
@Data
public class SysPermissionDTO {

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 权限编码（唯一约束）
     */
    @Schema(description = "权限编码")
    private String permissionCode;

    /**
     * 权限名称
     */
    @Schema(description = "权限名称")
    private String permissionName;

    /**
     * 权限类型：1=接口权限，2=数据权限
     */
    @Schema(description = "权限类型:1-接口权限 2-数据权限")
    private Integer permissionType;

    /**
     * 是否启用：1=启用，0=禁用
     */
    @Schema(description = "是否启用:1-启用 0-禁用")
    private Integer permissionStatus;

    /**
     * 是否删除标记：0=未删除，1=已删除
     */
    @Schema(description = "是否删除")
    private Integer deleteFlag;

    /**
     * 创建者
     */
    @Schema(description = "创建者")
    private String createBy;
    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 修改者
     */
    @Schema(description = "修改者")
    private String updateBy;

    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    private LocalDateTime updateTime;
}
