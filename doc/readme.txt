sys_user ──< sys_user_role >── sys_role ──< sys_role_menu >── sys_menu
                                      │
                                      └──< sys_role_permission >── sys_permission
sys_menu ──< sys_menu_permission >── sys_permission


    用户登录 → 获取用户角色。

    根据角色 → 查询菜单集合（决定前端路由）。

    根据角色 → 查询权限集合（决定接口能否调用）。

    根据菜单 → 查询菜单下的权限点（决定页面按钮是否显示）。

这样就实现了 前端可见性控制 + 后端接口校验 的完整闭环。