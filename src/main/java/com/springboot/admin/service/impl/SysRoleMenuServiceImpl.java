package com.springboot.admin.service.impl;

import com.springboot.admin.convert.SysRoleMenuConvert;
import com.springboot.admin.model.dto.BindResultDTO;
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

import java.util.List;

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
        return sysRoleMenuRepositoryCustom.findMenuIdsByRoleId(roleId).map(sysRoleMenuConvert::toVO)
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

    /**
     * 增量绑定角色菜单
     *
     * 核心逻辑：
     * 1. 查询该角色已有的菜单集合
     * 2. 计算需要新增的菜单（前端传入但数据库没有）
     * 3. 计算需要删除的菜单（数据库有但前端没传入）
     * 4. 执行删除和新增操作
     * 5. 返回 BindResultDTO，包含新增和删除的数量
     *
     * @param roleId 角色ID
     * @param menuIds 前端传入的最新菜单ID集合
     * @return Mono<BindResultDTO> 响应式单对象，包含新增和删除的数量
     */
    @Override
    public Mono<BindResultDTO> bindRoleMenus(Long roleId, List<Long> menuIds) {
        return sysRoleMenuRepositoryCustom.findMenuIdsByRoleId(roleId)
                .map(SysRoleMenu::getMenuId) // 提取 menuId
                .collectList()
                .flatMap(existing -> {
                    // 需要新增的菜单：前端传入但数据库没有
                    List<Long> toAdd = menuIds.stream()
                            .filter(m -> !existing.contains(m))
                            .toList();
                    log.info("Role {} toAdd menus: {}", roleId, toAdd);

                    // 需要删除的菜单：数据库有但前端没传入
                    List<Long> toRemove = existing.stream()
                            .filter(m -> !menuIds.contains(m))
                            .toList();
                    log.info("Role {} toRemove menus: {}", roleId, toRemove);

                    // 执行删除和新增，并返回 BindResultDTO
                    return sysRoleMenuRepositoryCustom.deleteByRoleIdAndMenuIds(roleId, toRemove,false)
                            .flatMap(removeCount ->
                                    sysRoleMenuRepositoryCustom.insertRoleMenus(roleId, toAdd)
                                            .map(addCount -> new BindResultDTO(addCount, removeCount))
                            );
                });
    }

}
