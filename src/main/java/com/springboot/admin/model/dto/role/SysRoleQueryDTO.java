package com.springboot.admin.model.dto.role;


import com.springboot.admin.model.dto.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 角色查询 DTO
 * <p>
 * 用于接收前端传递的角色查询条件，结合分页参数进行查询。
 */
@Data
public class SysRoleQueryDTO extends PageQueryDTO {

    /**
     * 角色名称（支持模糊查询）
     */
    @Schema(description = "角色名称（支持模糊查询）")
    private String roleName;

    /**
     * 角色编码（支持精确或模糊查询）
     */
    @Schema(description = "角色编码")
    private String roleCode;

    /**
     * 状态：1=启用，0=禁用
     */
    @Schema(description = "角色状态:1-启用 0-禁用")
    private Integer roleStatus;

    /**
     * 创建时间范围 - 开始
     */
    @Schema(description = "创建时间范围-开始")
    private String createTimeStart;

    /**
     * 创建时间范围 - 结束
     */
    @Schema(description = "创建时间范围-结束")
    private String createTimeEnd;
}
