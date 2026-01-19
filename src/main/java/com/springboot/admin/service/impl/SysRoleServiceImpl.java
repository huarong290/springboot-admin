package com.springboot.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.admin.convert.SysRoleConvert;
import com.springboot.admin.mapper.ext.SysRoleExtMapper;
import com.springboot.admin.model.entity.sys.SysRole;
import com.springboot.admin.model.vo.role.SysRoleVO;
import com.springboot.admin.service.ISysRoleService;
import com.springboot.admin.service.ISysUserRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private ISysUserRoleService iSysUserRoleService;
    @Autowired
    private SysRoleConvert roleConvert;
    @Override
    public List<SysRoleVO> listRolesByUserId(Long userId) {
        // 查询用户关联的角色ID
        List<Long> roleIds = iSysUserRoleService.listRoleIdsByUserId(userId);
        if (roleIds == null || roleIds.isEmpty()) {
            return Collections.emptyList();
        }
        // 根据角色ID批量查询角色信息
        List<SysRole> roles = this.listByIds(roleIds);
        return roleConvert.toVoList(roles);
    }
}
