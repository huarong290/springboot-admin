package com.springboot.admin.model.vo.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
/**
 * 菜单 VO（View Object）
 * <p>
 * 用于返回给前端的菜单数据，包含展示所需的字段。
 * 与 DTO 区分开，避免返回敏感信息。
 *
 * 特点：
 * - 包含 children 字段，用于构建树形结构
 * - 包含 meta 字段，前端路由常用的扩展信息
 */
@Data
public class SysMenuTreeVO {

    /**
     * 菜单ID
     */
    @Schema(description = "菜单ID")
    private Long id;

    /**
     * 菜单名称
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
     * 父菜单ID
     */
    @Schema(description = "父菜单ID")
    private Long menuParentId;

    /**
     * 菜单类型
     * 0 = 目录，1 = 菜单，2 = 按钮
     */
    @Schema(description = "菜单类型:0-目录 1-菜单 2-按钮")
    private Integer menuType;
    /**
     * 菜单图标
     * 示例："user"
     */
    @Schema(description = "菜单图标")
    private String menuIcon;

    /**
     * 权限标识
     * 示例："sys:user:add"
     */
    @Schema(description = "权限标识")
    private String menuPermission;

    /**
     * 排序值
     */
    @Schema(description = "排序值")
    private Integer menuSort;

    /**
     * 是否显示
     */
    @Schema(description = "是否显示")
    private Integer menuVisible;

    /**
     * 是否启用
     * 1 = 启用，0 = 禁用
     */
    @Schema(description = "是否启用:1-启用 0-禁用")
    private Integer menuStatus;


    /**
     * 子菜单列表
     * 用于构建树形结构
     *
     * 使用 @Schema(implementation = SysMenuTreeVO.class) 避免 Swagger 在解析 List 泛型时触发反射异常。
     */
    @Schema(description = "子菜单列表", implementation = SysMenuTreeVO.class)
    private List<SysMenuTreeVO> children = new ArrayList<>();

    /**
     * meta 信息
     * 前端路由常用的扩展字段，例如标题、图标、是否缓存等
     */
    @Schema(description = "meta 信息")
    private MetaVO meta = new MetaVO();

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
