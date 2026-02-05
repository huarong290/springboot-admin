package com.springboot.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.admin.mapper.ext.SysDeptClosureExtMapper;
import com.springboot.admin.model.entity.sys.SysDeptClosure;
import com.springboot.admin.service.ISysDeptClosureService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 部门层级闭包表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-02-06
 */
@Service
public class SysDeptClosureServiceImpl extends ServiceImpl<SysDeptClosureExtMapper, SysDeptClosure> implements ISysDeptClosureService {

}
