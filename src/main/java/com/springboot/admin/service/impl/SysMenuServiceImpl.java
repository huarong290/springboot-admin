package com.springboot.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.admin.convert.SysMenuConvert;
import com.springboot.admin.mapper.ext.SysMenuExtMapper;
import com.springboot.admin.model.dto.menu.SysMenuDTO;
import com.springboot.admin.model.entity.sys.SysMenu;
import com.springboot.admin.model.vo.menu.SysMenuTreeVO;
import com.springboot.admin.model.vo.menu.SysMenuVO;
import com.springboot.admin.service.ISysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-01-18
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuExtMapper, SysMenu> implements ISysMenuService {

    @Autowired
    private SysMenuExtMapper sysMenuExtMapper;

    @Autowired
    private SysMenuConvert menuConvert;

    /**
     * 新增菜单
     *
     * @param menuDTO 菜单实体
     * @return 新增菜单ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addMenu(SysMenuDTO menuDTO) {
        SysMenu sysMenu = menuConvert.toEntity(menuDTO);
        // 设置默认父ID，如果为空则默认为根节点 0
        if (sysMenu.getMenuParentId() == null) {
            sysMenu.setMenuParentId(0L);
        }
        sysMenu.setDeleteFlag(0);
        sysMenu.setCreateTime(LocalDateTime.now());
        sysMenu.setUpdateTime(LocalDateTime.now());
        sysMenuExtMapper.insert(sysMenu);
        return sysMenu.getId();
    }

    /**
     * 更新菜单信息
     *
     * @param menuDTO 菜单实体
     * @return 是否更新成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateMenu(SysMenuDTO menuDTO) {
        Long menuId = menuDTO.getId();

        if (menuId == null) {
            throw new IllegalArgumentException("菜单ID不能为空");
        }

        // 先校验是否存在
        SysMenu exist = this.getById(menuId);
        if (exist == null) {
            throw new RuntimeException("菜单不存在，无法更新");
        }

        if (menuDTO.getMenuParentId() != null
                && menuDTO.getMenuParentId().equals(menuId)) {
            throw new RuntimeException("上级菜单不能选择自己");
        }

        SysMenu sysMenu = menuConvert.toEntity(menuDTO);
        sysMenu.setUpdateTime(LocalDateTime.now());

        return sysMenuExtMapper.updateById(sysMenu);
    }




    /**
     * 删除菜单（逻辑 / 物理）
     *
     * 说明：
     * - 逻辑删除：菜单及其所有子菜单统一标记 delete_flag = 1
     * - 物理删除：菜单及其所有子菜单一次性删除
     *
     * @param menuId 菜单ID
     * @param logicalDelete 是否逻辑删除
     * @return 是否删除成功
     */
    @Override
    public int deleteMenu(Long menuId, boolean logicalDelete) {
        if (menuId == null) {
            return 0;
        }

        // 1️⃣ 查询当前菜单及所有子菜单ID
        List<Long> menuIds = sysMenuExtMapper.selectMenuAndChildrenIds(menuId);
        if (menuIds == null || menuIds.isEmpty()) {
            return 0;
        }

        if (logicalDelete) {
            // 2️⃣ 逻辑删除（推荐）
            LambdaUpdateWrapper<SysMenu> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper
                    .in(SysMenu::getId, menuIds)
                    .set(SysMenu::getDeleteFlag, 1)
                    .set(SysMenu::getUpdateTime, LocalDateTime.now());

            return sysMenuExtMapper.update(updateWrapper);
        } else {
            // 3️⃣ 物理删除（使用 Wrapper，避免 deprecated API）
            LambdaQueryWrapper<SysMenu> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.in(SysMenu::getId, menuIds);

            return sysMenuExtMapper.delete(deleteWrapper);
        }
    }
    /**
     * 根据菜单ID查询菜单详情
     *
     * @param menuId 菜单ID
     * @return 菜单 VO
     */
    @Override
    public SysMenuVO getMenuById(Long menuId) {
        SysMenu sysMenu = this.getById(menuId);
        if (sysMenu == null) {
            return null;
        }
        return menuConvert.toVO(sysMenu);
    }

    /**
     * 根据父菜单ID查询子菜单列表
     *
     * @param parentId 父菜单ID
     * @return 子菜单列表
     */
    @Override
    public List<SysMenuVO> getMenusByParentId(Long parentId) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getMenuParentId, parentId)
                .orderByAsc(SysMenu::getMenuSort); // 假设有排序字段
        List<SysMenu> list = this.list(wrapper);
        return menuConvert.toVOList(list);
    }
    /**
     * 查询所有菜单（平铺结构）
     *
     * @return 菜单列表
     */
    @Override
    public List<SysMenuVO> getMenuList() {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysMenu::getMenuSort);
        List<SysMenu> list = this.list(wrapper);
        return menuConvert.toVOList(list);
    }
    /**
     * 查询所有菜单（树形结构）
     *
     * @return 菜单树列表
     */
    @Override
    public List<SysMenuTreeVO> getMenuTree() {
        // 1. 查询所有菜单
        List<SysMenu> allMenus = this.list(new LambdaQueryWrapper<SysMenu>().orderByAsc(SysMenu::getMenuSort));

        // 2. 转换为TreeVO
        List<SysMenuTreeVO> allTreeVOs = menuConvert.toTreeVOList(allMenus);

        // 3. 构建树形结构 (默认根节点父ID为0)
        return buildTree(allTreeVOs, 0L);
    }
    /**
     * 根据用户ID查询菜单树
     *
     * @param userId 用户ID
     * @return 用户可访问的菜单树
     */
    @Override
    public List<SysMenuTreeVO> getMenuTreeByUserId(Long userId) {
        // 1. 获取该用户所有可用的菜单列表（平铺）
        // 这里假设 Mapper 中有自定义 SQL 关联 sys_user_role, sys_role_menu 表进行查询
        // 如果是超级管理员(admin)，通常直接返回所有菜单
        List<SysMenu> menuList;

        // 简单示例逻辑：如果 Mapper 未实现关联查询，需自行补全。
        menuList = sysMenuExtMapper.selectMenusByUserId(userId);

        if (CollectionUtils.isEmpty(menuList)) {
            return new ArrayList<>();
        }

        // 2. 转换为 TreeVO
        List<SysMenuTreeVO> treeVOs = menuConvert.toTreeVOList(menuList);

        // 3. 构建树形结构
        // 注意：由于是根据权限过滤后的列表，可能出现父节点不在列表中的情况（虽然通常UI逻辑会包含父节点）
        // 这里采用通用构建法，寻找列表中 parentId = 0 或者 parentId 不在当前列表中的节点作为根
        return buildTree(treeVOs, 0L);
    }
    /**
     * 根据用户ID查询菜单列表（平铺）
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    @Override
    public List<SysMenuVO> getMenuListByUserId(Long userId) {
        List<SysMenu> menuList = sysMenuExtMapper.selectMenusByUserId(userId);
        return menuConvert.toVOList(menuList);
    }

    /**
     * 根据角色ID查询菜单列表
     *
     * @param roleId 角色ID
     * @return 菜单列表
     */
    @Override
    public List<SysMenuVO> listMenusByRoleId(Long roleId) {
        List<SysMenu> menuList = selectMenusByRoleIds(List.of(roleId));
        return menuConvert.toVOList(menuList);
    }


    @Override
    public List<SysMenu> selectMenusByRoleIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return List.of();
        }
        return sysMenuExtMapper.selectMenusByRoleIds(roleIds);
    }


    // ================= 私有辅助方法 =================

    /**
     * 构建树形结构
     *
     * @param menus 全部菜单节点
     * @param rootParentId 根节点的父ID (通常为 0)
     * @return 树形列表
     */
    private List<SysMenuTreeVO> buildTree(List<SysMenuTreeVO> menus, Long rootParentId) {
        List<SysMenuTreeVO> returnList = new ArrayList<>();

        // 找到所有根节点
        List<SysMenuTreeVO> tempList = menus.stream()
                .filter(menu -> rootParentId.equals(menu.getMenuParentId()))
                .collect(Collectors.toList());

        for (SysMenuTreeVO menu : tempList) {
            // 递归查找子节点
            recursionFn(menus, menu);
            returnList.add(menu);
        }

        // 如果列表为空，尝试查找那些 parentId 不在 menus 列表中的节点作为兜底（处理某些断层数据）
        if (returnList.isEmpty() && !menus.isEmpty()) {
            // 此处逻辑视业务需求而定，简单版只处理 standard root
        }

        return returnList;
    }

    /**
     * 递归列表
     */
    private void recursionFn(List<SysMenuTreeVO> list, SysMenuTreeVO t) {
        // 得到子节点列表
        List<SysMenuTreeVO> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysMenuTreeVO tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SysMenuTreeVO> getChildList(List<SysMenuTreeVO> list, SysMenuTreeVO t) {
        return list.stream()
                .filter(n -> n.getMenuParentId() != null && n.getMenuParentId().equals(t.getId()))
                .sorted(Comparator.comparingInt(node -> node.getMenuSort() == null ? 0 : node.getMenuSort())) // 内存排序
                .collect(Collectors.toList());
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SysMenuTreeVO> list, SysMenuTreeVO t) {
        return getChildList(list, t).size() > 0;
    }
}

