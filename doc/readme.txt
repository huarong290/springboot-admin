用户 sys_user
   |
   |  (多对多)
   v
角色 sys_role
   |
   |  (多对多)
   +--------> 菜单 sys_menu
   |
   |  (多对多)
   +--------> 权限点 sys_permission

菜单 sys_menu
   |
   | (多对多)
   v
权限点 sys_permission


sys_user ──< sys_user_role >── sys_role ──< sys_role_menu >── sys_menu
                                      │
                                      └──< sys_role_permission >── sys_permission
sys_menu ──< sys_menu_permission >── sys_permission


    用户登录 → 获取用户角色。

    根据角色 → 查询菜单集合（决定前端路由）。

    根据角色 → 查询权限集合（决定接口能否调用）。

    根据菜单 → 查询菜单下的权限点（决定页面按钮是否显示）。

这样就实现了 前端可见性控制 + 后端接口校验 的完整闭环。

/**
 * 用户认证与授权服务实现类
 * <p>
 * - 登录校验用户名/密码
 * - 生成 JWT 访问令牌和刷新令牌
 * - 支持刷新令牌刷新
 * - 支持登出
 * - 获取用户信息，包括角色、权限、菜单树
 */
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.example.mapper.SysPermissionExtMapper">

    <!-- 根据用户ID查询权限点 -->
    <select id="selectPermissionsByUserId" resultType="com.example.entity.SysPermission">
        SELECT DISTINCT p.*
        FROM sys_permission p
        INNER JOIN sys_role_permission rp ON p.id = rp.permission_id
        INNER JOIN sys_user_role ur ON rp.role_id = ur.role_id
        WHERE ur.user_id = #{userId}
    </select>

    <!-- 根据用户ID查询菜单列表 -->
    <select id="selectMenusByUserId" resultType="com.example.entity.SysMenu">
        SELECT DISTINCT m.*
        FROM sys_menu m
        INNER JOIN sys_permission p ON m.id = p.menu_id
        INNER JOIN sys_role_permission rp ON p.id = rp.permission_id
        INNER JOIN sys_user_role ur ON rp.role_id = ur.role_id
        WHERE ur.user_id = #{userId}
        ORDER BY m.parent_id, m.sort_order
    </select>

</mapper>
