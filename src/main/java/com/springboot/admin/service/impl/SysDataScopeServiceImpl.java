package com.springboot.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.admin.mapper.ext.SysDataScopeExtMapper;
import com.springboot.admin.model.entity.sys.SysDataScope;
import com.springboot.admin.service.ISysDataScopeService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 数据权限范围定义表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-02-06
 */
@Service
public class SysDataScopeServiceImpl extends ServiceImpl<SysDataScopeExtMapper, SysDataScope> implements ISysDataScopeService {

}
