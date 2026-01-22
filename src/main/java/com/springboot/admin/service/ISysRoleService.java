package com.springboot.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.springboot.admin.model.dto.role.SysRoleDTO;
import com.springboot.admin.model.dto.role.SysRoleQueryDTO;
import com.springboot.admin.model.entity.sys.SysRole;
import com.springboot.admin.model.vo.PageResult;
import com.springboot.admin.model.vo.role.SysRoleVO;

import java.util.List;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author system
 * @since 2026-01-18
 */
public interface ISysRoleService extends IService<SysRole> {
    /**
     * 根据角色编码查询角色信息
     *
     * @param query 角色查询DTO
     * @return PageResult<SysRoleVO>
     */
    PageResult<SysRoleVO> pageRoleList(SysRoleQueryDTO query);
    /**
     * 新增角色
     *
     * @param roleDTO 角色对象
     * @return Long 返回保存后的实体主键id
     */
    Long addRole(SysRoleDTO roleDTO);
    /**
     * 更新角色信息
     *
     * @param roleDTO 角色对象
     * @return 返回更新的记录数
     */
    Long updateRole(SysRoleDTO roleDTO);
    /**
     * 删除角色
     *
     * @param id 角色ID
     * @return int 返回删除成功的记录数
     */
    int deleteRole(Long id,boolean logicalDelete);
    /**
     * 批量逻辑删除角色
     *
     * @param ids 角色ID集合
     * @return int 返回删除成功的记录数
     */
    int deleteRolesByIds(List<Long> ids,boolean logicalDelete);

    /**
     * 根据角色ID查询角色信息
     *
     * @param id 角色ID
     * @return SysRoleVO
     */
    SysRoleVO getRoleById(Long id);
    /**
     * 根据角色编码查询角色信息
     *
     * @param roleCode 角色编码
     * @return SysRoleVO
     */
    SysRoleVO getRoleByCode(String roleCode);
    /**
     * 根据用户ID查询角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<SysRoleVO> selectRolesByUserId(Long userId);

}

