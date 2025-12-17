package com.springboot.admin.model.dto.menu;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 菜单 DTO（Data Transfer Object）
 * <p>
 * 用于接收前端传递的菜单数据，主要用于新增和更新操作。
 * 与数据库实体 SysMenu 区分开，避免直接暴露数据库结构。
 */
@Data
public class SysMenuDTO {

    /**
     * 菜单ID
     * 更新时必填，新增时可为空
     */
    @Schema(description = "菜单ID")
    private Long id;

    /**
     * 父菜单ID
     * 顶级菜单为 0
     */
    @Schema(description = "父菜单ID")
    private Long menuParentId;

    /**
     * 菜单名称
     * 示例："用户管理"
     */
    @Schema(description = "菜单名称")
    private String menuName;

    /**
     * 路由路径
     * 示例："/system/user"
     */
    @Schema(description = "路由路径")
    private String menuPath;

    /**
     * 前端组件路径
     * 示例："system/user/index"
     */
    @Schema(description = "前端组件路径")
    private String menuComponent;

    /**
     * 菜单图标
     * 示例："user"
     */
    @Schema(description = "菜单图标")
    private String menuIcon;

    /**
     * 菜单类型
     * 0 = 目录，1 = 菜单，2 = 按钮
     */
    @Schema(description = "菜单类型:0-目录 1-菜单 2-按钮")
    private Integer menuType;

    /**
     * 权限标识
     * 示例："sys:user:add"
     */
    @Schema(description = "权限标识")
    private String menuPermission;

    /**
     * 排序值
     * 数值越小越靠前
     */
    @Schema(description = "排序值")
    private Integer menuSort;

    /**
     * 是否显示
     * 1 = 显示，0 = 隐藏
     */
    @Schema(description = "是否显示")
    private Integer menuVisible;

    /**
     * 是否启用
     * 1 = 启用，0 = 禁用
     */
    @Schema(description = "是否启用:1-启用 0-禁用")
    private Integer menuStatus;
}

