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
 * 组织表
 *
 * @author system
 * @since 2026-01-15
 */
@Schema(name = "SysOrg", description = "组织表")
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
     * 父组织ID，顶级组织为0
     */
    @Schema(description = "父组织ID，顶级组织为0")
    @TableField("parent_id")
    private Long parentId;
    /**
     * 父级ID路径，例如 0/1/3
     */
    @Schema(description = "父级ID路径，例如 0/1/3")
    @TableField("parent_ids")
    private String parentIds;
    /**
     * 组织名称
     */
    @Schema(description = "组织名称")
    @TableField("org_name")
    private String orgName;
    /**
     * 组织编码，唯一
     */
    @Schema(description = "组织编码，唯一")
    @TableField("org_code")
    private String orgCode;
    /**
     * 组织类型（集团/公司/事业部等）
     */
    @Schema(description = "组织类型（集团/公司/事业部等）")
    @TableField("org_type")
    private String orgType;
    /**
     * 排序值
     */
    @Schema(description = "排序值")
    @TableField("org_sort")
    private Integer orgSort;
    /**
     * 状态：1启用，0禁用
     */
    @Schema(description = "状态：1启用，0禁用")
    @TableField("org_status")
    private Byte orgStatus;
    /**
     * 是否删除：0未删除，1已删除
     */
    @Schema(description = "是否删除：0未删除，1已删除")
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