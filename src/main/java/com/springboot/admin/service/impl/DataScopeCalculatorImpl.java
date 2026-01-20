package com.springboot.admin.service.impl;

import com.springboot.admin.enums.DataScopeTypeEnum;
import com.springboot.admin.model.dto.datascope.DataScopeDTO;
import com.springboot.admin.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 数据权限计算器
 * <p>
 * 根据用户角色，计算可访问的组织/部门范围
 */
@Component
public class DataScopeCalculatorImpl implements IDataScopeCalculator {
    @Autowired
    private ISysUserRoleService iSysUserRoleService;
    @Autowired
    private ISysRoleDataScopeService iSysRoleDataScopeService;
    @Autowired
    private ISysOrgService iSysOrgService;
    @Autowired
    private ISysDeptService sysDeptService;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private ISysOrgClosureService  iSysOrgClosureService;
    @Autowired
    private ISysRoleCustomOrgService sysRoleCustomOrgService ;
    @Autowired
    private ISysRoleCustomDeptService  iSysRoleCustomDeptService;
    /**
     * 根据用户ID计算数据权限范围
     *  @param userId 用户ID
     *  @return 数据权限范围DTO
     */
//    public  DataScopeDTO calculate(Long userId) {
//        DataScopeDTO dto = new DataScopeDTO();
//        // 1. 查询用户角色
//        List<Long> roleIds = iSysUserRoleService.listRoleIdsByUserId(userId);
//
//        // 2. 查询角色可访问的组织和部门
//        // 查询角色可访问的组织范围
//        List<Long> orgIds = iSysRoleDataScopeService.selectScopeIdsByRoleIds(
//                roleIds, DataScopeTypeEnum.ORG.getValue());
//        // 查询角色可访问的部门范围
//        List<Long> deptIds = iSysRoleDataScopeService.selectScopeIdsByRoleIds(
//                roleIds, DataScopeTypeEnum.DEPT.getValue());
//
//        dto.setOrgIds(orgIds);
//        dto.setDeptIds(deptIds);
//
//        // 3. 默认范围类型 ALL / ORG_AND_CHILD / DEPT
//        dto.setScopeType(DataScopeTypeEnum.ORG_AND_CHILD.getCode());
//
//
//        return dto;
//    }


    @Override
    public DataScopeDTO calculate(Long userId) {
        DataScopeDTO dto = new DataScopeDTO();

        List<Long> roleIds = iSysUserRoleService.selectRoleIdsByUserId(userId);
        DataScopeTypeEnum maxScope = iSysRoleDataScopeService.getMaxScopeByRoleIds(roleIds);

        dto.setScopeType(maxScope.getCode());

        switch (maxScope) {
            case ALL -> { // 超级管理员
                dto.setOrgIds(iSysOrgService.selectAllOrgIds());
                dto.setDeptIds(sysDeptService.selectAllDeptIds());
            }
            case ORG_AND_CHILD -> {
                Long orgId = sysUserService.getUserOrgId(userId);
                dto.setOrgIds(iSysOrgClosureService.selectDescendantIds(orgId));
                dto.setDeptIds(sysDeptService.selectByOrgIds(dto.getOrgIds()));
            }
            case DEPT -> {
                Long deptId = sysUserService.getUserDeptId(userId);
                dto.setDeptIds(List.of(deptId));
            }
            case CUSTOM -> {
                dto.setOrgIds(sysRoleCustomOrgService.selectOrgIdsByUserId(userId));
                dto.setDeptIds(iSysRoleCustomDeptService.selectDeptIdsByUserId(userId));
            }
            default -> {}
        }
        return dto;
    }

}
