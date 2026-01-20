package com.springboot.admin.model.dto.datascope;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Data
public class DataScopeDTO2 {

    /**
     * 数据权限范围编码
     * ALL / ORG_AND_CHILD / ORG_ONLY / DEPT_ONLY / CUSTOM
     */
    private String scopeCode;

    /**
     * 是否全量数据
     */
    private boolean all;

    /**
     * 组织ID列表（当 scope = ORG / ORG_AND_CHILD / CUSTOM）
     */
    private List<Long> orgIds = new ArrayList<>();

    /**
     * 部门ID列表（当 scope = DEPT / CUSTOM）
     */
    private List<Long> deptIds = new ArrayList<>();
}

