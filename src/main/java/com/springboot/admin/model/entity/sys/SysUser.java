package com.springboot.admin.model.entity.sys;

import lombok.Data;
import org.springframework.data.annotation.Id;
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
    private String username;

    /**
     * 加密后的密码
     */
    private String password;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 用户头像URL
     */
    private String avatar;

    /**
     * 是否启用
     */
    private Integer enabled;

    /**
     * 上次登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 是否删除
     */
    private Integer deleteFlag;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改者
     */
    private String updateBy;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;
}
