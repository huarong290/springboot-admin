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
 * 系统权限点表
 *
 * @author system
 * @since 2026-01-15
 */
@Schema(name = "SysPermission", description = "系统权限点表")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_permission")
public class SysPermission extends Model<SysPermission> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 权限编码
     */
    @Schema(description = "权限编码")
    @TableField("permission_code")
    private String permissionCode;
    /**
     * 权限名称
     */
    @Schema(description = "权限名称")
    @TableField("permission_name")
    private String permissionName;
    /**
     * 权限类型：1接口权限 2数据权限
     */
    @Schema(description = "权限类型：1接口权限 2数据权限")
    @TableField("permission_type")
    private Byte permissionType;
    /**
     * 状态：1启用 0禁用
     */
    @Schema(description = "状态：1启用 0禁用")
    @TableField("permission_status")
    private Byte permissionStatus;
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