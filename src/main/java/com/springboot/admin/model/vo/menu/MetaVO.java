package com.springboot.admin.model.vo.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Meta VO
 * <p>
 * 前端路由扩展信息，用于控制菜单展示效果。
 */
@Data
public class MetaVO {

    /**
     * 菜单标题
     * 示例："角色管理"
     */
    @Schema(description = "菜单标题")
    private String title;

    /**
     * 菜单图标
     * 示例："role"
     */
    @Schema(description = "菜单图标")
    private String icon;

    /**
     * 是否缓存页面
     * true = 缓存，false = 不缓存
     */
    @Schema(description = "是否缓存页面")
    private Boolean keepAlive;

    /**
     * 是否隐藏菜单
     * true = 隐藏，false = 显示
     */
    @Schema(description = "是否隐藏菜单")
    private Boolean hidden;
}
