package com.springboot.admin.model.dto.org;

import com.springboot.admin.model.dto.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 组织查询 DTO
 * <p>
 * 用于接收前端传输的组织查询条件，继承分页参数。
 */
@Data
public class SysOrgQueryDTO extends PageQueryDTO {

    /**
     * 组织名称（模糊查询）
     */
    @Schema(description = "组织名称")
    private String orgName;

    /**
     * 组织编码
     */
    @Schema(description = "组织编码")
    private String orgCode;

    /**
     * 状态
     * 1 = 正常，0 = 停用
     */
    @Schema(description = "状态:1-正常 0-停用")
    private Integer status;
}

