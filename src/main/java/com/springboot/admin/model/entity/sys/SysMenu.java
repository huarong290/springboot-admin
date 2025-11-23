package com.springboot.admin.model.entity.sys;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * 系统菜单表 (R2DBC 响应式版本)
 */
@Data
@Table("sys_menu")
public class SysMenu {

    @Id
    private Long id;

    /**
     * 父菜单ID
     */
    private Long menuParentId;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 路由路径
     */
    private String menuPath;

    /**
     * 前端组件路径
     */
    private String menuComponent;

    /**
     * 菜单图标
     */
    private String menuIcon;

    /**
     * 菜单类型：0=目录，1=菜单，2=按钮
     */
    private Integer menuType;

    /**
     * 权限标识
     */
    private String menuPermission;

    /**
     * 排序值
     */
    private Integer menuSort;

    /**
     * 是否显示
     */
    private Integer menuVisible;

    /**
     * 是否删除
     */
    private Integer deleteFlag;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改者
     */
    private String updateBy;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;
}
