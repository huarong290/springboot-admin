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
 * 系统用户表
 *
 * @author system
 * @since 2026-01-15
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
    /**
     * 用户名，唯一
     */
    @Schema(description = "用户名，唯一")
    @TableField("username")
    private String username;
    /**
     * 加密密码
     */
    @Schema(description = "加密密码")
    @TableField("password")
    private String password;
    /**
     * 用户昵称
     */
    @Schema(description = "用户昵称")
    @TableField("nickname")
    private String nickname;
    /**
     * 邮箱地址
     */
    @Schema(description = "邮箱地址")
    @TableField("email")
    private String email;
    /**
     * 手机号
     */
    @Schema(description = "手机号")
    @TableField("phone")
    private String phone;
    /**
     * 用户头像URL
     */
    @Schema(description = "用户头像URL")
    @TableField("avatar")
    private String avatar;
    /**
     * 状态：1启用，0禁用
     */
    @Schema(description = "状态：1启用，0禁用")
    @TableField("status")
    private Byte status;
    /**
     * 所属部门ID
     */
    @Schema(description = "所属部门ID")
    @TableField("dept_id")
    private Long deptId;
    /**
     * 所属组织ID
     */
    @Schema(description = "所属组织ID")
    @TableField("org_id")
    private Long orgId;
    /**
     * 最后登录时间
     */
    @Schema(description = "最后登录时间")
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;
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