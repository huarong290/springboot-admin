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
  `org_status` tinyint(5) NOT NULL DEFAULT '1' COMMENT '状态：1=启用，0=禁用',
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
  `dept_status` tinyint(5) NOT NULL DEFAULT '1' COMMENT '状态：1=启用，0=禁用',
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
  `status` tinyint(5) NOT NULL DEFAULT '1' COMMENT '是否启用',
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
  `role_status` tinyint(5) NOT NULL DEFAULT '1' COMMENT '是否启用',
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
  `menu_status` tinyint(5) NOT NULL DEFAULT '1' COMMENT '是否启用',
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
  `permission_status` tinyint(5) NOT NULL DEFAULT '1' COMMENT '是否启用:1-启用 0-禁用',
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

CREATE TABLE `sys_menu_permission` (
                                       `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                       `menu_id` bigint NOT NULL COMMENT '菜单ID（对应 sys_menu 表的主键）',
                                       `permission_id` bigint NOT NULL COMMENT '权限ID（对应 sys_permission 表的主键）',
                                       `delete_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除：0=未删除，1=已删除',
                                       `create_by` varchar(64) NOT NULL DEFAULT 'system' COMMENT '创建者',
                                       `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `update_by` varchar(64) NOT NULL DEFAULT 'system' COMMENT '修改者',
                                       `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                       PRIMARY KEY (`id`),
                                       UNIQUE KEY `uk_menu_permission` (`menu_id`, `permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4  COMMENT='菜单-权限关联表';



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
(1, 'admin', '$2a$10$WWfiAZKieNynjwbyAxWSK.aAo0OfBpDYOLj03ksJV/C1hD4zwG03m', '系统管理员', 'admin@example.com', '13800000000', '', 1, 1, 1),
(2, 'zhangsan', '$2a$10$WWfiAZKieNynjwbyAxWSK.aAo0OfBpDYOLj03ksJV/C1hD4zwG03m', '张三', 'zhangsan@example.com', '13800000001', '', 1, 2, 1),
(3, 'lisi', '$2a$10$WWfiAZKieNynjwbyAxWSK.aAo0OfBpDYOLj03ksJV/C1hD4zwG03m', '李四', 'lisi@example.com', '13800000002', '', 1, 3, 2);

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

-- 用户管理权限 (id 1-3)
INSERT INTO sys_permission (id, permission_code, permission_name, permission_type, permission_status, delete_flag, create_by, create_time, update_by, update_time) VALUES
    (1, 'sys:user:add','新增用户',1,1,0,'system',NOW(),'system',NOW()),
    (2, 'sys:user:edit','编辑用户',1,1,0,'system',NOW(),'system',NOW()),
    (3, 'sys:user:delete','删除用户',1,1,0,'system',NOW(),'system',NOW());

-- 角色管理权限 (id 4-7)
INSERT INTO sys_permission (id, permission_code, permission_name, permission_type, permission_status, delete_flag, create_by, create_time, update_by, update_time) VALUES
    (4, 'sys:role:assign','分配角色',1,1,0,'system',NOW(),'system',NOW()),
    (5, 'sys:role:add','新增角色',1,1,0,'system',NOW(),'system',NOW()),
    (6, 'sys:role:edit','编辑角色',1,1,0,'system',NOW(),'system',NOW()),
    (7, 'sys:role:delete','删除角色',1,1,0,'system',NOW(),'system',NOW());

-- 菜单管理权限 (id 8-10)
INSERT INTO sys_permission (id, permission_code, permission_name, permission_type, permission_status, delete_flag, create_by, create_time, update_by, update_time) VALUES
    (8, 'sys:menu:add','新增菜单',1,1,0,'system',NOW(),'system',NOW()),
    (9, 'sys:menu:edit','编辑菜单',1,1,0,'system',NOW(),'system',NOW()),
    (10, 'sys:menu:delete','删除菜单',1,1,0,'system',NOW(),'system',NOW());

-- 权限管理权限 (id 11-13)
INSERT INTO sys_permission (id, permission_code, permission_name, permission_type, permission_status, delete_flag, create_by, create_time, update_by, update_time) VALUES
    (11, 'sys:perm:add','新增权限',1,1,0,'system',NOW(),'system',NOW()),
    (12, 'sys:perm:edit','编辑权限',1,1,0,'system',NOW(),'system',NOW()),
    (13, 'sys:perm:delete','删除权限',1,1,0,'system',NOW(),'system',NOW());

-- 部门管理权限 (id 14-16)
INSERT INTO sys_permission (id, permission_code, permission_name, permission_type, permission_status, delete_flag, create_by, create_time, update_by, update_time) VALUES
    (14, 'sys:dept:add','新增部门',1,1,0,'system',NOW(),'system',NOW()),
    (15, 'sys:dept:edit','编辑部门',1,1,0,'system',NOW(),'system',NOW()),
    (16, 'sys:dept:delete','删除部门',1,1,0,'system',NOW(),'system',NOW());

-- 组织管理权限 (id 17-19)
INSERT INTO sys_permission (id, permission_code, permission_name, permission_type, permission_status, delete_flag, create_by, create_time, update_by, update_time) VALUES
    (17, 'sys:org:add','新增组织',1,1,0,'system',NOW(),'system',NOW()),
    (18, 'sys:org:edit','编辑组织',1,1,0,'system',NOW(),'system',NOW()),
    (19, 'sys:org:delete','删除组织',1,1,0,'system',NOW(),'system',NOW());

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
-- 超级管理员 (role_id=1) 绑定所有权限 (id=1~19)
INSERT INTO sys_role_permission (id, role_id, permission_id, delete_flag, create_by, create_time, update_by, update_time) VALUES
    (1, 1, 1, 0, 'system', NOW(), 'system', NOW()),
    (2, 1, 2, 0, 'system', NOW(), 'system', NOW()),
    (3, 1, 3, 0, 'system', NOW(), 'system', NOW()),
    (4, 1, 4, 0, 'system', NOW(), 'system', NOW()),
    (5, 1, 5, 0, 'system', NOW(), 'system', NOW()),
    (6, 1, 6, 0, 'system', NOW(), 'system', NOW()),
    (7, 1, 7, 0, 'system', NOW(), 'system', NOW()),
    (8, 1, 8, 0, 'system', NOW(), 'system', NOW()),
    (9, 1, 9, 0, 'system', NOW(), 'system', NOW()),
    (10, 1, 10, 0, 'system', NOW(), 'system', NOW()),
    (11, 1, 11, 0, 'system', NOW(), 'system', NOW()),
    (12, 1, 12, 0, 'system', NOW(), 'system', NOW()),
    (13, 1, 13, 0, 'system', NOW(), 'system', NOW()),
    (14, 1, 14, 0, 'system', NOW(), 'system', NOW()),
    (15, 1, 15, 0, 'system', NOW(), 'system', NOW()),
    (16, 1, 16, 0, 'system', NOW(), 'system', NOW()),
    (17, 1, 17, 0, 'system', NOW(), 'system', NOW()),
    (18, 1, 18, 0, 'system', NOW(), 'system', NOW()),
    (19, 1, 19, 0, 'system', NOW(), 'system', NOW());


-- 用户管理 (menu_id=2)
INSERT INTO sys_menu_permission (id, menu_id, permission_id, delete_flag, create_by, create_time, update_by, update_time) VALUES
    (1, 2, 1, 0, 'system', NOW(), 'system', NOW()),
    (2, 2, 2, 0, 'system', NOW(), 'system', NOW()),
    (3, 2, 3, 0, 'system', NOW(), 'system', NOW());

-- 角色管理 (menu_id=3)
INSERT INTO sys_menu_permission (id, menu_id, permission_id, delete_flag, create_by, create_time, update_by, update_time) VALUES
    (4, 3, 4, 0, 'system', NOW(), 'system', NOW()),
    (5, 3, 5, 0, 'system', NOW(), 'system', NOW()),
    (6, 3, 6, 0, 'system', NOW(), 'system', NOW()),
    (7, 3, 7, 0, 'system', NOW(), 'system', NOW());

-- 菜单管理 (menu_id=4)
INSERT INTO sys_menu_permission (id, menu_id, permission_id, delete_flag, create_by, create_time, update_by, update_time) VALUES
    (8, 4, 8, 0, 'system', NOW(), 'system', NOW()),
    (9, 4, 9, 0, 'system', NOW(), 'system', NOW()),
    (10, 4, 10, 0, 'system', NOW(), 'system', NOW());

-- 权限管理 (menu_id=5)
INSERT INTO sys_menu_permission (id, menu_id, permission_id, delete_flag, create_by, create_time, update_by, update_time) VALUES
    (11, 5, 11, 0, 'system', NOW(), 'system', NOW()),
    (12, 5, 12, 0, 'system', NOW(), 'system', NOW()),
    (13, 5, 13, 0, 'system', NOW(), 'system', NOW());

-- 部门管理 (menu_id=6)
INSERT INTO sys_menu_permission (id, menu_id, permission_id, delete_flag, create_by, create_time, update_by, update_time) VALUES
    (14, 6, 14, 0, 'system', NOW(), 'system', NOW()),
    (15, 6, 15, 0, 'system', NOW(), 'system', NOW()),
    (16, 6, 16, 0, 'system', NOW(), 'system', NOW());

-- 组织管理 (menu_id=7)
INSERT INTO sys_menu_permission (id, menu_id, permission_id, delete_flag, create_by, create_time, update_by, update_time) VALUES
    (17, 7, 17, 0, 'system', NOW(), 'system', NOW()),
    (18, 7, 18, 0, 'system', NOW(), 'system', NOW()),
    (19, 7, 19, 0, 'system', NOW(), 'system', NOW());



DROP TABLE IF EXISTS `hr_employee`;
CREATE TABLE `hr_employee`  (
                                `id` bigint NOT NULL AUTO_INCREMENT COMMENT '员工ID',
                                `employee_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '员工编号（唯一，可修改）',
                                `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '姓名',
                                `company_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '所属公司',
                                `department` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '部门',
                                `employment_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '在职状态',
                                `is_transferred` tinyint(1) NULL DEFAULT 0 COMMENT '是否转岗',
                                `accommodation_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '住宿情况',
                                `delete_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
                                `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'admin' COMMENT '创建者',
                                `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'admin' COMMENT '修改者',
                                `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                PRIMARY KEY (`id`) USING BTREE,
                                UNIQUE INDEX `uk_employee_code`(`employee_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '员工基本信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of hr_employee
-- ----------------------------
INSERT INTO `hr_employee` VALUES (1, 'GL00055', 'alice', '平台财务中心', '产品开发01组', '在职', 0, '外宿', 0, 'admin', '2025-10-08 09:52:33', 'admin', '2025-11-09 13:05:19');

-- ----------------------------
-- Table structure for hr_exchange_rate_log
-- ----------------------------
DROP TABLE IF EXISTS `hr_exchange_rate_log`;
CREATE TABLE `hr_exchange_rate_log`  (
                                         `id` bigint NOT NULL AUTO_INCREMENT,
                                         `base_currency` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '本币',
                                         `target_currency` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '目标币',
                                         `exchange_rate` decimal(18, 8) NOT NULL DEFAULT 1.00000000 COMMENT '汇率值',
                                         `effective_date` date NULL DEFAULT NULL COMMENT '生效日期',
                                         `source` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '汇率来源（如央行、CoinMarketCap等）',
                                         `delete_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
                                         `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'admin' COMMENT '创建者',
                                         `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                         `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'admin' COMMENT '修改者',
                                         `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                         PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '汇率记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of hr_exchange_rate_log
-- ----------------------------
INSERT INTO `hr_exchange_rate_log` VALUES (1, 'USDT', '比索', 55.50000000, '2025-05-01', 'YH', 0, 'admin', '2025-10-09 01:15:39', 'admin', '2025-10-26 11:38:05');
INSERT INTO `hr_exchange_rate_log` VALUES (2, 'USDT', '泰铢', 32.40000000, '2025-05-01', 'YH', 0, 'admin', '2025-10-09 01:20:10', 'admin', '2025-10-26 11:38:26');
INSERT INTO `hr_exchange_rate_log` VALUES (3, 'USDT', '比索', 56.00000000, '2025-06-01', 'YH', 0, 'admin', '2025-10-26 11:37:56', 'admin', '2025-10-26 11:38:35');
INSERT INTO `hr_exchange_rate_log` VALUES (4, 'USDT', '泰铢', 32.10000000, '2025-06-01', 'YH', 0, 'admin', '2025-10-26 11:39:21', 'admin', '2025-10-26 11:39:21');

-- ----------------------------
-- Table structure for hr_salary_deduction
-- ----------------------------
DROP TABLE IF EXISTS `hr_salary_deduction`;
CREATE TABLE `hr_salary_deduction`  (
                                        `id` bigint NOT NULL AUTO_INCREMENT COMMENT '扣款ID',
                                        `period_id` bigint NOT NULL COMMENT '薪资周期ID',
                                        `absenteeism_deduction` decimal(10, 2) NULL DEFAULT NULL COMMENT '旷工扣款',
                                        `late_deduction` decimal(10, 2) NULL DEFAULT NULL COMMENT '迟到扣款',
                                        `utility_deduction` decimal(10, 2) NULL DEFAULT NULL COMMENT '水电网扣款',
                                        `housing_deduction` decimal(10, 2) NULL DEFAULT NULL COMMENT '外宿房补/宿舍超标扣款',
                                        `fine` decimal(10, 2) NULL DEFAULT NULL COMMENT '各类罚款(负数)',
                                        `passport_deduction` decimal(10, 2) NULL DEFAULT NULL COMMENT '护照费用代扣(负数)',
                                        `annual_leave_downgrade` decimal(10, 2) NULL DEFAULT NULL COMMENT '年假降级',
                                        `delete_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
                                        `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'admin' COMMENT '创建者',
                                        `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'admin' COMMENT '修改者',
                                        `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                        PRIMARY KEY (`id`) USING BTREE,
                                        INDEX `period_id`(`period_id` ASC) USING BTREE,
                                        CONSTRAINT `hr_salary_deduction_ibfk_1` FOREIGN KEY (`period_id`) REFERENCES `hr_salary_period` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '员工扣款项表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of hr_salary_deduction
-- ----------------------------

-- ----------------------------
-- Table structure for hr_salary_deposit
-- ----------------------------
DROP TABLE IF EXISTS `hr_salary_deposit`;
CREATE TABLE `hr_salary_deposit`  (
                                      `id` bigint NOT NULL AUTO_INCREMENT COMMENT '押金ID',
                                      `period_id` bigint NOT NULL COMMENT '薪资周期ID',
                                      `deposit_current` decimal(10, 2) NULL DEFAULT NULL COMMENT '本月押金',
                                      `deposit_total_deducted` decimal(10, 2) NULL DEFAULT NULL COMMENT '截止本月已扣押金',
                                      `deposit_current_returned` decimal(10, 2) NULL DEFAULT NULL COMMENT '本月返还',
                                      `deposit_total_returned` decimal(10, 2) NULL DEFAULT NULL COMMENT '截止本月返还',
                                      `delete_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
                                      `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'admin' COMMENT '创建者',
                                      `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'admin' COMMENT '修改者',
                                      `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                      PRIMARY KEY (`id`) USING BTREE,
                                      INDEX `period_id`(`period_id` ASC) USING BTREE,
                                      CONSTRAINT `hr_salary_deposit_ibfk_1` FOREIGN KEY (`period_id`) REFERENCES `hr_salary_period` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '押金记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of hr_salary_deposit
-- ----------------------------

-- ----------------------------
-- Table structure for hr_salary_income
-- ----------------------------
DROP TABLE IF EXISTS `hr_salary_income`;
CREATE TABLE `hr_salary_income`  (
                                     `id` bigint NOT NULL AUTO_INCREMENT COMMENT '收入ID',
                                     `period_id` bigint NOT NULL COMMENT '薪资周期ID',
                                     `base_salary` decimal(10, 2) NULL DEFAULT NULL COMMENT '本月底薪标准',
                                     `day_shift_pay` decimal(10, 2) NULL DEFAULT NULL COMMENT '日加白工资',
                                     `overtime_pay` decimal(10, 2) NULL DEFAULT NULL COMMENT '时加班工资',
                                     `kpi_bonus` decimal(10, 2) NULL DEFAULT NULL COMMENT 'KPI绩效',
                                     `performance_commission` decimal(10, 2) NULL DEFAULT NULL COMMENT '业绩提成',
                                     `agent_commission` decimal(10, 2) NULL DEFAULT NULL COMMENT '代理提成',
                                     `twelve_hour_subsidy` decimal(10, 2) NULL DEFAULT NULL COMMENT '12小时补贴',
                                     `housing_adjustment` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '外宿房补/宿舍超标扣款',
                                     `transport_subsidy` decimal(10, 2) NULL DEFAULT NULL COMMENT '车补',
                                     `full_attendance_bonus` decimal(10, 2) NULL DEFAULT NULL COMMENT '全勤奖',
                                     `holiday_bonus` decimal(10, 2) NULL DEFAULT NULL COMMENT '节日福利',
                                     `remote_subsidy` decimal(10, 2) NULL DEFAULT NULL COMMENT '远程补贴',
                                     `referral_bonus` decimal(10, 2) NULL DEFAULT NULL COMMENT '内推奖金',
                                     `travel_meal_subsidy` decimal(10, 2) NULL DEFAULT NULL COMMENT '出差餐补/住宿费',
                                     `card_reward` decimal(10, 2) NULL DEFAULT NULL COMMENT '员工卡/注册/安全卡奖励',
                                     `onboarding_reimbursement` decimal(10, 2) NULL DEFAULT NULL COMMENT '新人入职/回国费用报销',
                                     `resignation_settlement` decimal(10, 2) NULL DEFAULT NULL COMMENT '离职费用结算',
                                     `last_month_adjustment` decimal(10, 2) NULL DEFAULT NULL COMMENT '上月补发/续扣',
                                     `delete_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
                                     `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'admin' COMMENT '创建者',
                                     `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'admin' COMMENT '修改者',
                                     `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                     `employee_id` bigint NOT NULL COMMENT '员工ID',
                                     `dragon_boat_festival_gift` decimal(10, 2) NULL DEFAULT NULL COMMENT '端午节礼金',
                                     `dragon_boat_festival_double_pay` decimal(10, 2) NULL DEFAULT NULL COMMENT '端午节两倍薪资',
                                     `mid_autumn_festival_gift` decimal(10, 2) NULL DEFAULT NULL COMMENT '中秋节礼金',
                                     `mid_autumn_festival_double_pay` decimal(10, 2) NULL DEFAULT NULL COMMENT '中秋节两倍薪资',
                                     `annual_leave_bonus` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '年假奖金',
                                     PRIMARY KEY (`id`) USING BTREE,
                                     INDEX `period_id`(`period_id` ASC) USING BTREE,
                                     INDEX `employee_id`(`employee_id` ASC) USING BTREE,
                                     CONSTRAINT `hr_salary_income_ibfk_1` FOREIGN KEY (`period_id`) REFERENCES `hr_salary_period` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                     CONSTRAINT `hr_salary_income_ibfk_2` FOREIGN KEY (`employee_id`) REFERENCES `hr_employee` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '员工收入项表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of hr_salary_income
-- ----------------------------
INSERT INTO `hr_salary_income` VALUES (1, 2, 5840.00, 0.00, 0.00, 1168.00, 0.00, 0.00, 0.00, 300.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0, 'admin', '2025-10-10 23:58:24', 'admin', '2025-10-26 11:26:01', 1, 0.00, 0.00, 0.00, 0.00, 0.00);
INSERT INTO `hr_salary_income` VALUES (2, 1, 5840.00, 0.00, 0.00, 2920.00, 0.00, 0.00, 0.00, 300.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0, 'admin', '2025-10-26 11:21:51', 'admin', '2025-10-26 11:26:42', 1, 180.00, 376.77, 0.00, 0.00, 0.00);
INSERT INTO `hr_salary_income` VALUES (3, 3, 5840.00, 0.00, 0.00, 1168.00, 0.00, 0.00, 0.00, 300.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0, 'admin', '2025-10-26 11:30:43', 'admin', '2025-10-26 11:30:43', 1, 0.00, 0.00, 0.00, 0.00, 0.00);
INSERT INTO `hr_salary_income` VALUES (4, 4, 5840.00, 0.00, 0.00, 2920.00, 0.00, 0.00, 0.00, 300.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0, 'admin', '2025-10-26 14:09:29', 'admin', '2025-10-26 14:09:53', 1, 0.00, 0.00, 0.00, 0.00, 0.00);
INSERT INTO `hr_salary_income` VALUES (5, 5, 5840.00, 0.00, 0.00, 1168.00, 0.00, 0.00, 0.00, 300.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 859.05, 0, 'admin', '2025-10-26 14:19:37', 'admin', '2025-10-26 15:10:05', 1, 0.00, 0.00, 0.00, 0.00, 0.00);
INSERT INTO `hr_salary_income` VALUES (6, 6, 7008.00, 0.00, 0.00, 1401.60, 0.00, 0.00, 0.00, 300.00, 0.00, 70.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0, 'admin', '2025-10-26 15:14:28', 'admin', '2025-11-07 22:41:31', 1, 0.00, 0.00, 0.00, 0.00, 0.00);
INSERT INTO `hr_salary_income` VALUES (7, 7, 7008.00, 0.00, 0.00, 3504.00, 0.00, 0.00, 0.00, 300.00, 0.00, 70.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0, 'admin', '2025-11-07 22:40:54', 'admin', '2025-11-07 22:40:54', 1, 0.00, 452.13, 180.00, 0.00, 0.00);

-- ----------------------------
-- Table structure for hr_salary_period
-- ----------------------------
DROP TABLE IF EXISTS `hr_salary_period`;
CREATE TABLE `hr_salary_period`  (
                                     `id` bigint NOT NULL AUTO_INCREMENT COMMENT '周期ID',
                                     `employee_id` bigint NOT NULL COMMENT '员工ID',
                                     `work_month` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '在岗月份',
                                     `settlement_month` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '结算月份（格式：YYYYMM）',
                                     `start_date` date NULL DEFAULT NULL COMMENT '开始日期',
                                     `end_date` date NULL DEFAULT NULL COMMENT '结束日期',
                                     `month_days` int NULL DEFAULT NULL COMMENT '月天数',
                                     `attendance_days` int NULL DEFAULT NULL COMMENT '出勤天数',
                                     `delete_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
                                     `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'admin' COMMENT '创建者',
                                     `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'admin' COMMENT '修改者',
                                     `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                     PRIMARY KEY (`id`) USING BTREE,
                                     INDEX `employee_id`(`employee_id` ASC) USING BTREE,
                                     CONSTRAINT `hr_salary_period_ibfk_1` FOREIGN KEY (`employee_id`) REFERENCES `hr_employee` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '薪资周期信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of hr_salary_period
-- ----------------------------
INSERT INTO `hr_salary_period` VALUES (1, 1, '13', '2025-05', '2025-05-01', '2025-05-31', 31, 31, 0, 'admin', '2025-10-09 23:00:33', 'admin', '2025-10-09 23:00:33');
INSERT INTO `hr_salary_period` VALUES (2, 1, '12', '2025-04', '2025-04-01', '2025-04-30', 30, 30, 0, 'admin', '2025-10-09 23:08:47', 'admin', '2025-10-09 23:08:47');
INSERT INTO `hr_salary_period` VALUES (3, 1, '14', '2025-06', '2025-06-01', '2025-06-30', 30, 30, 0, 'admin', '2025-10-09 23:24:23', 'admin', '2025-10-09 23:24:23');
INSERT INTO `hr_salary_period` VALUES (4, 1, '15', '2025-07', '2025-07-01', '2025-07-31', 31, 31, 0, 'admin', '2025-10-26 14:04:10', 'admin', '2025-10-26 14:04:10');
INSERT INTO `hr_salary_period` VALUES (5, 1, '16', '2025-08', '2025-08-01', '2025-08-31', 31, 31, 0, 'admin', '2025-10-26 14:15:28', 'admin', '2025-10-26 14:15:28');
INSERT INTO `hr_salary_period` VALUES (6, 1, '17', '2025-09', '2025-09-01', '2025-09-30', 30, 30, 0, 'admin', '2025-10-26 15:12:52', 'admin', '2025-10-26 15:12:52');
INSERT INTO `hr_salary_period` VALUES (7, 1, '18', '2025-10', '2025-10-01', '2025-10-31', 31, 31, 0, 'admin', '2025-11-07 22:37:37', 'admin', '2025-11-07 22:37:37');

-- ----------------------------
-- Table structure for hr_salary_summary
-- ----------------------------
DROP TABLE IF EXISTS `hr_salary_summary`;
CREATE TABLE `hr_salary_summary`  (
                                      `id` bigint NOT NULL AUTO_INCREMENT COMMENT '汇总ID',
                                      `period_id` bigint NOT NULL COMMENT '薪资周期ID',
                                      `currency` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'CNY' COMMENT '结算币种',
                                      `exchange_rate` decimal(10, 4) NULL DEFAULT 1.0000 COMMENT '汇率（本币兑目标币）',
                                      `salary_subtotal` decimal(10, 2) NULL DEFAULT NULL COMMENT '应发小计（本币）',
                                      `salary_total` decimal(10, 2) NULL DEFAULT NULL COMMENT '结算薪资（本币）',
                                      `salary_converted` decimal(10, 2) NULL DEFAULT NULL COMMENT '结算薪资（目标币）',
                                      `salary_rmb` decimal(10, 2) NULL DEFAULT NULL COMMENT '人民币金额（如需展示）',
                                      `salary_usdt` decimal(10, 2) NULL DEFAULT NULL COMMENT 'USDT金额（如需展示）',
                                      `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注',
                                      `delete_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
                                      `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'admin' COMMENT '创建者',
                                      `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'admin' COMMENT '修改者',
                                      `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                      PRIMARY KEY (`id`) USING BTREE,
                                      INDEX `period_id`(`period_id` ASC) USING BTREE,
                                      CONSTRAINT `hr_salary_summary_ibfk_1` FOREIGN KEY (`period_id`) REFERENCES `hr_salary_period` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '薪资汇总与结算表（含币种与汇率）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of hr_salary_summary
-- ----------------------------

-- ----------------------------
-- Table structure for sys_dict_item
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_item`;
CREATE TABLE `sys_dict_item`  (
                                  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                  `dict_type_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '所属字典类型code',
                                  `dict_item_label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '字典项标签（如 男、女）',
                                  `dict_item_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '字典项值（如 1、0）',
                                  `sort` int NOT NULL DEFAULT 0 COMMENT '排序值，越小越靠前',
                                  `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用，0表示启用',
                                  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '备注说明',
                                  `delete_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
                                  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'admin' COMMENT '创建者',
                                  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'admin' COMMENT '修改者',
                                  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                  PRIMARY KEY (`id`) USING BTREE,
                                  UNIQUE INDEX `uk_dict_type_value`(`dict_type_code` ASC, `dict_item_value` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统字典项表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict_item
-- ----------------------------
INSERT INTO `sys_dict_item` VALUES (1, 'gender', '男', '1', 1, 0, '', 0, 'admin', '2025-08-16 20:25:34', 'admin', '2025-09-30 10:49:19');
INSERT INTO `sys_dict_item` VALUES (2, 'gender', '女', '0', 3, 0, '', 0, 'admin', '2025-08-16 20:25:34', 'admin', '2025-09-30 10:49:36');
INSERT INTO `sys_dict_item` VALUES (3, 'status', '启用', '1', 1, 1, '', 0, 'admin', '2025-08-16 20:25:49', 'admin', '2025-08-16 20:25:49');
INSERT INTO `sys_dict_item` VALUES (4, 'status', '禁用', '0', 2, 1, '', 0, 'admin', '2025-08-16 20:25:49', 'admin', '2025-08-16 20:25:49');

-- ----------------------------
-- Table structure for sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type`  (
                                  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                  `dict_type_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '字典类型编码（如 gender、status）',
                                  `dict_type_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '字典类型名称（如 性别、状态）',
                                  `status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否启用,0 表示启用',
                                  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '备注说明',
                                  `delete_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
                                  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'admin' COMMENT '创建者',
                                  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'admin' COMMENT '修改者',
                                  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                  PRIMARY KEY (`id`) USING BTREE,
                                  UNIQUE INDEX `uk_dict_code`(`dict_type_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统字典类型表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict_type
-- ----------------------------
INSERT INTO `sys_dict_type` VALUES (1, 'gender', '性别', 0, '性别字典', 0, 'admin', '2025-08-16 20:25:16', 'admin', '2025-09-30 10:50:31');
INSERT INTO `sys_dict_type` VALUES (2, 'status', '状态', 0, '通用状态字典', 0, 'admin', '2025-08-16 20:25:16', 'admin', '2025-08-16 20:25:16');
INSERT INTO `sys_dict_type` VALUES (3, 'channel', '渠道1', 0, '渠道字典1', 0, 'admin', '2025-09-19 09:50:56', 'admin', '2025-09-19 10:11:59');
