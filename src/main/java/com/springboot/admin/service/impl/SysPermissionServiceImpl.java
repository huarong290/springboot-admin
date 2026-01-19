package com.springboot.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.admin.convert.SysPermissionConvert;
import com.springboot.admin.mapper.ext.SysPermissionExtMapper;
import com.springboot.admin.model.entity.sys.SysPermission;
import com.springboot.admin.model.vo.menu.SysMenuTreeVO;
import com.springboot.admin.model.vo.permission.SysPermissionVO;
import com.springboot.admin.service.ISysPermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 系统权限点表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-01-18
 */
@Service
@Slf4j
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionExtMapper, SysPermission> implements ISysPermissionService {

    @Autowired
    private SysPermissionExtMapper sysPermissionExtMapper;

    @Autowired
    private SysPermissionConvert permissionConvert;

    @Override
    public List<SysPermissionVO> listPermissionsByUserId(Long userId) {
        List<SysPermission> permissions = sysPermissionExtMapper.selectPermissionsByUserId(userId);
        return permissionConvert.toVoList(permissions);
    }


}
