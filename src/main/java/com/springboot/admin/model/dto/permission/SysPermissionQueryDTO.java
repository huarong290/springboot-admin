package com.springboot.admin.model.dto.permission;


import com.springboot.admin.model.dto.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 权限查询 DTO
 * <p>
 * 用于分页查询权限列表时接收前端传递的查询条件。
 * 继承 PageQueryDTO，包含分页参数（page、size）。
 */
@Data
public class SysPermissionQueryDTO extends PageQueryDTO {

    /** 权限编码（支持模糊查询） */
    @Schema(description = "权限编码")
    private String permissionCode;

    /** 权限名称（支持模糊查询） */
    @Schema(description = "权限名称")
    private String permissionName;

    /** 权限类型：1=接口权限，2=数据权限 */
    @Schema(description = "权限类型:1-接口权限 2-数据权限")
    private Integer permissionType;

    /** 权限状态：1=启用，0=禁用 */
    @Schema(description = "权限状态:1-启用 0-禁用")
    private Integer permissionStatus;

    /** 创建时间起始范围 */
    @Schema(description = "创建时间起始")
    private String createTimeStart;

    /** 创建时间结束范围 */
    @Schema(description = "创建时间结束")
    private String createTimeEnd;
}

