package com.springboot.admin.model.entity.sys;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * 系统用户表 (R2DBC 响应式版本)
 */
@Data
@Table("sys_user")
public class SysUser {

    @Id
    private Long id;

    /**
     * 用户名，唯一
     */
    @Column("username")
    private String username;

    /**
     * 加密后的密码
     */
    @Column("password")
    private String password;

    /**
     * 用户昵称
     */
    @Column("nickname")
    private String nickname;

    /**
     * 邮箱地址
     */
    @Column("email")
    private String email;

    /**
     * 手机号
     */
    @Column("phone")
    private String phone;

    /**
     * 用户头像URL
     */
    @Column("avatar")
    private String avatar;

    /**
     * 是否启用
     */
    @Column("enabled")
    private Integer enabled;

    /**
     * 所属部门ID
     */
    @Column("dept_id")
    private Long deptId;

    /**
     * 所属组织ID
     */
    @Column("org_id")
    private Long orgId;

    /**
     * 上次登录时间
     */
    @Column("last_login_time")
    private LocalDateTime lastLoginTime;

    /**
     * 是否删除
     */
    @Column("delete_flag")
    private Integer deleteFlag;

    /**
     * 创建者
     */
    @Column("create_by")
    private String createBy;

    /**
     * 创建时间
     */
    @Column("create_time")
    private LocalDateTime createTime;

    /**
     * 修改者
     */
    @Column("update_by")
    private String updateBy;

    /**
     * 修改时间
     */
    @Column("update_time")
    private LocalDateTime updateTime;
}
