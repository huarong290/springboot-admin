package com.springboot.admin.service.impl;

import com.springboot.admin.model.entity.sys.SysOrgClosure;
import com.springboot.admin.mapper.auto.SysOrgClosureMapper;
import com.springboot.admin.service.ISysOrgClosureService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 组织层级闭包表（替代 parent_ids LIKE） 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-01-18
 */
@Service
public class SysOrgClosureServiceImpl extends ServiceImpl<SysOrgClosureMapper, SysOrgClosure> implements ISysOrgClosureService {

}
