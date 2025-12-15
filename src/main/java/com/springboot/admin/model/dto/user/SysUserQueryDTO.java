package com.springboot.admin.model.dto.user;

import com.springboot.admin.model.dto.PageQueryDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户分页查询参数
 * 继承 PageQueryDTO，增加用户相关的查询条件。
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysUserQueryDTO extends PageQueryDTO {
    /**
     * 用户名模糊查询
     */
    private String username;
    /**
     * 邮箱模糊查询
     */
    private String email;
    /**
     * 手机号模糊查询
     */
    private String phone;
    /**
     * 部门 ID 精确匹配
     */
    private Long deptId;
}
