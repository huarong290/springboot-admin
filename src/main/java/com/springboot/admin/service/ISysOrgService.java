package com.springboot.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.springboot.admin.model.entity.sys.SysOrg;

import java.util.List;

/**
 * <p>
 * 组织表（高并发版，仅存自身信息） 服务类
 * </p>
 * 说明：
 * - 提供组织信息查询及高并发读写操作
 * - 支持数据权限计算
 *
 * 使用场景：
 * - 数据权限计算：ORG / ORG_AND_CHILD
 * - 获取组织列表/ID集合
 *
 * @author system
 * @since 2026-01-18
 */
public interface ISysOrgService extends IService<SysOrg> {

    /**
     * 查询所有组织ID
     *
     * @return 所有组织ID列表
     */
    List<Long> selectAllOrgIds();
}
