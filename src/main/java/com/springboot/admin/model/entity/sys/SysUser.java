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
 * 系统用户表
 *
 * @author system
 * @since 2026-01-18
 */
@Schema(name = "SysUser", description = "系统用户表")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_user")
public class SysUser extends Model<SysUser> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** * 租户ID */
    @Schema(description = "租户ID")
    @TableField("tenant_id")
    private Long tenantId;
    /**
     * 登录用户名
     */
    @Schema(description = "登录用户名")
    @TableField("username")
    private String username;
    /**
     * 登录密码（加密）
     */
    @Schema(description = "登录密码（加密）")
    @TableField("password")
    private String password;
    /**
     * 用户昵称
     */
    @Schema(description = "用户昵称")
    @TableField("nickname")
    private String nickname;
    /**
     * 手机号
     */
    @Schema(description = "手机号")
    @TableField("phone")
    private String phone;
    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    @TableField("email")
    private String email;
    /**
     * 头像
     */
    @Schema(description = "头像")
    @TableField("avatar")
    private String avatar;
    /**
     * 所属组织ID
     */
    @Schema(description = "所属组织ID")
    @TableField("org_id")
    private Long orgId;
    /**
     * 所属部门ID
     */
    @Schema(description = "所属部门ID")
    @TableField("dept_id")
    private Long deptId;
    /**
     * 状态：1启用 0禁用
     */
    @Schema(description = "状态：1启用 0禁用")
    @TableField("user_status")
    private Byte userStatus;
    /**
     * 最后登录时间
     */
    @Schema(description = "最后登录时间")
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;
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