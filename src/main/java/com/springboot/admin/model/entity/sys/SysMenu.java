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
 * 系统菜单表，存储前端菜单和权限路由
 *
 * @author system
 * @since 2026-02-06
 */
@Schema(name = "SysMenu", description = "系统菜单表，存储前端菜单和权限路由")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_menu")
public class SysMenu extends Model<SysMenu> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID，自增唯一标识每个菜单
     */
    @Schema(description = "主键ID，自增唯一标识每个菜单")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 父菜单ID,0表示顶级菜单
     */
    @Schema(description = "父菜单ID,0表示顶级菜单")
    @TableField("menu_parent_id")
    private Long menuParentId;
    /**
     * 菜单名称，用于显示和识别菜单
     */
    @Schema(description = "菜单名称，用于显示和识别菜单")
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
     * 菜单类型：1目录 2菜单 3按钮，用于前端区分
     */
    @Schema(description = "菜单类型：1目录 2菜单 3按钮，用于前端区分")
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
     * 逻辑删除标志：0未删除 1已删除
     */
    @Schema(description = "逻辑删除标志：0未删除 1已删除")
    @TableLogic
    @TableField("delete_flag")
    private Byte deleteFlag;
    /**
     * 乐观锁版本号，用于并发控制
     */
    @Schema(description = "乐观锁版本号，用于并发控制")
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
     * 最后更新人
     */
    @Schema(description = "最后更新人")
    @TableField("update_by")
    private String updateBy;
    /**
     * 最后更新时间
     */
    @Schema(description = "最后更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}