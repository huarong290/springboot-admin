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
 * @since 2026-02-06
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
     * 权限编码（唯一）
     */
    @Schema(description = "权限编码（唯一）")
    @TableField("permission_code")
    private String permissionCode;
    /**
     * 权限名称
     */
    @Schema(description = "权限名称")
    @TableField("permission_name")
    private String permissionName;
    /**
     * 权限类型：1-后端接口鉴权 2-前端按钮显隐 3-数据权限SQL片段（不参与RBAC）
     */
    @Schema(description = "权限类型：1-后端接口鉴权 2-前端按钮显隐 3-数据权限SQL片段（不参与RBAC）")
    @TableField("permission_type")
    private Byte permissionType;
    /**
     * 状态：1启用 0禁用
     */
    @Schema(description = "状态：1启用 0禁用")
    @TableField("permission_status")
    private Byte permissionStatus;
    /**
     * 逻辑删除标志
     */
    @Schema(description = "逻辑删除标志")
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