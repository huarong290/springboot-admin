package com.springboot.admin.service;

import com.springboot.admin.model.entity.sys.SysOrgClosure;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 组织层级闭包表（替代 parent_ids LIKE） 服务类
 * </p>
 *
 * @author system
 * @since 2026-01-18
 */
public interface ISysOrgClosureService extends IService<SysOrgClosure> {

    List<Long> selectDescendantIds(Long orgId);
}
