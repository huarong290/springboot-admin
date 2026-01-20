package com.springboot.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.admin.mapper.ext.SysOrgExtMapper;
import com.springboot.admin.model.entity.sys.SysOrg;
import com.springboot.admin.service.ISysOrgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 组织表（高并发版，仅存自身信息） 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-01-18
 */
@Service
public class SysOrgServiceImpl extends ServiceImpl<SysOrgExtMapper, SysOrg> implements ISysOrgService {
    @Autowired
    private SysOrgExtMapper sysOrgExtMapper;
    @Override
    public List<Long> selectAllOrgIds() {

        return this.list().stream().map(SysOrg::getId).collect(Collectors.toList());
    }
}
