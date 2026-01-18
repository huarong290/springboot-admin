package com.springboot.admin.service.impl;

import com.springboot.admin.model.entity.sys.SysDept;
import com.springboot.admin.mapper.auto.SysDeptMapper;
import com.springboot.admin.service.ISysDeptService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 部门表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-01-18
 */
@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements ISysDeptService {

}
