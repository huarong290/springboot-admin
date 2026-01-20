package com.springboot.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.springboot.admin.model.entity.sys.SysDept;

import java.util.List;

/**
 * <p>
 * 部门表 服务类
 * </p>
 * 说明：
 * - 提供部门信息查询和数据权限支持
 * - 支持根据组织ID查询所属部门
 *
 * 使用场景：
 * - 数据权限计算：DEPT / ORG_AND_CHILD
 * - 获取部门列表/ID集合
 *
 * @author system
 * @since 2026-01-18
 */
public interface ISysDeptService extends IService<SysDept> {

    /**
     * 查询所有部门ID
     *
     * @return 所有部门ID列表
     */
    List<Long> selectAllDeptIds();

    /**
     * 根据组织ID列表查询对应部门ID
     *
     * @param orgIds 组织ID列表
     * @return 部门ID列表
     */
    List<Long> selectByOrgIds(List<Long> orgIds);
}
