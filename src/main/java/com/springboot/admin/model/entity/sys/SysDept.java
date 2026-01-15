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
 * @since 2026-01-15
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
     * 父部门ID，顶级部门为0
     */
    @Schema(description = "父部门ID，顶级部门为0")
    @TableField("parent_id")
    private Long parentId;
    /**
     * 父级ID路径，例如 0/1/3
     */
    @Schema(description = "父级ID路径，例如 0/1/3")
    @TableField("parent_ids")
    private String parentIds;
    /**
     * 所属组织ID
     */
    @Schema(description = "所属组织ID")
    @TableField("org_id")
    private Long orgId;
    /**
     * 部门名称
     */
    @Schema(description = "部门名称")
    @TableField("dept_name")
    private String deptName;
    /**
     * 部门编码，唯一
     */
    @Schema(description = "部门编码，唯一")
    @TableField("dept_code")
    private String deptCode;
    /**
     * 排序值
     */
    @Schema(description = "排序值")
    @TableField("dept_sort")
    private Integer deptSort;
    /**
     * 部门负责人
     */
    @Schema(description = "部门负责人")
    @TableField("leader")
    private String leader;
    /**
     * 联系电话
     */
    @Schema(description = "联系电话")
    @TableField("phone")
    private String phone;
    /**
     * 部门邮箱
     */
    @Schema(description = "部门邮箱")
    @TableField("email")
    private String email;
    /**
     * 状态：1启用，0禁用
     */
    @Schema(description = "状态：1启用，0禁用")
    @TableField("dept_status")
    private Byte deptStatus;
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