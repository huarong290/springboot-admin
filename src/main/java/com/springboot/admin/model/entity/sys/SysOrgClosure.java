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
 * 组织层级闭包表（替代 parent_ids LIKE）
 *
 * @author system
 * @since 2026-01-18
 */
@Schema(name = "SysOrgClosure", description = "组织层级闭包表（替代 parent_ids LIKE）")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_org_closure")
public class SysOrgClosure extends Model<SysOrgClosure> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 祖先组织ID
     */
    @Schema(description = "祖先组织ID")
    @TableField("ancestor_id")
    private Long ancestorId;
    /**
     * 后代组织ID
     */
    @Schema(description = "后代组织ID")
    @TableField("descendant_id")
    private Long descendantId;
    /**
     * 层级深度：0自身 1子级 2孙级
     */
    @Schema(description = "层级深度：0自身 1子级 2孙级")
    @TableField("depth")
    private Integer depth;
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