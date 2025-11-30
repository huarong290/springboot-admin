package com.springboot.admin.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户传输对象 DTO
 * 用于新增和修改用户时的请求体
 */
@Data
public class UserDTO {

    @Schema(description = "用户ID（更新时必填）")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码（新增时必填，更新时可选）")
    private String password;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "部门ID")
    private Long deptId;

    @Schema(description = "组织ID")
    private Long orgId;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "是否启用")
    private Integer enabled;

    /**
     * 用户头像 URL
     * 对应 sys_user.avatar
     */
    @Schema(description = "是否启用")
    private String avatar;
}
