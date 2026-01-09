package com.springboot.admin.service.impl;

import com.alibaba.fastjson2.JSON;
import com.springboot.admin.convert.SysMenuConvert;
import com.springboot.admin.model.dto.menu.SysMenuDTO;
import com.springboot.admin.model.vo.menu.MetaVO;
import com.springboot.admin.model.vo.menu.SysMenuTreeVO;
import com.springboot.admin.model.vo.menu.SysMenuVO;
import com.springboot.admin.repository.custom.SysMenuRepositoryCustom;
import com.springboot.admin.repository.single.SysMenuRepository;
import com.springboot.admin.service.ISysMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 菜单表 Service 实现类
 * <p>
 * 封装菜单相关业务逻辑：
 * - 单表操作：增删改查、查询
 * - 多表操作：菜单 → 用户、角色
 * - 提供平铺列表和树形结构两种返回形式
 */
@Service
@Slf4j
public class SysMenuServiceImpl implements ISysMenuService {

    private final SysMenuRepository sysMenuRepository;
    private final SysMenuRepositoryCustom sysMenuRepositoryCustom;
    private final SysMenuConvert sysMenuConvert;

    public SysMenuServiceImpl(SysMenuRepository sysMenuRepository,
                              SysMenuRepositoryCustom sysMenuRepositoryCustom,
                              SysMenuConvert sysMenuConvert) {
        this.sysMenuRepository = sysMenuRepository;
        this.sysMenuRepositoryCustom = sysMenuRepositoryCustom;
        this.sysMenuConvert = sysMenuConvert;
    }

    /** ---------------- 单表操作 ---------------- */

    /**
     * 根据菜单ID查询菜单信息
     *
     * 用途：
     * - 后台管理：查看某个菜单详情
     * - 前端路由：根据菜单ID获取对应的路由信息
     *
     * @param id 菜单ID（数据库主键）
     * @return Mono<SysMenuVO> 响应式单对象，可能为空
     */
    @Override
    public Mono<SysMenuVO> getMenuById(Long id) {
        return sysMenuRepository.findById(id)
                .map(sysMenuConvert::toVO);
    }

    /**
     * 根据父菜单ID查询子菜单（带 isLeaf 标识）
     *
     * 用途：
     * - 懒加载菜单树：前端 el-tree 的 lazy 模式需要知道某个节点是否还有子节点。构建树形结构时，查询某个父菜单下的所有子菜单
     * - 后台管理：在菜单维护页面，查看某个目录下的菜单列表 避免一次性加载所有节点，提升性能。
     *
     * 实现思路：
     * 1. 调用 Repository 的 findByMenuParentId 查询指定父节点下的所有子菜单。
     * 2. 对每个子菜单，调用 existsByMenuParentId 判断该菜单是否还有子节点。
     * 3. 使用 SysMenuConvert.toVOWithLeaf 将 SysMenu 转换为 SysMenuVO，并动态设置 isLeaf：
     *    - hasChildren = true  → isLeaf = false（不是叶子节点）
     *    - hasChildren = false → isLeaf = true（是叶子节点）
     *
     * 返回值：
     * - Flux<SysMenuVO> 响应式流，包含多个菜单对象，每个对象带有 isLeaf 字段。
     * - 前端可以直接使用该字段来控制懒加载树的展开逻辑。
     *
     * @param parentId 父菜单ID
     * @return Flux<SysMenuVO> 响应式流，返回多个菜单对象（带 isLeaf）
     */
    @Override
    public Flux<SysMenuVO> getMenusByParentId(Long parentId) {
        return sysMenuRepository.findByMenuParentId(parentId)
                .flatMap(menu -> sysMenuRepository.existsByMenuParentId(menu.getId())
                        .map(hasChildren -> sysMenuConvert.toVOWithLeaf(menu, hasChildren))
                );
    }



    /**
     * 新增菜单
     *
     * 用途：
     * - 后台管理：新增目录、菜单或按钮
     *
     * @param menuDTO 前端传入的菜单对象（DTO）
     * @return Mono<Long> 响应式单对象，返回保存后的实体主键id
     */
    @Override
    public Mono<Long> addMenu(SysMenuDTO menuDTO) {

        return sysMenuRepositoryCustom.insertMenu(menuDTO);

    }

    /**
     * 更新菜单信息
     *
     * 用途：
     * - 后台管理：修改菜单名称、路径、组件等信息
     *
     * @param menuDTO 前端传入的菜单对象（DTO）
     * @return Mono<SysMenuVO> 响应式单对象，返回更新后的实体
     */
    @Override
    public Mono<Long> updateMenu(SysMenuDTO menuDTO) {

        return sysMenuRepositoryCustom.updateMenu(menuDTO);
    }

    /**
     * 删除菜单
     *
     * 用途：
     * - 后台管理：删除某个菜单（通常需要级联删除子菜单）
     *
     * @param id 菜单ID
     * @return Mono<Integer> 响应式单对象，返回删除成功的记录数（1 表示成功）
     */
    @Override
    public Mono<Long> deleteMenu(Long id) {
        return sysMenuRepositoryCustom.deleteMenuById(id,false);
    }

    /**
     * 查询所有菜单（平铺列表）
     *
     * 用途：
     * - 后台管理：展示所有菜单列表
     * - 数据维护：导出菜单信息
     *
     * @return Flux<SysMenuVO> 响应式流，返回多个菜单对象
     */
    @Override
    public Flux<SysMenuVO> getMenuList() {
        return sysMenuRepository.findAll()
                .map(sysMenuConvert::toVO);
    }

    /**
     * 查询所有菜单（树形结构）
     *
     * 用途：
     * - 前端路由：生成动态菜单树
     * - 权限管理：展示角色拥有的菜单树
     *
     * @return Flux<SysMenuTreeVO> 响应式流，返回树形菜单集合
     */
    @Override
    public Flux<SysMenuTreeVO> getMenuTree() {
        return sysMenuRepository.findAll()
                .map(sysMenuConvert::toVO)   // Entity -> VO
                .collectList()
                .flatMapMany(list -> Flux.fromIterable(buildMenuTree(list, 0L)));
    }


    /** ---------------- 多表操作 ---------------- */

    /**
     * 根据用户ID查询菜单列表
     *
     * 用途：
     * - 权限管理：查询某个用户拥有的菜单集合
     * - 前端路由：根据用户权限生成菜单
     *
     * @param userId 用户ID
     * @return Flux<SysMenuVO> 响应式流，返回该用户拥有的菜单集合
     */
    @Override
    public Flux<SysMenuVO> getMenuListByUserId(Long userId) {
        return sysMenuRepositoryCustom.getMenuListByUserId(userId)
                .map(sysMenuConvert::toVO);
    }

    /**
     * 根据角色ID查询菜单列表
     *
     * 用途：
     * - 权限管理：查询某个角色拥有的菜单集合
     * - 前端路由：根据角色权限生成菜单
     *
     * @param roleId 角色ID
     * @return Flux<SysMenuVO> 响应式流，返回该角色拥有的菜单集合
     */
    @Override
    public Flux<SysMenuVO> listMenusByRoleId(Long roleId) {
        return sysMenuRepositoryCustom.getMenuListByRoleId(roleId)
                .map(sysMenuConvert::toVO);
    }

    @Override
    public Flux<SysMenuTreeVO> getMenuTreeByUserId(Long userId) {
        return getMenuListByUserId(userId)
                .collectList()
                .flatMapMany(list -> {
                    log.info("[MenuQuery] 用户 {} 拥有 {} 个菜单", userId, list.size());
                    Map<Long, List<SysMenuVO>> parentMap = list.stream()
                            .collect(Collectors.groupingBy(menu ->
                                    Optional.ofNullable(menu.getMenuParentId()).orElse(0L)));
                    return Flux.fromIterable(buildChildren(parentMap, 0L));
                });
    }

    private List<SysMenuTreeVO> buildChildren(Map<Long, List<SysMenuVO>> parentMap, Long parentId) {
        List<SysMenuVO> children = parentMap.getOrDefault(parentId, Collections.emptyList());

        return children.stream()
                .sorted(Comparator.comparing(SysMenuVO::getMenuSort))
                .map(menu -> {
                    SysMenuTreeVO vo = sysMenuConvert.toTreeVO(menu);

                    // 图标判空处理
                    String icon = Optional.ofNullable(menu.getMenuIcon()).orElse("").toLowerCase();
                    vo.setMenuIcon(icon);

                    // 构建 meta 信息
                    MetaVO meta = new MetaVO();
                    meta.setTitle(menu.getMenuName());
                    meta.setIcon(icon);
                    meta.setKeepAlive(true);
                    meta.setHidden(menu.getMenuVisible() != null && menu.getMenuVisible() == 0);
                    vo.setMeta(meta);

                    // 递归构建子菜单
                    vo.setChildren(buildChildren(parentMap, menu.getId()));

                    log.debug("[TreeBuild] 构建完成: id={}, childrenCount={}", vo.getId(), vo.getChildren().size());
                    return vo;
                })
                .collect(Collectors.toList());
    }




    /** ---------------- 辅助方法 ---------------- */





    /**
     * 构建树形菜单结构
     *
     * 用途：将平铺的菜单列表转换成树形结构，方便前端渲染
     *
     * @param menus 平铺菜单列表
     * @param parentId 父菜单ID
     * @return 树形菜单列表
     */
    private List<SysMenuTreeVO> buildMenuTree(List<SysMenuVO> menus, Long parentId) {
        List<SysMenuTreeVO> tree = new ArrayList<>();
        for (SysMenuVO menu : menus) {
            log.info("[TreeBuild] 正在处理菜单: menu{}", JSON.toJSONString(menu));


            // 判断是否属于当前父节点
            if ((menu.getMenuParentId() == null && parentId == 0L) ||
                    (menu.getMenuParentId() != null && menu.getMenuParentId().equals(parentId))) {
                log.info("[TreeBuild] 原始 menu={}", JSON.toJSONString(menu));
                SysMenuTreeVO vo = sysMenuConvert.toTreeVO(menu);
                log.info("[TreeBuild] 转换后 menu={}", JSON.toJSONString(menu));
                // 递归构建子菜单
                vo.setChildren(buildMenuTree(menus, menu.getId()));

                log.info("[TreeBuild] 完成子菜单构建: id={}, childrenCount={}",
                        vo.getId(), vo.getChildren().size());

                tree.add(vo);
            }
        }
        log.info("[TreeBuild] parentId={} 构建完成，共 {} 个子菜单", parentId, tree.size());
        return tree;
    }

}
