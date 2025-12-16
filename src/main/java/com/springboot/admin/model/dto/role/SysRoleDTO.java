package com.springboot.admin.model.dto.role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 角色 DTO（Data Transfer Object）
 * <p>
 * 用于接收前端传递的角色数据，主要用于新增和更新操作。
 * 与数据库实体 SysRole 区分开，避免直接暴露数据库结构。
 *
 * 特点：
 * - 字段只包含数据库存储所需的基本信息
 * - 不包含前端展示用的扩展字段
 */
@Data
public class SysRoleDTO {

    /**
     * 角色ID
     * 更新时必填，新增时可为空
     */
    @Schema(description = "角色ID")
    private Long id;

    /**
     * 角色名称
     * 示例："管理员"
     */
    @Schema(description = "角色名称")
    private String roleName;

    /**
     * 角色编码（唯一标识）
     * 示例："admin"
     */
    @Schema(description = "角色编码")
    private String roleCode;

    /**
     * 角色描述
     * 示例："系统管理员，拥有全部权限"
     */
    @Schema(description = "角色描述")
    private String roleDescription;

    /**
     * 是否启用
     * 1 = 启用，0 = 禁用
     */
    @Schema(description = "是否启用:1-启用 0-禁用")
    private Integer roleStatus;
}
