package com.springboot.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.admin.convert.SysRoleConvert;
import com.springboot.admin.mapper.ext.SysRoleExtMapper;
import com.springboot.admin.model.dto.role.SysRoleDTO;
import com.springboot.admin.model.dto.role.SysRoleQueryDTO;
import com.springboot.admin.model.entity.sys.SysRole;
import com.springboot.admin.model.vo.PageResult;
import com.springboot.admin.model.vo.role.SysRoleVO;
import com.springboot.admin.service.ISysRoleService;
import com.springboot.admin.service.ISysUserRoleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-01-18
 */
@Service
@Slf4j
public class SysRoleServiceImpl extends ServiceImpl<SysRoleExtMapper, SysRole> implements ISysRoleService {
    @Autowired
    private SysRoleExtMapper sysRoleExtMapper;
    @Autowired
    private ISysUserRoleService iSysUserRoleService;
    @Autowired
    private SysRoleConvert roleConvert;

    @Override
    public PageResult<SysRoleVO> pageRoleList(SysRoleQueryDTO query) {
        // 1️⃣ 创建 LambdaQueryWrapper 条件
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getDeleteFlag, 0); // 逻辑未删除的记录

        if (StringUtils.isNotBlank(query.getRoleName())) {
            wrapper.like(SysRole::getRoleName, query.getRoleName());
        }
        if (StringUtils.isNotBlank(query.getRoleCode())) {
            wrapper.like(SysRole::getRoleCode, query.getRoleCode());
        }
        // 2️⃣ 分页查询
        int page = query.getPage() > 0 ? query.getPage() : 1;
        int size = query.getSize() > 0 ? query.getSize() : 10;

        // MyBatis-Plus 内置分页插件，使用 page 对象
        Page<SysRole> pageInfo = new Page<>(page, size);
        page(pageInfo, wrapper);
        List<SysRoleVO> voList = roleConvert.toVoList(pageInfo.getRecords());
        PageResult<SysRoleVO> result = new PageResult<>();
        result.setRecords(voList);
        result.setTotal(pageInfo.getTotal());
        result.setPage(pageInfo.getCurrent());
        result.setSize(pageInfo.getSize());
        return result;
    }

    /**
     * 新增角色
     */
    @Override
    public Long addRole(SysRoleDTO roleDTO) {
        SysRole role = roleConvert.dtoToEntity(roleDTO);
        role.setCreateTime(LocalDateTime.now());
        role.setUpdateTime(LocalDateTime.now());
        save(role);
        return role.getId();
    }

    /**
     * 更新角色信息
     */
    @Override
    public Long updateRole(SysRoleDTO roleDTO) {
        SysRole role = roleConvert.dtoToEntity(roleDTO);
        role.setUpdateTime(LocalDateTime.now());
        updateById(role);
        return role.getId();
    }

    /**
     * 单个删除角色（逻辑/物理）
     */
    @Override
    public int deleteRole(Long id, boolean logicalDelete) {
        if (id == null) return 0;
        if (logicalDelete) {
            LambdaUpdateWrapper<SysRole> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(SysRole::getId, id)
                    .set(SysRole::getDeleteFlag, 1)
                    .set(SysRole::getUpdateTime, LocalDateTime.now());
            return this.baseMapper.update(null, wrapper);
        } else {
            return this.baseMapper.deleteById(id);
        }
    }

    /**
     * 批量删除角色（逻辑/物理）
     */
    @Override
    public int deleteRolesByIds(List<Long> ids, boolean logicalDelete) {
        if (ids == null || ids.isEmpty()) return 0;
        if (logicalDelete) {
            LambdaUpdateWrapper<SysRole> wrapper = new LambdaUpdateWrapper<>();
            wrapper.in(SysRole::getId, ids)
                    .set(SysRole::getDeleteFlag, 1)
                    .set(SysRole::getUpdateTime, LocalDateTime.now());
            return this.baseMapper.update(null, wrapper);
        } else {
            LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(SysRole::getId, ids);
            return this.baseMapper.delete(wrapper);
        }
    }



    /**
     * 根据ID查询角色
     */
    @Override
    public SysRoleVO getRoleById(Long id) {
        SysRole role = getById(id);
        if (role == null || role.getDeleteFlag() == 1) return null;
        return roleConvert.toVo(role);
    }

    /**
     * 根据角色编码查询角色
     */
    @Override
    public SysRoleVO getRoleByCode(String roleCode) {
        if (StringUtils.isBlank(roleCode)) return null;
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleCode, roleCode)
                .eq(SysRole::getDeleteFlag, 0);
        SysRole role = getOne(wrapper);
        return role == null ? null : roleConvert.toVo(role);
    }
    /**
     * 查询用户拥有的角色列表
     */
    @Override
    public List<SysRoleVO> selectRolesByUserId(Long userId) {
        // 查询用户关联的角色ID
        List<Long> roleIds = iSysUserRoleService.selectRoleIdsByUserId(userId);
        if (roleIds == null || roleIds.isEmpty()) {
            return Collections.emptyList();
        }
        // 根据角色ID批量查询角色信息
        List<SysRole> roles = this.listByIds(roleIds);
        return roleConvert.toVoList(roles);
    }

}
