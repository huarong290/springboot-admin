package com.springboot.admin.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.springboot.admin.model.dto.clientinfo.ClientInfoDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "用户登录请求参数")
public class UserLoginReqDTO {

    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "admin")
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @Schema(description = "密码", example = "123456")
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 验证码
     */
    @Schema(description = "验证码", example = "ABCD")
    @NotBlank(message = "验证码不能为空")
    private String captchaCode;

    /**
     * 验证码标识
     */
    @Schema(description = "验证码ID", example = "captcha_123456")
    @NotBlank(message = "验证码标识不能为空")
    private String captchaId;

    /**
     * 客户端
     */
    private ClientInfoDTO clientInfo;


    /**
     * 登录IP
     * ❌ 注意：不要让 Swagger 显示这个字段，也不要接受前端传值
     * 这个字段只应由 Controller 通过 HttpServletRequest 获取并 set 进去
     */
    @Schema(hidden = true)
    @JsonIgnore // 防止前端通过 JSON body 恶意篡改 IP
    private String loginIp;

}