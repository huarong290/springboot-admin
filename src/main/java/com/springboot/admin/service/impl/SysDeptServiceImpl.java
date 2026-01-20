package com.springboot.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.admin.mapper.ext.SysDeptExtMapper;
import com.springboot.admin.model.entity.sys.SysDept;
import com.springboot.admin.service.ISysDeptService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 部门表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-01-18
 */
@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptExtMapper, SysDept> implements ISysDeptService {

    @Override
    public List<Long> selectAllDeptIds() {
        return this.list().stream().map(SysDept::getId).collect(Collectors.toList());
    }

    @Override
    public List<Long> selectByOrgIds(List<Long> orgIds) {
        return this.listByIds(orgIds).stream()
                .map(SysDept::getId)
                .collect(Collectors.toList());
    }
}
