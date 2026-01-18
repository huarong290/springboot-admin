-- =========================
-- 组织表 sys_org
-- =========================
CREATE TABLE `sys_org`
(
    `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `parent_id`   BIGINT        NOT NULL DEFAULT 0 COMMENT '父组织ID，顶级组织为0',
    `parent_ids`  VARCHAR(1024) NOT NULL DEFAULT '0' COMMENT '父级ID路径，例如 0/1/3',
    `org_name`    VARCHAR(100)  NOT NULL DEFAULT '' COMMENT '组织名称',
    `org_code`    VARCHAR(64)   NOT NULL DEFAULT '' COMMENT '组织编码，唯一',
    `org_type`    VARCHAR(50)   NOT NULL DEFAULT '' COMMENT '组织类型（集团/公司/事业部等）',
    `org_sort`    INT           NOT NULL DEFAULT 0 COMMENT '排序值',
    `org_status`  TINYINT       NOT NULL DEFAULT 1 COMMENT '状态：1启用，0禁用',
    `delete_flag` TINYINT       NOT NULL DEFAULT 0 COMMENT '是否删除：0未删除，1已删除',
    `version`     INT           NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `create_by`   VARCHAR(64)   NOT NULL DEFAULT 'system' COMMENT '创建人',
    `create_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   VARCHAR(64)   NOT NULL DEFAULT 'system' COMMENT '修改人',
    `update_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_org_code` (`org_code`, `delete_flag`),
    KEY           `idx_parent_id` (`parent_id`),
    KEY           `idx_parent_ids` (`parent_ids`),
    KEY           `idx_status` (`org_status`, `delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='组织表';

-- =========================
-- 部门表 sys_dept
-- =========================
CREATE TABLE sys_dept
(
    id          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    parent_id   BIGINT        NOT NULL DEFAULT 0 COMMENT '父部门ID',
    parent_ids  VARCHAR(1024) NOT NULL DEFAULT '0' COMMENT '父级路径',
    org_id      BIGINT        NOT NULL COMMENT '所属组织ID',
    dept_name   VARCHAR(100)  NOT NULL COMMENT '部门名称',
    dept_code   VARCHAR(64)   NOT NULL COMMENT '部门编码',
    dept_sort   INT           NOT NULL DEFAULT 0 COMMENT '排序',
    leader      VARCHAR(64)   NOT NULL DEFAULT '' COMMENT '负责人',
    phone       VARCHAR(20)   NOT NULL DEFAULT '' COMMENT '手机号',
    email       VARCHAR(100)  NOT NULL DEFAULT '' COMMENT '邮箱',
    dept_status TINYINT       NOT NULL DEFAULT 1 COMMENT '状态',
    delete_flag TINYINT       NOT NULL DEFAULT 0 COMMENT '删除标志',
    version     INT           NOT NULL DEFAULT 0 COMMENT '乐观锁',
    create_by   VARCHAR(64)   NOT NULL DEFAULT 'system',
    create_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by   VARCHAR(64)   NOT NULL DEFAULT 'system',
    update_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_dept_code (dept_code, delete_flag),
    KEY         idx_org_id (org_id),
    KEY         idx_parent_id (parent_id),
    KEY         idx_parent_ids (parent_ids),
    KEY         idx_status (dept_status, delete_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';


-- =========================
-- 用户表 sys_user
-- =========================
CREATE TABLE sys_user
(
    id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    username        VARCHAR(64)  NOT NULL COMMENT '用户名',
    password        VARCHAR(100) NOT NULL COMMENT '密码',
    nickname        VARCHAR(50)  NOT NULL DEFAULT '',
    phone           VARCHAR(20)  NOT NULL DEFAULT '' COMMENT '手机号',
    email           VARCHAR(100) NOT NULL DEFAULT '' COMMENT '邮箱',
    avatar          VARCHAR(255) NOT NULL DEFAULT '' COMMENT '头像',
    user_status     TINYINT      NOT NULL DEFAULT 1 COMMENT '状态',
    dept_id         BIGINT       NOT NULL DEFAULT 0 COMMENT '所属部门ID',
    org_id          BIGINT       NOT NULL DEFAULT 0 COMMENT '所属组织ID',
    last_login_time DATETIME              DEFAULT NULL,
    delete_flag     TINYINT      NOT NULL DEFAULT 0 COMMENT '删除标志',
    version         INT          NOT NULL DEFAULT 0 COMMENT '乐观锁',
    create_by       VARCHAR(64)  NOT NULL DEFAULT 'system',
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by       VARCHAR(64)  NOT NULL DEFAULT 'system',
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username, delete_flag),
    KEY             idx_dept_id (dept_id),
    KEY             idx_status (user_status, delete_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';


-- =========================
-- 角色表 sys_role
-- =========================
CREATE TABLE sys_role
(
    id               BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    role_name        VARCHAR(50)  NOT NULL COMMENT '角色名称',
    role_code        VARCHAR(64)  NOT NULL COMMENT '角色编码',
    role_description VARCHAR(255) NOT NULL DEFAULT '' COMMENT '角色编码',
    role_status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态',
    is_builtin       TINYINT      NOT NULL DEFAULT 0 COMMENT '是否内置角色',
    delete_flag      TINYINT      NOT NULL DEFAULT 0 COMMENT '删除标志',
    version          INT          NOT NULL DEFAULT 0,
    create_by        VARCHAR(64)  NOT NULL DEFAULT 'system',
    create_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by        VARCHAR(64)  NOT NULL DEFAULT 'system',
    update_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_code (role_code, delete_flag),
    KEY              idx_status (role_status, delete_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';


-- =========================
-- 菜单表 sys_menu
-- =========================
CREATE TABLE sys_menu
(
    id             BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    parent_id      BIGINT        NOT NULL DEFAULT 0 COMMENT '父菜单ID',
    parent_ids     VARCHAR(1024) NOT NULL DEFAULT '0' COMMENT '父级路径',
    menu_name      VARCHAR(50)   NOT NULL COMMENT '菜单名称',
    menu_path      VARCHAR(100)  NOT NULL DEFAULT '',
    menu_component VARCHAR(100)  NOT NULL DEFAULT '',
    menu_icon      VARCHAR(50)   NOT NULL DEFAULT '',
    menu_type      TINYINT       NOT NULL COMMENT '0目录 1菜单 2按钮',
    menu_sort      INT           NOT NULL DEFAULT 0,
    visible        TINYINT       NOT NULL DEFAULT 1,
    menu_status    TINYINT       NOT NULL DEFAULT 1,
    delete_flag    TINYINT       NOT NULL DEFAULT 0,
    version        INT           NOT NULL DEFAULT 0,
    create_by      VARCHAR(64)   NOT NULL DEFAULT 'system',
    create_time    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by      VARCHAR(64)   NOT NULL DEFAULT 'system',
    update_time    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_parent_name (parent_id, menu_name, delete_flag),
    KEY            idx_parent_id (parent_id),
    KEY            idx_parent_ids (parent_ids),
    KEY            idx_type (menu_type),
    KEY            idx_status (menu_status, delete_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';


-- =========================
-- 权限表 sys_permission
-- =========================
CREATE TABLE `sys_permission`
(
    `id`                BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `permission_code`   VARCHAR(100) NOT NULL COMMENT '权限编码',
    `permission_name`   VARCHAR(100) NOT NULL COMMENT '权限名称',
    `permission_type`   TINYINT      NOT NULL DEFAULT 1 COMMENT '权限类型：1接口权限 2数据权限',
    `permission_status` TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1启用 0禁用',
    `delete_flag`       TINYINT      NOT NULL DEFAULT 0 COMMENT '是否删除：0未删除 1已删除',
    `version`           INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `create_by`         VARCHAR(64)  NOT NULL DEFAULT 'system' COMMENT '创建人',
    `create_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`         VARCHAR(64)  NOT NULL DEFAULT 'system' COMMENT '修改人',
    `update_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_permission_code` (`permission_code`, `delete_flag`),
    KEY                 `idx_status` (`permission_status`, `delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统权限点表';



-- =========================
-- 用户角色关联表 sys_user_role
-- =========================

CREATE TABLE `sys_user_role`
(
    `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`     BIGINT      NOT NULL COMMENT '用户ID',
    `role_id`     BIGINT      NOT NULL COMMENT '角色ID',
    `delete_flag` TINYINT     NOT NULL DEFAULT 0 COMMENT '是否删除：0未删除 1已删除',
    `version`     INT         NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `create_by`   VARCHAR(64) NOT NULL DEFAULT 'system' COMMENT '创建人',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   VARCHAR(64) NOT NULL DEFAULT 'system' COMMENT '修改人',
    `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY uk_user_role (`user_id`, `role_id`, `delete_flag`),
    KEY           `idx_user_id` (`user_id`),
    KEY           `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户与角色关联表';

-- =========================
-- 角色菜单关联表 sys_role_menu
-- =========================
CREATE TABLE `sys_role_menu`
(
    `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `role_id`     BIGINT      NOT NULL COMMENT '角色ID',
    `menu_id`     BIGINT      NOT NULL COMMENT '菜单ID',
    `delete_flag` TINYINT     NOT NULL DEFAULT 0 COMMENT '是否删除：0未删除 1已删除',
    `version`     INT         NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `create_by`   VARCHAR(64) NOT NULL DEFAULT 'system' COMMENT '创建人',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   VARCHAR(64) NOT NULL DEFAULT 'system' COMMENT '修改人',
    `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_menu` (`role_id`,`menu_id`, `delete_flag`),
    KEY           `idx_role_id` (`role_id`),
    KEY           `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色与菜单关联表';

-- =========================
-- 角色权限关联表 sys_role_permission
-- =========================
CREATE TABLE `sys_role_permission`
(
    `id`            BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `role_id`       BIGINT      NOT NULL COMMENT '角色ID',
    `permission_id` BIGINT      NOT NULL COMMENT '权限ID',
    `delete_flag`   TINYINT     NOT NULL DEFAULT 0 COMMENT '是否删除：0未删除 1已删除',
    `version`       INT         NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `create_by`     VARCHAR(64) NOT NULL DEFAULT 'system' COMMENT '创建人',
    `create_time`   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`     VARCHAR(64) NOT NULL DEFAULT 'system' COMMENT '修改人',
    `update_time`   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_permission` (`role_id`,`permission_id`, `delete_flag`),
    KEY             `idx_role_id` (`role_id`),
    KEY             `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色与权限点关联表';

-- =========================
-- 菜单权限关联表 sys_menu_permission
-- =========================
CREATE TABLE `sys_menu_permission`
(
    `id`            BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `menu_id`       BIGINT      NOT NULL COMMENT '菜单ID',
    `permission_id` BIGINT      NOT NULL COMMENT '权限ID',
    `delete_flag`   TINYINT     NOT NULL DEFAULT 0 COMMENT '是否删除：0未删除 1已删除',
    `version`       INT         NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `create_by`     VARCHAR(64) NOT NULL DEFAULT 'system' COMMENT '创建人',
    `create_time`   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`     VARCHAR(64) NOT NULL DEFAULT 'system' COMMENT '修改人',
    `update_time`   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_menu_permission` (`menu_id`, `permission_id`,`delete_flag`),
    KEY             `idx_menu_id` (`menu_id`),
    KEY             `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单与权限关联表';



-- =========================
-- 初始化角色
-- =========================
INSERT INTO sys_role (id, role_name, role_code, role_description, role_status, delete_flag, version, create_by,
                      create_time, update_by, update_time)
VALUES (1, '超级管理员', 'super_admin', '系统超级管理员，拥有所有权限', 1, 0, 0, 'system', NOW(), 'system', NOW()),
       (2, '管理员', 'admin', '系统管理员，拥有基础管理权限', 1, 0, 0, 'system', NOW(), 'system', NOW());

-- =========================
-- 初始化用户（密码需替换为加密后的值）
-- =========================
INSERT INTO sys_user (id, username, password, nickname, email, phone, avatar, status, dept_id, org_id, last_login_time,
                      delete_flag, version, create_by, create_time, update_by, update_time)
VALUES (1, 'system', '$2a$10$abcdefghijklmnopqrstuv', '系统用户', 'system@example.com', '13800000000', '', 1, NULL,
        NULL, NULL, 0, 0, 'system', NOW(), 'system', NOW()),
       (2, 'admin', '$2a$10$abcdefghijklmnopqrstuv', '管理员用户', 'admin@example.com', '13900000000', '', 1, NULL,
        NULL, NULL, 0, 0, 'system', NOW(), 'system', NOW());

-- =========================
-- 用户角色关联
-- =========================
INSERT INTO sys_user_role (id, user_id, role_id, delete_flag, version, create_by, create_time, update_by, update_time)
VALUES (1, 1, 1, 0, 0, 'system', NOW(), 'system', NOW()), -- system → super_admin
       (2, 2, 2, 0, 0, 'system', NOW(), 'system', NOW());
-- admin → admin

-- =========================
-- 初始化菜单
-- =========================
INSERT INTO sys_menu (id, menu_parent_id, menu_parent_ids, menu_name, menu_path, menu_component, menu_icon, menu_type,
                      menu_sort, menu_visible, menu_status, delete_flag, version, create_by, create_time, update_by,
                      update_time)
VALUES (100, 0, '0', '系统管理', '/system', 'Layout', 'setting', 0, 1, 1, 1, 0, 0, 'system', NOW(), 'system', NOW()),
       (101, 100, '0/100', '用户管理', '/system/user', 'system/user/index', 'user', 1, 1, 1, 1, 0, 0, 'system', NOW(),
        'system', NOW()),
       (102, 100, '0/100', '角色管理', '/system/role', 'system/role/index', 'team', 1, 2, 1, 1, 0, 0, 'system', NOW(),
        'system', NOW()),
       (103, 100, '0/100', '权限管理', '/system/permission', 'system/permission/index', 'lock', 1, 3, 1, 1, 0, 0,
        'system', NOW(), 'system', NOW()),
       (104, 100, '0/100', '菜单管理', '/system/menu', 'system/menu/index', 'menu', 1, 4, 1, 1, 0, 0, 'system', NOW(),
        'system', NOW());

-- =========================
-- 角色菜单关联
-- =========================
INSERT INTO sys_role_menu (role_id, menu_id, delete_flag, version, create_by, create_time, update_by, update_time)
SELECT 1,
       id,
       0,
       0,
       'system',
       NOW(),
       'system',
       NOW()
FROM sys_menu
WHERE id IN (100, 101, 102, 103, 104);

INSERT INTO sys_role_menu (role_id, menu_id, delete_flag, version, create_by, create_time, update_by, update_time)
SELECT 2,
       id,
       0,
       0,
       'system',
       NOW(),
       'system',
       NOW()
FROM sys_menu
WHERE id IN (100, 101, 102, 103, 104);

-- =========================
-- 初始化权限点
-- =========================
INSERT INTO sys_permission (id, permission_code, permission_name, permission_type, permission_status, delete_flag,
                            version, create_by, create_time, update_by, update_time)
VALUES (201, 'user:manage', '用户管理权限', 1, 1, 0, 0, 'system', NOW(), 'system', NOW()),
       (202, 'role:manage', '角色管理权限', 1, 1, 0, 0, 'system', NOW(), 'system', NOW()),
       (203, 'permission:manage', '权限管理权限', 1, 1, 0, 0, 'system', NOW(), 'system', NOW()),
       (204, 'menu:manage', '菜单管理权限', 1, 1, 0, 0, 'system', NOW(), 'system', NOW());

-- =========================
-- 角色权限关联
-- =========================
INSERT INTO sys_role_permission (role_id, permission_id, delete_flag, version, create_by, create_time, update_by,
                                 update_time)
SELECT 1,
       id,
       0,
       0,
       'system',
       NOW(),
       'system',
       NOW()
FROM sys_permission;

INSERT INTO sys_role_permission (role_id, permission_id, delete_flag, version, create_by, create_time, update_by,
                                 update_time)
SELECT 2,
       id,
       0,
       0,
       'system',
       NOW(),
       'system',
       NOW()
FROM sys_permission
WHERE id IN (201, 202, 203, 204);

-- =========================
-- 菜单权限关联
-- =========================
INSERT INTO sys_menu_permission (menu_id, permission_id, delete_flag, version, create_by, create_time, update_by,
                                 update_time)
VALUES (101, 201, 0, 0, 'system', NOW(), 'system', NOW()),
       (102, 202, 0, 0, 'system', NOW(), 'system', NOW()),
       (103, 203, 0, 0, 'system', NOW(), 'system', NOW()),
       (104, 204, 0, 0, 'system', NOW(), 'system', NOW());

