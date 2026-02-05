package com.springboot.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.admin.mapper.ext.SysOrgClosureExtMapper;
import com.springboot.admin.model.entity.sys.SysOrgClosure;
import com.springboot.admin.service.ISysOrgClosureService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 组织层级闭包表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-02-06
 */
@Service
public class SysOrgClosureServiceImpl extends ServiceImpl<SysOrgClosureExtMapper, SysOrgClosure> implements ISysOrgClosureService {

}
