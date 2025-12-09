package com.springboot.admin.model.entity.sys;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * 系统菜单表 (R2DBC 响应式版本)
 */
@Data
@Table("sys_menu")
public class SysMenu {

    @Id
    @Column("id")
    private Long id;

    /**
     * 父菜单ID，顶级菜单为0
     */
    @Column("menu_parent_id")
    private Long menuParentId;

    /**
     * 菜单名称
     */
    @Column("menu_name")
    private String menuName;

    /**
     * 路由路径
     */
    @Column("menu_path")
    private String menuPath;

    /**
     * 前端组件路径
     */
    @Column("menu_component")
    private String menuComponent;

    /**
     * 菜单图标
     */
    @Column("menu_icon")
    private String menuIcon;

    /**
     * 菜单类型：0=目录，1=菜单，2=按钮
     */
    @Column("menu_type")
    private Integer menuType;

    /**
     * 权限标识
     */
    @Column("menu_permission")
    private String menuPermission;

    /**
     * 排序值
     */
    @Column("menu_sort")
    private Integer menuSort;

    /**
     * 是否显示
     */
    @Column("menu_visible")
    private Integer menuVisible;
    /**
     * 是否启用
     * <p>
     * 控制菜单是否可用。
     * 1 = 启用，0 = 禁用。
     * 示例：1（启用）、0（禁用）
     */
    @Column("menu_status")
    private Integer menuStatus;

    /**
     * 是否删除
     */
    @Column("delete_flag")
    private Integer deleteFlag;

    /**
     * 创建者
     */
    @Column("create_by")
    private String createBy;

    /**
     * 创建时间
     */
    @Column("create_time")
    private LocalDateTime createTime;

    /**
     * 修改者
     */
    @Column("update_by")
    private String updateBy;

    /**
     * 修改时间
     */
    @Column("update_time")
    private LocalDateTime updateTime;
}
