package com.springboot.admin.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户传输对象 DTO
 * <p>
 * 用于新增和修改用户时的请求体
 */
@Data
public class SysUserDTO {

    /**
     * 用户ID（更新时必填）
     */
    @Schema(description = "用户ID（更新时必填）")
    private Long id;

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String username;

    /**
     * 密码（新增时必填，更新时可选）
     */
    @Schema(description = "密码（新增时必填，更新时可选）")
    private String password;

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
     * 昵称
     */
    @Schema(description = "昵称")
    private String nickname;

    /**
     * 是否启用:1-启用 0-禁用
     */
    @Schema(description = "是否启用:1-启用 0-禁用")
    private Integer userStatus;

    /**
     * 用户头像URL
     */
    @Schema(description = "用户头像URL")
    private String avatar;

    /**
     * 上次登录时间
     */
    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginTime;
}
