package com.springboot.admin.repository.single;

import com.springboot.admin.model.entity.sys.SysPermission;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

/**
 * 权限点表 Repository 接口
 *
 * 提供权限点相关的 CRUD 操作。
 */
public interface SysPermissionRepository extends ReactiveCrudRepository<SysPermission, Long> {
    // 可以扩展自定义查询方法，例如 findByPermissionCode(String code)

    /**
     * 判断权限编码是否存在
     *
     * @param permissionCode 权限编码
     * @return Mono<Boolean> 响应式单对象，返回True或False
     */
    Mono<Boolean> existsByPermissionCode(String permissionCode);
}
