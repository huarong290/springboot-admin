package com.springboot.admin.model.entity.sys;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 菜单表
 *
 * @author system
 * @since 2026-01-18
 */
@Schema(name = "SysMenu", description = "菜单表")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_menu")
public class SysMenu extends Model<SysMenu> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 父菜单ID
     */
    @Schema(description = "父菜单ID")
    @TableField("menu_parent_id")
    private Long menuParentId;
    /**
     * 菜单名称
     */
    @Schema(description = "菜单名称")
    @TableField("menu_name")
    private String menuName;
    /**
     * 菜单路由路径，前端页面访问路径
     */
    @Schema(description = "菜单路由路径，前端页面访问路径")
    @TableField("menu_path")
    private String menuPath;

    /**
     * 菜单对应前端组件路径或组件名
     */
    @Schema(description = "菜单对应前端组件路径或组件名")
    @TableField("menu_component")
    private String menuComponent;

    /**
     * 菜单图标，用于前端显示
     */
    @Schema(description = "菜单图标，用于前端显示")
    @TableField("menu_icon")
    private String menuIcon;
    /**
     * 1目录 2菜单 3按钮
     */
    @Schema(description = "1目录 2菜单 3按钮")
    @TableField("menu_type")
    private Byte menuType;
    /**
     * 菜单排序值，数字越小越靠前
     */
    @Schema(description = "菜单排序值，数字越小越靠前")
    @TableField("menu_sort")
    private Integer menuSort;
    /**
     * 菜单是否可见：1可见 0隐藏，前端渲染控制
     */
    @Schema(description = "菜单是否可见：1可见 0隐藏，前端渲染控制")
    @TableField("visible")
    private Byte visible;

    /**
     * 菜单状态：1启用 0禁用，控制权限访问
     */
    @Schema(description = "菜单状态：1启用 0禁用，控制权限访问")
    @TableField("menu_status")
    private Byte menuStatus;

    /**
     * 删除标志
     */
    @Schema(description = "删除标志")
    @TableLogic
    @TableField("delete_flag")
    private Byte deleteFlag;
    /**
     * 乐观锁版本号
     */
    @Schema(description = "乐观锁版本号")
    @Version
    @TableField("version")
    private Long version;
    /**
     * 创建人
     */
    @Schema(description = "创建人")
    @TableField("create_by")
    private String createBy;
    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    /**
     * 更新人
     */
    @Schema(description = "更新人")
    @TableField("update_by")
    private String updateBy;
    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}