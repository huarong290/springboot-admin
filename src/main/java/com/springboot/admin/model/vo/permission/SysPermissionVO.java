package com.springboot.admin.model.vo.permission;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 权限点 VO
 * 用于前端展示权限信息
 */
@Data
public class SysPermissionVO {

    /**
     * 权限ID
     */
    private Long id;

    /**
     * 权限编码
     * 用于唯一标识权限，例如 "sys:user:add"
     */
    private String permissionCode;

    /**
     * 权限名称
     * 用于展示权限的中文名称，例如 "新增用户"
     */
    private String permissionName;

    /**
     * 权限类型
     * 1 = 接口权限，2 = 数据权限
     */
    private Integer permissionType;

    /**
     * 是否启用
     * 1 = 启用，0 = 禁用
     */
    private Integer permissionStatus;

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
