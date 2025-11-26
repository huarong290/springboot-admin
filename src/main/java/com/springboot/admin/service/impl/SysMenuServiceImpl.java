package com.springboot.admin.service.impl;

import com.springboot.admin.model.entity.sys.SysMenu;
import com.springboot.admin.repository.single.SysMenuRepository;
import com.springboot.admin.repository.custom.SysMenuRepositoryCustom;
import com.springboot.admin.service.ISysMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 菜单表 Service 实现类
 * <p>
 * 封装菜单相关业务逻辑：
 * - 单表操作：增删改查、查询
 * - 多表操作：菜单 → 角色
 */
@Service
@Slf4j
public class SysMenuServiceImpl implements ISysMenuService {

    private final SysMenuRepository menuRepository;
    private final SysMenuRepositoryCustom menuRepositoryCustom;

    public SysMenuServiceImpl(SysMenuRepository menuRepository,
                              SysMenuRepositoryCustom menuRepositoryCustom) {
        this.menuRepository = menuRepository;
        this.menuRepositoryCustom = menuRepositoryCustom;
    }

    /** ---------------- 单表操作 ---------------- */

    /**
     * 根据菜单ID查询菜单信息
     *
     * @param id 菜单ID
     * @return Mono<SysMenu> 响应式单对象，可能为空
     */
    @Override
    public Mono<SysMenu> getMenuById(Long id) {
        return menuRepository.findById(id);
    }

    @Override
    public Flux<SysMenu> getMenusByParentId(Long parentId) {
        return menuRepository.findByMenuParentId(parentId);
    }

    /**
     * 新增菜单
     *
     * @param menu 菜单对象
     * @return Mono<SysMenu> 响应式单对象，返回保存后的实体
     */
    @Override
    public Mono<SysMenu> addMenu(SysMenu menu) {
        return menuRepository.save(menu);
    }

    /**
     * 更新菜单信息
     *
     * @param menu 菜单对象
     * @return Mono<SysMenu> 响应式单对象，返回更新后的实体
     */
    @Override
    public Mono<SysMenu> updateMenu(SysMenu menu) {
        return menuRepository.save(menu);
    }

    /**
     * 删除菜单
     *
     * @param id 菜单ID
     * @return Mono<Void> 响应式空对象，表示删除完成
     */
    @Override
    public Mono<Void> deleteMenu(Long id) {
        return menuRepository.deleteById(id);
    }

    /**
     * 查询所有菜单
     *
     * @return Flux<SysMenu> 响应式流，返回多个菜单对象
     */
    @Override
    public Flux<SysMenu> getMenuList() {
        return menuRepository.findAll();
    }



    /** ---------------- 多表操作 ---------------- */
    @Override
    public Flux<SysMenu> getMenuListByUserId(Long userId) {
        return menuRepositoryCustom.getMenuListByUserId(userId);
    }

}

