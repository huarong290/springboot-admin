package com.springboot.admin.model.vo.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户返回对象 VO
 * <p>
 * 用于接口返回时裁剪敏感字段
 */
@Data
public class SysUserVO {

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long id;

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String username;

    /** 
     * 昵称 
     */
    @Schema(description = "昵称")
    private String nickname;

    /** 
     * 邮箱 
     */
    @Schema(description = "邮箱")
    private String email;

    /** 
     * 手机号 
     */
    @Schema(description = "手机号")
    private String phone;

    /** 
     * 头像URL 
     */
    @Schema(description = "头像URL")
    private String avatar;

    /** 
     * 是否启用:1-启用 0-禁用 
     */
    @Schema(description = "是否启用:1-启用 0-禁用")
    private Integer status;

    /** 
     * 部门ID 
     */
    @Schema(description = "部门ID")
    private Long deptId;

    /** 
     * 组织ID 
     */
    @Schema(description = "组织ID")
    private Long orgId;

    /** 
     * 上次登录时间 
     */
    @Schema(description = "最后登录时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTime;

    /** 
     * 创建时间 
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /** 
     * 更新时间 
     */
    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
