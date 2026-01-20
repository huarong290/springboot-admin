package com.springboot.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.admin.mapper.ext.SysOrgClosureExtMapper;
import com.springboot.admin.model.entity.sys.SysOrgClosure;
import com.springboot.admin.service.ISysOrgClosureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 组织层级闭包表（替代 parent_ids LIKE） 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-01-18
 */
@Service
public class SysOrgClosureServiceImpl extends ServiceImpl<SysOrgClosureExtMapper, SysOrgClosure> implements ISysOrgClosureService {

    @Autowired
    private SysOrgClosureExtMapper sysOrgClosureExtMapper;

    @Override
    public List<Long> selectDescendantIds(Long orgId) {
        return sysOrgClosureExtMapper.selectDescendantIds(orgId);
    }
}
