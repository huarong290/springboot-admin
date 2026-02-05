package com.springboot.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.admin.mapper.ext.SysDeptExtMapper;
import com.springboot.admin.model.entity.sys.SysDept;
import com.springboot.admin.service.ISysDeptService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 部门表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-02-06
 */
@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptExtMapper, SysDept> implements ISysDeptService {

}
