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
 * 组织表（高并发版，仅存自身信息）
 *
 * @author system
 * @since 2026-02-06
 */
@Schema(name = "SysOrg", description = "组织表（高并发版，仅存自身信息）")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_org")
public class SysOrg extends Model<SysOrg> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID，自增唯一标识每个组织
     */
    @Schema(description = "主键ID，自增唯一标识每个组织")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 父组织ID,0表示顶级组织（数据库默认）
     */
    @Schema(description = "父组织ID,0表示顶级组织（数据库默认）")
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
     * 排序号，数字越小越靠前
     */
    @Schema(description = "排序号，数字越小越靠前")
    @TableField("org_sort")
    private Integer orgSort;
    /**
     * 状态：1启用 0禁用
     */
    @Schema(description = "状态：1启用 0禁用")
    @TableField("org_status")
    private Byte orgStatus;
    /**
     * 逻辑删除：0未删除 1已删除
     */
    @Schema(description = "逻辑删除：0未删除 1已删除")
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