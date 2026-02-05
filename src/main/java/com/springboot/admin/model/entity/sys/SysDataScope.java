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
 * 数据权限范围定义表
 *
 * @author system
 * @since 2026-02-06
 */
@Schema(name = "SysDataScope", description = "数据权限范围定义表")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_data_scope")
public class SysDataScope extends Model<SysDataScope> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 范围编码（ALL/ORG/ORG_AND_CHILD/DEPT/CUSTOM）
     */
    @Schema(description = "范围编码（ALL/ORG/ORG_AND_CHILD/DEPT/CUSTOM）")
    @TableField("scope_code")
    private String scopeCode;
    /**
     * 范围名称
     */
    @Schema(description = "范围名称")
    @TableField("scope_name")
    private String scopeName;
    /**
     * 范围类型：1-全部 2-本组织 3-本组织及下级 4-本部门 5-自定义
     */
    @Schema(description = "范围类型：1-全部 2-本组织 3-本组织及下级 4-本部门 5-自定义")
    @TableField("scope_type")
    private Byte scopeType;
    /**
     * 范围描述
     */
    @Schema(description = "范围描述")
    @TableField("scope_description")
    private String scopeDescription;
    /**
     * 状态：1启用 0禁用
     */
    @Schema(description = "状态：1启用 0禁用")
    @TableField("scope_status")
    private Byte scopeStatus;
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