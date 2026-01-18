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
 * 部门表
 *
 * @author system
 * @since 2026-01-18
 */
@Schema(name = "SysDept", description = "部门表")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_dept")
public class SysDept extends Model<SysDept> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 父部门ID，顶级为0
     */
    @Schema(description = "父部门ID，顶级为0")
    @TableField("parent_id")
    private Long parentId;
    /**
     * 所属组织ID（冗余字段）
     */
    @Schema(description = "所属组织ID（冗余字段）")
    @TableField("org_id")
    private Long orgId;
    /**
     * 部门名称
     */
    @Schema(description = "部门名称")
    @TableField("dept_name")
    private String deptName;
    /**
     * 部门编码
     */
    @Schema(description = "部门编码")
    @TableField("dept_code")
    private String deptCode;
    /**
     * 排序号
     */
    @Schema(description = "排序号")
    @TableField("dept_sort")
    private Integer deptSort;
    /**
     * 状态：1启用 0禁用
     */
    @Schema(description = "状态：1启用 0禁用")
    @TableField("dept_status")
    private Byte deptStatus;
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