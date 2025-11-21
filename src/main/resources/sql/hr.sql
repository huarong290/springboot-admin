-- =========================
-- 组织表（集团/公司/事业部）
-- =========================
CREATE TABLE `sys_org` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `parent_id` bigint NOT NULL DEFAULT '0' COMMENT '上级组织ID，顶级组织为0',
  `org_name` varchar(100) NOT NULL COMMENT '组织名称',
  `org_code` varchar(50) NOT NULL COMMENT '组织编码，唯一',
  `org_type` varchar(50) DEFAULT NULL COMMENT '组织类型（集团/公司/事业部等）',
  `org_sort` int NOT NULL DEFAULT '0' COMMENT '排序值',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态：1=启用，0=禁用',
  `delete_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `create_by` varchar(64) NOT NULL DEFAULT 'system' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) NOT NULL DEFAULT 'system' COMMENT '修改者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_org_code` (`org_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='组织表';

-- =========================
-- 部门表
-- =========================
CREATE TABLE `sys_dept` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `parent_id` bigint NOT NULL DEFAULT '0' COMMENT '上级部门ID，顶级部门为0',
  `org_id` bigint NOT NULL COMMENT '所属组织ID',
  `dept_name` varchar(100) NOT NULL COMMENT '部门名称',
  `dept_code` varchar(50) NOT NULL COMMENT '部门编码，唯一',
  `dept_sort` int NOT NULL DEFAULT '0' COMMENT '排序值',
  `leader` varchar(64) DEFAULT NULL COMMENT '部门负责人',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) DEFAULT NULL COMMENT '部门邮箱',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态：1=启用，0=禁用',
  `delete_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `create_by` varchar(64) NOT NULL DEFAULT 'system' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) NOT NULL DEFAULT 'system' COMMENT '修改者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_dept_code` (`dept_code`),
  KEY `idx_org_id` (`org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- =========================
-- 用户表
-- =========================
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '用户名，唯一',
  `password` varchar(100) NOT NULL COMMENT '加密后的密码',
  `nickname` varchar(50) DEFAULT '' COMMENT '用户昵称',
  `email` varchar(100) DEFAULT '' COMMENT '邮箱地址',
  `phone` varchar(20) DEFAULT '' COMMENT '手机号',
  `avatar` varchar(255) DEFAULT '' COMMENT '用户头像URL',
  `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
  `dept_id` bigint DEFAULT NULL COMMENT '所属部门ID',
  `org_id` bigint DEFAULT NULL COMMENT '所属组织ID',
  `last_login_time` datetime DEFAULT NULL COMMENT '上次登录时间',
  `delete_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `create_by` varchar(64) NOT NULL DEFAULT 'system' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) NOT NULL DEFAULT 'system' COMMENT '修改者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_dept_id` (`dept_id`),
  KEY `idx_org_id` (`org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- =========================
-- 角色表
-- =========================
CREATE TABLE `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_name` varchar(50) NOT NULL COMMENT '角色名称',
  `role_code` varchar(50) NOT NULL COMMENT '角色编码',
  `role_description` varchar(255) DEFAULT '' COMMENT '角色描述',
  `delete_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `create_by` varchar(64) NOT NULL DEFAULT 'system' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) NOT NULL DEFAULT 'system' COMMENT '修改者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色表';

-- =========================
-- 菜单表
-- =========================
CREATE TABLE `sys_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `menu_parent_id` bigint NOT NULL DEFAULT '0' COMMENT '父菜单ID',
  `menu_name` varchar(50) NOT NULL COMMENT '菜单名称',
  `menu_path` varchar(100) DEFAULT '' COMMENT '路由路径',
  `menu_component` varchar(100) DEFAULT '' COMMENT '前端组件路径',
  `menu_icon` varchar(50) DEFAULT '' COMMENT '菜单图标',
  `menu_type` tinyint NOT NULL DEFAULT '1' COMMENT '菜单类型：0=目录，1=菜单，2=按钮',
  `menu_permission` varchar(100) DEFAULT '' COMMENT '权限标识',
  `menu_sort` int NOT NULL DEFAULT '0' COMMENT '排序值',
  `menu_visible` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否显示',
  `delete_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `create_by` varchar(64) NOT NULL DEFAULT 'system' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) NOT NULL DEFAULT 'system' COMMENT '修改者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统菜单表';

-- =========================
-- 权限点表
-- =========================
CREATE TABLE `sys_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `permission_code` varchar(100) NOT NULL COMMENT '权限编码',
  `permission_name` varchar(100) NOT NULL COMMENT '权限名称',
  `permission_type` tinyint NOT NULL DEFAULT '1' COMMENT '权限类型：1=接口权限，2=数据权限',
  `delete_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `create_by` varchar(64) NOT NULL DEFAULT 'system' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) NOT NULL DEFAULT 'system' COMMENT '修改者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_permission_code` (`permission_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统权限点表';

-- =========================
-- 用户角色关联表
-- =========================
CREATE TABLE `sys_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `delete_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `create_by` varchar(64) NOT NULL DEFAULT 'system' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) NOT NULL DEFAULT 'system' COMMENT '修改者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`,`role_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户与角色关联表';

-- =========================
-- 角色菜单关联表
-- =========================
CREATE TABLE `sys_role_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  `delete_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `create_by` varchar(64) NOT NULL DEFAULT 'system' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) NOT NULL DEFAULT 'system' COMMENT '修改者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_menu` (`role_id`,`menu_id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色与菜单关联表';
-- =========================
-- 角色权限点关联表
-- =========================
CREATE TABLE `sys_role_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `permission_id` bigint NOT NULL COMMENT '权限点ID',
  `delete_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `create_by` varchar(64) NOT NULL DEFAULT 'system' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) NOT NULL DEFAULT 'system' COMMENT '修改者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`role_id`,`permission_id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色与权限点关联表';




INSERT INTO `sys_org` (`id`, `parent_id`, `org_name`, `org_code`, `org_type`, `org_sort`, `status`)
VALUES
(1, 0, '集团总部', 'ORG001', '集团', 1, 1),
(2, 1, '子公司A', 'ORG002', '子公司', 2, 1),
(3, 1, '子公司B', 'ORG003', '子公司', 3, 1);

INSERT INTO `sys_dept` (`id`, `parent_id`, `org_id`, `dept_name`, `dept_code`, `dept_sort`, `status`)
VALUES
(1, 0, 1, '研发部', 'DEPT001', 1, 1),
(2, 0, 1, '财务部', 'DEPT002', 2, 1),
(3, 0, 2, '市场部', 'DEPT003', 1, 1),
(4, 0, 3, '销售部', 'DEPT004', 1, 1);

INSERT INTO `sys_user` (`id`, `username`, `password`, `nickname`, `email`, `phone`, `avatar`, `enabled`, `dept_id`, `org_id`)
VALUES
(1, 'admin', 'hashed_password_admin', '系统管理员', 'admin@example.com', '13800000000', '', 1, 1, 1),
(2, 'zhangsan', 'hashed_password_zhangsan', '张三', 'zhangsan@example.com', '13800000001', '', 1, 2, 1),
(3, 'lisi', 'hashed_password_lisi', '李四', 'lisi@example.com', '13800000002', '', 1, 3, 2);

INSERT INTO `sys_role` (`id`, `role_name`, `role_code`, `role_description`)
VALUES
(1, '超级管理员', 'ROLE_ADMIN', '拥有系统全部权限'),
(2, '普通用户', 'ROLE_USER', '普通业务用户'),
(3, '财务专员', 'ROLE_FINANCE', '财务相关权限');

INSERT INTO `sys_menu` (`id`, `menu_parent_id`, `menu_name`, `menu_path`, `menu_component`, `menu_icon`, `menu_type`, `menu_permission`, `menu_sort`, `menu_visible`)
VALUES
(1, 0, '系统管理', '/system', 'Layout', 'setting', 0, '', 1, 1),
(2, 1, '用户管理', '/system/user', 'UserPage', 'user', 1, 'sys:user:list', 1, 1),
(3, 1, '角色管理', '/system/role', 'RolePage', 'team', 1, 'sys:role:list', 2, 1),
(4, 1, '菜单管理', '/system/menu', 'MenuPage', 'menu', 1, 'sys:menu:list', 3, 1);

INSERT INTO `sys_permission` (`id`, `permission_code`, `permission_name`, `permission_type`)
VALUES
(1, 'sys:user:add', '新增用户', 1),
(2, 'sys:user:edit', '编辑用户', 1),
(3, 'sys:user:delete', '删除用户', 1),
(4, 'sys:role:assign', '分配角色', 1);

INSERT INTO `sys_user_role` (`id`, `user_id`, `role_id`)
VALUES
(1, 1, 1), -- admin → 超级管理员
(2, 2, 2), -- 张三 → 普通用户
(3, 3, 3); -- 李四 → 财务专员


INSERT INTO `sys_role_menu` (`id`, `role_id`, `menu_id`)
VALUES
(1, 1, 1), (2, 1, 2), (3, 1, 3), (4, 1, 4), -- 超级管理员 → 全部菜单
(5, 2, 2), -- 普通用户 → 用户管理
(6, 3, 3); -- 财务专员 → 角色管理
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`)
VALUES
(1, 1, 1), (2, 1, 2), (3, 1, 3), (4, 1, 4), -- 超级管理员 → 全部权限
(5, 2, 1), -- 普通用户 → 新增用户
(6, 3, 4); -- 财务专员 → 分配角色
