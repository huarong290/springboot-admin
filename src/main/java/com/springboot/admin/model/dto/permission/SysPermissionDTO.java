package com.springboot.admin.model.dto.permission;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 权限点 DTO
 * 用于接口层传输数据
 */
@Data
public class SysPermissionDTO {

    @Schema(description = "权限ID")
    private Long id;

    @Schema(description = "权限编码")
    private String permissionCode;

    @Schema(description = "权限名称")
    private String permissionName;

    @Schema(description = "权限类型：1=接口权限，2=数据权限")
    private Integer permissionType;

    @Schema(description = "是否启用：1=启用，0=禁用")
    private Integer status;

    @Schema(description = "是否删除：1=删除，0=未删除")
    private Integer deleteFlag;

    @Schema(description = "创建者")
    private String createBy;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "修改者")
    private String updateBy;

    @Schema(description = "修改时间")
    private LocalDateTime updateTime;
}
