package com.springboot.admin.model.entity.sys;

import io.swagger.v3.oas.annotations.media.Schema;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统菜单表
 *
 * @author system
 * @since 2026-01-15
 */
@Schema(name = "SysMenu", description = "系统菜单表")
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
     * 父菜单ID，顶级为0
     */
    @Schema(description = "父菜单ID，顶级为0")
    @TableField("menu_parent_id")
    private Long menuParentId;
    /**
     * 父级ID路径，例如 0/1/3
     */
    @Schema(description = "父级ID路径，例如 0/1/3")
    @TableField("menu_parent_ids")
    private String menuParentIds;
    /**
     * 菜单名称
     */
    @Schema(description = "菜单名称")
    @TableField("menu_name")
    private String menuName;
    /**
     * 路由路径
     */
    @Schema(description = "路由路径")
    @TableField("menu_path")
    private String menuPath;
    /**
     * 前端组件路径
     */
    @Schema(description = "前端组件路径")
    @TableField("menu_component")
    private String menuComponent;
    /**
     * 菜单图标
     */
    @Schema(description = "菜单图标")
    @TableField("menu_icon")
    private String menuIcon;
    /**
     * 菜单类型：0目录 1菜单 2按钮
     */
    @Schema(description = "菜单类型：0目录 1菜单 2按钮")
    @TableField("menu_type")
    private Byte menuType;
    /**
     * 排序值
     */
    @Schema(description = "排序值")
    @TableField("menu_sort")
    private Integer menuSort;
    /**
     * 是否显示：1显示 0隐藏
     */
    @Schema(description = "是否显示：1显示 0隐藏")
    @TableField("menu_visible")
    private Byte menuVisible;
    /**
     * 状态：1启用 0禁用
     */
    @Schema(description = "状态：1启用 0禁用")
    @TableField("menu_status")
    private Byte menuStatus;
    /**
     * 是否删除：0未删除 1已删除
     */
    @Schema(description = "是否删除：0未删除 1已删除")
    @TableLogic
    @TableField("delete_flag")
    private Byte deleteFlag;
    /**
     * 乐观锁版本号
     */
    @Schema(description = "乐观锁版本号")
    @Version
    @TableField("version")
    private Integer version;
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
     * 修改人
     */
    @Schema(description = "修改人")
    @TableField("update_by")
    private String updateBy;
    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}