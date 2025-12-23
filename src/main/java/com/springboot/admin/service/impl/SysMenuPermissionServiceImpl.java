package com.springboot.admin.service.impl;

import com.springboot.admin.service.ISysMenuPermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 角色权限表 Service 实现类
 * <p>
 * 封装角色权限关业务逻辑：
 * - 单表操作：增删改查、查询、判断存在
 * - 多表操作：
 * <p>
 * 企业级 MySQL 风格：
 * - 单表用 Repository
 * - 多表用 DatabaseClient 封装在 RepositoryCustom
 */
@Service
@Slf4j
public class SysMenuPermissionServiceImpl implements ISysMenuPermissionService {
}
