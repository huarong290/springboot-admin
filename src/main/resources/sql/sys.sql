-- =========================
-- 组织表
-- =========================
CREATE TABLE `sys_org`
(
    `id`          bigint       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `parent_id`   bigint       NOT NULL DEFAULT '0' COMMENT '上级组织ID，顶级组织为0',
    `org_name`    varchar(100) NOT NULL COMMENT '组织名称',
    `org_code`    varchar(50)  NOT NULL COMMENT '组织编码，唯一',
    `org_type`    varchar(50)           DEFAULT NULL COMMENT '组织类型（集团/公司/事业部等）',
    `org_sort`    int          NOT NULL DEFAULT '0' COMMENT '排序值',
    `org_status`  tinyint(5) NOT NULL DEFAULT '1' COMMENT '状态：1=启用，0=禁用',
    `delete_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
    `create_by`   varchar(64)  NOT NULL DEFAULT 'system' COMMENT '创建者',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   varchar(64)  NOT NULL DEFAULT 'system' COMMENT '修改者',
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_org_code` (`org_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='组织表';

-- =========================
-- 部门表
-- =========================
CREATE TABLE `sys_dept`
(
    `id`          bigint       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `parent_id`   bigint       NOT NULL DEFAULT '0' COMMENT '上级部门ID，顶级部门为0',
    `org_id`      bigint       NOT NULL COMMENT '所属组织ID',
    `dept_name`   varchar(100) NOT NULL COMMENT '部门名称',
    `dept_code`   varchar(50)  NOT NULL COMMENT '部门编码，唯一',
    `dept_sort`   int          NOT NULL DEFAULT '0' COMMENT '排序值',
    `leader`      varchar(64)           DEFAULT NULL COMMENT '部门负责人',
    `phone`       varchar(20)           DEFAULT NULL COMMENT '联系电话',
    `email`       varchar(100)          DEFAULT NULL COMMENT '部门邮箱',
    `dept_status` tinyint(5) NOT NULL DEFAULT '1' COMMENT '状态：1=启用，0=禁用',
    `delete_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
    `create_by`   varchar(64)  NOT NULL DEFAULT 'system' COMMENT '创建者',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   varchar(64)  NOT NULL DEFAULT 'system' COMMENT '修改者',
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dept_code` (`dept_code`),
    KEY           `idx_org_id` (`org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- =========================
-- 用户表
-- =========================
CREATE TABLE `sys_user`
(
    `id`              bigint       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username`        varchar(50)  NOT NULL COMMENT '用户名，唯一',
    `password`        varchar(100) NOT NULL COMMENT '加密后的密码',
    `nickname`        varchar(50)           DEFAULT '' COMMENT '用户昵称',
    `email`           varchar(100)          DEFAULT '' COMMENT '邮箱地址',
    `phone`           varchar(20)           DEFAULT '' COMMENT '手机号',
    `avatar`          varchar(255)          DEFAULT '' COMMENT '用户头像URL',
    `status`          tinyint(5) NOT NULL DEFAULT '1' COMMENT '是否启用',
    `dept_id`         bigint                DEFAULT NULL COMMENT '所属部门ID',
    `org_id`          bigint                DEFAULT NULL COMMENT '所属组织ID',
    `last_login_time` datetime              DEFAULT NULL COMMENT '上次登录时间',
    `delete_flag`     tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
    `create_by`       varchar(64)  NOT NULL DEFAULT 'system' COMMENT '创建者',
    `create_time`     datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       varchar(64)  NOT NULL DEFAULT 'system' COMMENT '修改者',
    `update_time`     datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- =========================
-- 角色表
-- =========================
CREATE TABLE `sys_role`
(
    `id`               bigint      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `role_name`        varchar(50) NOT NULL COMMENT '角色名称',
    `role_code`        varchar(50) NOT NULL COMMENT '角色编码',
    `role_description` varchar(255)         DEFAULT '' COMMENT '角色描述',
    `role_status`      tinyint(5) NOT NULL DEFAULT '1' COMMENT '是否启用',
    `delete_flag`      tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
    `create_by`        varchar(64) NOT NULL DEFAULT 'system' COMMENT '创建者',
    `create_time`      datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`        varchar(64) NOT NULL DEFAULT 'system' COMMENT '修改者',
    `update_time`      datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色表';


-- =========================
-- 菜单表（优化版）
-- =========================
CREATE TABLE `sys_menu`
(
    `id`              bigint      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `menu_parent_id`  bigint      NOT NULL DEFAULT '0' COMMENT '父菜单ID',
    `menu_parent_ids` varchar(500)         DEFAULT '' COMMENT '所有上级菜单ID序列，逗号分隔（如：0,1,5）',
    `menu_name`       varchar(50) NOT NULL COMMENT '菜单名称',
    `menu_path`       varchar(100)         DEFAULT '' COMMENT '路由路径',
    `menu_component`  varchar(100)         DEFAULT '' COMMENT '前端组件路径',
    `menu_icon`       varchar(50)          DEFAULT '' COMMENT '菜单图标',
    `menu_type`       tinyint     NOT NULL DEFAULT '1' COMMENT '菜单类型：0=目录，1=菜单，2=按钮',
    `menu_sort`       int         NOT NULL DEFAULT '0' COMMENT '排序值',
    `menu_visible`    tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否显示',
    `menu_status`     tinyint(5) NOT NULL DEFAULT '1' COMMENT '是否启用',
    `delete_flag`     tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
    `create_by`       varchar(64) NOT NULL DEFAULT 'system' COMMENT '创建者',
    `create_time`     datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       varchar(64) NOT NULL DEFAULT 'system' COMMENT '修改者',
    `update_time`     datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    KEY               `idx_parent_id` (`menu_parent_id`),
    -- 为路径字段添加索引，方便使用 LIKE '0,1,%' 进行前缀匹配查询
    KEY               `idx_parent_ids` (`menu_parent_ids`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统菜单表';

-- =========================
-- 权限点表
-- =========================
CREATE TABLE `sys_permission`
(
    `id`                bigint       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `permission_code`   varchar(100) NOT NULL DEFAULT ''  COMMENT '权限编码',
    `permission_name`   varchar(100) NOT NULL DEFAULT '1' COMMENT '权限名称',
    `permission_type`   tinyint      NOT NULL DEFAULT '1' COMMENT '权限类型：1=接口权限，2=数据权限',
    `permission_status` tinyint(5) NOT NULL DEFAULT '1' COMMENT '是否启用:1-启用 0-禁用',
    `delete_flag`       tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
    `create_by`         varchar(64)  NOT NULL DEFAULT 'system' COMMENT '创建者',
    `create_time`       datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`         varchar(64)  NOT NULL DEFAULT 'system' COMMENT '修改者',
    `update_time`       datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_permission_code` (`permission_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统权限点表';

-- =========================
-- 用户角色关联表
-- =========================
CREATE TABLE `sys_user_role`
(
    `id`          bigint      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`     bigint      NOT NULL COMMENT '用户ID',
    `role_id`     bigint      NOT NULL COMMENT '角色ID',
    `delete_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
    `create_by`   varchar(64) NOT NULL DEFAULT 'system' COMMENT '创建者',
    `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   varchar(64) NOT NULL DEFAULT 'system' COMMENT '修改者',
    `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`,`role_id`),
    KEY           `idx_user_id` (`user_id`),
    KEY           `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户与角色关联表';



-- =========================
-- 角色菜单关联表
-- =========================
CREATE TABLE `sys_role_menu`
(
    `id`          bigint      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `role_id`     bigint      NOT NULL COMMENT '角色ID',
    `menu_id`     bigint      NOT NULL COMMENT '菜单ID',
    `delete_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
    `create_by`   varchar(64) NOT NULL DEFAULT 'system' COMMENT '创建者',
    `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   varchar(64) NOT NULL DEFAULT 'system' COMMENT '修改者',
    `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_menu` (`role_id`,`menu_id`),
    KEY           `idx_role_id` (`role_id`),
    KEY           `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色与菜单关联表';

-- =========================
-- 角色权限点关联表
-- =========================
CREATE TABLE `sys_role_permission`
(
    `id`            bigint      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `role_id`       bigint      NOT NULL COMMENT '角色ID',
    `permission_id` bigint      NOT NULL COMMENT '权限点ID',
    `delete_flag`   tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
    `create_by`     varchar(64) NOT NULL DEFAULT 'system' COMMENT '创建者',
    `create_time`   datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`     varchar(64) NOT NULL DEFAULT 'system' COMMENT '修改者',
    `update_time`   datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_permission` (`role_id`,`permission_id`),
    KEY             `idx_role_id` (`role_id`),
    KEY             `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色与权限点关联表';

-- =========================
-- 菜单权限关联表
-- =========================
CREATE TABLE `sys_menu_permission`
(
    `id`            bigint      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `menu_id`       bigint      NOT NULL COMMENT '菜单ID（对应 sys_menu 表的主键）',
    `permission_id` bigint      NOT NULL COMMENT '权限ID（对应 sys_permission 表的主键）',
    `delete_flag`   tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除：0=未删除，1=已删除',
    `create_by`     varchar(64) NOT NULL DEFAULT 'system' COMMENT '创建者',
    `create_time`   datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`     varchar(64) NOT NULL DEFAULT 'system' COMMENT '修改者',
    `update_time`   datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_menu_permission` (`menu_id`, `permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单-权限关联表';


-- 初始化角色
INSERT INTO sys_role (id, role_name, role_code, role_description, role_status, delete_flag, create_by, create_time,
                      update_by, update_time)
VALUES (1, '超级管理员', 'super_admin', '系统超级管理员，拥有所有权限', 1, 0, 'system', NOW(), 'system', NOW()),
       (2, '管理员', 'admin', '系统管理员，拥有基础管理权限', 1, 0, 'system', NOW(), 'system', NOW());

-- 初始化用户（密码需替换为加密后的值）
INSERT INTO sys_user (id, username, password, nickname, email, phone, avatar, status, dept_id, org_id, last_login_time,
                      delete_flag, create_by, create_time, update_by, update_time)
VALUES (1, 'system', '$2a$10$abcdefghijklmnopqrstuv', '系统用户', 'system@example.com', '13800000000', '', 1, NULL,
        NULL, NULL, 0, 'system', NOW(), 'system', NOW()),
       (2, 'admin', '$2a$10$abcdefghijklmnopqrstuv', '管理员用户', 'admin@example.com', '13900000000', '', 1, NULL,
        NULL, NULL, 0, 'system', NOW(), 'system', NOW());

-- 用户角色关联
INSERT INTO sys_user_role (id, user_id, role_id, delete_flag, create_by, create_time, update_by, update_time)
VALUES (1, 1, 1, 0, 'system', NOW(), 'system', NOW()), -- system → super_admin
       (2, 2, 2, 0, 'system', NOW(), 'system', NOW());
-- admin → admin

-- 初始化菜单
INSERT INTO sys_menu (id, menu_parent_id, menu_name, menu_path, menu_component, menu_icon, menu_type, menu_permission,
                      menu_sort, menu_visible, menu_status, delete_flag, create_by, create_time, update_by, update_time)
VALUES (100, 0, '系统管理', '/system', 'Layout', 'setting', 0, '', 1, 1, 1, 0, 'system', NOW(), 'system', NOW()),
       (101, 100, '用户管理', '/system/user', 'system/user/index', 'user', 1, 'user:manage', 1, 1, 1, 0, 'system',
        NOW(), 'system', NOW()),
       (102, 100, '角色管理', '/system/role', 'system/role/index', 'team', 1, 'role:manage', 2, 1, 1, 0, 'system',
        NOW(), 'system', NOW()),
       (103, 100, '权限管理', '/system/permission', 'system/permission/index', 'lock', 1, 'permission:manage', 3, 1, 1,
        0, 'system', NOW(), 'system', NOW()),
       (104, 100, '菜单管理', '/system/menu', 'system/menu/index', 'menu', 1, 'menu:manage', 4, 1, 1, 0, 'system',
        NOW(), 'system', NOW());

-- 角色菜单关联
INSERT INTO sys_role_menu (role_id, menu_id, delete_flag, create_by, create_time, update_by, update_time)
SELECT 1, id, 0, 'system', NOW(), 'system', NOW()
FROM sys_menu
WHERE id IN (100, 101, 102, 103, 104);
INSERT INTO sys_role_menu (role_id, menu_id, delete_flag, create_by, create_time, update_by, update_time)
SELECT 2, id, 0, 'system', NOW(), 'system', NOW()
FROM sys_menu
WHERE id IN (100, 101, 102, 103, 104);

-- 初始化权限点
INSERT INTO sys_permission (id, permission_code, permission_name, permission_type, permission_status, delete_flag,
                            create_by, create_time, update_by, update_time)
VALUES (201, 'user:manage', '用户管理权限', 1, 1, 0, 'system', NOW(), 'system', NOW()),
       (202, 'role:manage', '角色管理权限', 1, 1, 0, 'system', NOW(), 'system', NOW()),
       (203, 'permission:manage', '权限管理权限', 1, 1, 0, 'system', NOW(), 'system', NOW()),
       (204, 'menu:manage', '菜单管理权限', 1, 1, 0, 'system', NOW(), 'system', NOW());

-- 角色权限关联
INSERT INTO sys_role_permission (role_id, permission_id, delete_flag, create_by, create_time, update_by, update_time)
SELECT 1, id, 0, 'system', NOW(), 'system', NOW()
FROM sys_permission;
INSERT INTO sys_role_permission (role_id, permission_id, delete_flag, create_by, create_time, update_by, update_time)
SELECT 2, id, 0, 'system', NOW(), 'system', NOW()
FROM sys_permission
WHERE id IN (201, 202, 203, 204);

-- 菜单权限关联
INSERT INTO sys_menu_permission (menu_id, permission_id, delete_flag, create_by, create_time, update_by, update_time)
VALUES (101, 201, 0, 'system', NOW(), 'system', NOW()),
       (102, 202, 0, 'system', NOW(), 'system', NOW()),
       (103, 203, 0, 'system', NOW(), 'system', NOW()),
       (104, 204, 0, 'system', NOW(), 'system', NOW());
