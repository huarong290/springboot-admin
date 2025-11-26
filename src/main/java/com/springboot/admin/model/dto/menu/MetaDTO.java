package com.springboot.admin.model.dto.menu;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 路由元信息 DTO
 * 用于前端 Vue/React 路由的 meta 配置
 */
@Data
public class MetaDTO {

    /**
     * 菜单标题
     * 对应 sys_menu.menu_name
     */
    private String title;

    /**
     * 菜单图标
     * 对应 sys_menu.menu_icon
     */
    private String icon;

    /**
     * 是否显示菜单
     * 对应 sys_menu.menu_visible
     */
    private Boolean visible;

    /**
     * 角色限制
     * 当前菜单允许访问的角色列表
     */
    private List<String> roles = new ArrayList<>();;

    /**
     * 权限点限制
     * 当前菜单允许访问的权限点列表
     */
    private List<String> permissions = new ArrayList<>();;
}
