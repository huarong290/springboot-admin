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
    @TableField("parent_id")
    private Long parentId;
    /**
     * 菜单名称
     */
    @Schema(description = "菜单名称")
    @TableField("menu_name")
    private String menuName;
    @TableField("menu_path")
    private String menuPath;
    @TableField("menu_component")
    private String menuComponent;
    @TableField("menu_icon")
    private String menuIcon;
    /**
     * 0目录 1菜单 2按钮
     */
    @Schema(description = "0目录 1菜单 2按钮")
    @TableField("menu_type")
    private Byte menuType;
    @TableField("menu_sort")
    private Integer menuSort;
    @TableField("visible")
    private Byte visible;
    @TableField("menu_status")
    private Byte menuStatus;
    @TableLogic
    @TableField("delete_flag")
    private Byte deleteFlag;
    @Version
    @TableField("version")
    private Long version;
    @TableField("create_by")
    private String createBy;
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField("update_by")
    private String updateBy;
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}