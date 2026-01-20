package com.springboot.admin.utils;

import com.springboot.admin.model.vo.menu.SysMenuTreeVO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MenuTreeBuilderUtil {

    public static List<SysMenuTreeVO> build(List<SysMenuTreeVO> list) {
        Map<Long, SysMenuTreeVO> map = new HashMap<>();
        List<SysMenuTreeVO> tree = new ArrayList<>();

        for (SysMenuTreeVO menu : list) {
            map.put(menu.getId(), menu);
        }

        for (SysMenuTreeVO menu : list) {
            if (menu.getMenuParentId() == null || menu.getMenuParentId() == 0) {
                tree.add(menu);
            } else {
                SysMenuTreeVO parent = map.get(menu.getMenuParentId());
                if (parent != null) {
                    parent.getChildren().add(menu);
                }
            }
        }
        return tree;
    }
}
