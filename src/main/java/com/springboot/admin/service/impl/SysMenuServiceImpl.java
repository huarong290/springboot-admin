package com.springboot.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.admin.convert.SysMenuConvert;
import com.springboot.admin.mapper.ext.SysMenuExtMapper;
import com.springboot.admin.model.entity.sys.SysMenu;
import com.springboot.admin.model.vo.menu.SysMenuTreeVO;
import com.springboot.admin.service.ISysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public List<SysMenuTreeVO> listMenusByUserId(Long userId) {
        List<SysMenu> menus = sysMenuExtMapper.selectMenusByUserId(userId);
        List<SysMenuTreeVO> menuVOs = menuConvert.toVoList(menus);
        return buildMenuTree(menuVOs);
    }

    private List<SysMenuTreeVO> buildMenuTree(List<SysMenuTreeVO> menuList) {
        Map<Long, SysMenuTreeVO> map = new HashMap<>();
        List<SysMenuTreeVO> tree = new ArrayList<>();
        for (SysMenuTreeVO menu : menuList) {
            map.put(menu.getId(), menu);
        }
        for (SysMenuTreeVO menu : menuList) {
            if (menu.getMenuParentId() == null || menu.getMenuParentId() == 0) {
                tree.add(menu);
            } else {
                SysMenuTreeVO parent = map.get(menu.getMenuParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(menu);
                }
            }
        }
        return tree;
    }
}

