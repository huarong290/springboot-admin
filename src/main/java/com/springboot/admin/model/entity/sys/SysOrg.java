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
 * 组织表（高并发版，仅存自身信息）
 *
 * @author system
 * @since 2026-01-18
 */
@Schema(name = "SysOrg", description = "组织表（高并发版，仅存自身信息）")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_org")
public class SysOrg extends Model<SysOrg> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 父组织ID，顶级为0
     */
    @Schema(description = "父组织ID，顶级为0")
    @TableField("parent_id")
    private Long parentId;
    /**
     * 组织名称
     */
    @Schema(description = "组织名称")
    @TableField("org_name")
    private String orgName;
    /**
     * 组织唯一编码
     */
    @Schema(description = "组织唯一编码")
    @TableField("org_code")
    private String orgCode;
    /**
     * 组织类型（集团/公司/事业部等）
     */
    @Schema(description = "组织类型（集团/公司/事业部等）")
    @TableField("org_type")
    private String orgType;
    /**
     * 排序号
     */
    @Schema(description = "排序号")
    @TableField("org_sort")
    private Integer orgSort;
    /**
     * 状态：1启用 0禁用
     */
    @Schema(description = "状态：1启用 0禁用")
    @TableField("org_status")
    private Byte orgStatus;
    /**
     * 删除标志：0未删除 1已删除
     */
    @Schema(description = "删除标志：0未删除 1已删除")
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