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
 * 角色与菜单关联表
 *
 * @author system
 * @since 2026-01-18
 */
@Schema(name = "SysRoleMenu", description = "角色与菜单关联表")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_role_menu")
public class SysRoleMenu extends Model<SysRoleMenu> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 角色ID
     */
    @Schema(description = "角色ID")
    @TableField("role_id")
    private Long roleId;
    /**
     * 菜单ID
     */
    @Schema(description = "菜单ID")
    @TableField("menu_id")
    private Long menuId;
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