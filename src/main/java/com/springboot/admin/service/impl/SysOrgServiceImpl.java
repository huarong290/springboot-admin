package com.springboot.admin.service.impl;

import com.springboot.admin.model.entity.sys.SysOrg;
import com.springboot.admin.mapper.auto.SysOrgMapper;
import com.springboot.admin.service.ISysOrgService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 组织表（高并发版，仅存自身信息） 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-01-18
 */
@Service
public class SysOrgServiceImpl extends ServiceImpl<SysOrgMapper, SysOrg> implements ISysOrgService {

}
