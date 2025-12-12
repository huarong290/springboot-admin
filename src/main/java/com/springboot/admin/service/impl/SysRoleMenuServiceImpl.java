package com.springboot.admin.service.impl;

import com.springboot.admin.convert.SysRoleMenuConvert;
import com.springboot.admin.model.dto.rolemenu.SysRoleMenuDTO;
import com.springboot.admin.model.entity.sys.SysRoleMenu;
import com.springboot.admin.model.vo.rolemenu.SysRoleMenuVO;
import com.springboot.admin.repository.custom.SysRoleMenuRepositoryCustom;
import com.springboot.admin.repository.single.SysRoleMenuRepository;
import com.springboot.admin.service.ISysRoleMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 角色菜单关联 Service 实现类
 *
 * 提供角色与菜单关联的业务逻辑实现：
 * - 根据角色ID查询菜单关联关系
 * - 新增角色菜单关联
 * - 删除角色菜单关联
 * - 查询所有角色菜单关联
 *
 * 使用响应式编程（Mono/Flux）返回结果，保证非阻塞。
 */
@Service
@Slf4j
public class SysRoleMenuServiceImpl implements ISysRoleMenuService {

    private final SysRoleMenuRepository sysRoleMenuRepository;
    private final SysRoleMenuRepositoryCustom sysRoleMenuRepositoryCustom;
    private final SysRoleMenuConvert sysRoleMenuConvert;


    public SysRoleMenuServiceImpl(SysRoleMenuRepository sysRoleMenuRepository,
                                  SysRoleMenuRepositoryCustom sysRoleMenuRepositoryCustom,
                                  SysRoleMenuConvert sysRoleMenuConvert) {
        this.sysRoleMenuRepository = sysRoleMenuRepository;
        this.sysRoleMenuRepositoryCustom = sysRoleMenuRepositoryCustom;
        this.sysRoleMenuConvert = sysRoleMenuConvert;
    }

    /**
     * 根据角色ID查询菜单关联关系
     *
     * @param roleId 角色ID
     * @return Flux<SysRoleMenuVO> 响应式流，返回该角色绑定的菜单列表
     */
    @Override
    public Flux<SysRoleMenuVO> getMenusByRoleId(Long roleId) {
        log.info("查询角色ID={} 的菜单关联关系", roleId);
        return sysRoleMenuRepositoryCustom.findByRoleId(roleId).map(sysRoleMenuConvert::toVO)
                .doOnComplete(() -> log.info("角色ID={} 的菜单查询完成", roleId))
                .doOnError(e -> log.error("查询角色菜单失败: {}", e.getMessage(), e));
    }

    /**
     * 新增角色菜单关联
     *
     * @param sysRoleMenuDTO 角色菜单对象
     * @return Mono<SysRoleMenu> 响应式单对象，返回保存后的实体
     */
    @Override
    public Mono<Long> addRoleMenu(SysRoleMenuDTO sysRoleMenuDTO) {
        log.info("新增角色菜单关联: {}", sysRoleMenuDTO);
        SysRoleMenu  sysRoleMenu = sysRoleMenuConvert.toEntity(sysRoleMenuDTO);
        return sysRoleMenuRepository.save(sysRoleMenu).map(SysRoleMenu::getId);
    }

    /**
     * 删除角色菜单关联
     *
     * @param id 主键ID
     * @return Mono<Long> 响应式空对象，表示删除完成
     */
    @Override
    public Mono<Long> deleteRoleMenu(Long id) {
        log.info("删除角色菜单关联，ID={}", id);
        return sysRoleMenuRepositoryCustom.deleteRoleMenuById(id,false)
                .doOnSuccess(v -> log.info("角色菜单关联删除成功，ID={}", id))
                .doOnError(e -> log.error("删除角色菜单关联失败: {}", e.getMessage(), e));
    }

    /**
     * 查询所有角色菜单关联
     *
     * @return Flux<SysRoleMenu> 响应式流，返回所有角色菜单关联对象
     */
    @Override
    public Flux<SysRoleMenuVO> getRoleMenuList() {
        log.info("查询所有角色菜单关联");
        return sysRoleMenuRepository.findAll().map(sysRoleMenuConvert::toVO)
                .doOnComplete(() -> log.info("查询所有角色菜单关联完成"))
                .doOnError(e -> log.error("查询所有角色菜单关联失败: {}", e.getMessage(), e));
    }
}
