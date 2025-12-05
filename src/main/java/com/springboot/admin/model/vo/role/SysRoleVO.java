package com.springboot.admin.model.vo.role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 角色 VO（View Object）
 * <p>
 * 用于返回给前端的角色数据，包含展示所需的字段。
 * 与 DTO 区分开，避免返回敏感信息。
 *
 * 特点：
 * - 包含角色的基本信息
 * - 可扩展关联的菜单、权限信息
 */
@Data
public class SysRoleVO {

    /**
     * 角色ID
     */
    @Schema(description = "角色ID")
    private Long id;

    /**
     * 角色名称
     */
    @Schema(description = "角色名称")
    private String roleName;

    /**
     * 角色编码
     */
    @Schema(description = "角色编码")
    private String roleCode;

    /**
     * 角色描述
     */
    @Schema(description = "角色描述")
    private String roleDescription;

    /**
     * 是否启用
     * 1 = 启用，0 = 禁用
     */
    @Schema(description = "是否启用:1-启用 0-禁用")
    private Integer enabled;

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

    /**
     * 关联的菜单列表
     * 用于前端展示角色拥有的菜单权限
     */
    @Schema(description = "关联的菜单列表")
    private List<Long> menuIds = new ArrayList<>();

    /**
     * 关联的权限标识列表
     * 用于前端展示角色拥有的操作权限
     */
    @Schema(description = "关联的权限标识列表")
    private List<String> permissions = new ArrayList<>();
}
