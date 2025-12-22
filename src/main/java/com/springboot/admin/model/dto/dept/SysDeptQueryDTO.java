package com.springboot.admin.model.dto.dept;

import com.springboot.admin.model.dto.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 部门查询 DTO
 * <p>
 * 用于接收前端传输的部门查询条件，继承分页参数。
 */
@Data
public class SysDeptQueryDTO extends PageQueryDTO {

    /**
     * 部门名称（模糊查询）
     */
    @Schema(description = "部门名称")
    private String deptName;

    /**
     * 部门编码
     */
    @Schema(description = "部门编码")
    private String deptCode;

    /**
     * 状态
     * 1 = 正常，0 = 停用
     */
    @Schema(description = "状态:1-正常 0-停用")
    private Integer deptStatus;
}
