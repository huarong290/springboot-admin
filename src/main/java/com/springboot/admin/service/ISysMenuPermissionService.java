package com.springboot.admin.service;

import com.springboot.admin.model.dto.BindResultDTO;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 菜单权限点关联 Service 接口
 *
 * 提供角色与权限点关联的业务逻辑方法。
 */
public interface ISysMenuPermissionService {


    Mono<BindResultDTO> bindMenuPermissions(Long menuId, List<Long> permissionIds);
}
