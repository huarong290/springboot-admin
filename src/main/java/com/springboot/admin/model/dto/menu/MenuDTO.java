package com.springboot.admin.model.dto.menu;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单 DTO
 * 用于描述前端路由菜单结构，支持目录、菜单、按钮三种类型
 * 增加 meta 字段，方便前端 Vue/React 动态路由生成
 */
@Data
public class MenuDTO {

    /**
     * 菜单ID
     * 对应 sys_menu.id
     */
    private Long id;

    /**
     * 路由路径
     * 对应 sys_menu.menu_path
     * 示例："/system/user"
     */
    private String path;

    /**
     * 前端组件路径
     * 对应 sys_menu.menu_component
     * 示例："system/user/index"
     */
    private String component;

    /**
     * 父菜单ID
     * 用于构建树形结构
     */
    private Long parentId;

    /**
     * 菜单类型
     * 对应 sys_menu.menu_type
     * 0 = 目录，1 = 菜单，2 = 按钮
     */
    private Integer type;

    /**
     * 权限标识
     * 对应 sys_menu.menu_permission
     * 示例："sys:user:add"
     */
    private String permission;

    /**
     * 子菜单列表
     * 用于构建树形结构
     */
    private List<MenuDTO> children = new ArrayList<>();

    /**
     * meta 信息
     * 前端路由常用的扩展字段
     */
    private MetaDTO meta = new MetaDTO();
}
