-- =====================================================================
-- 系统数据库表设计（高并发 / 大组织 / RBAC + 数据权限）
-- 设计原则：
-- 1. 主键统一 BIGINT 自增
-- 2. 所有表统一包含逻辑删除、乐观锁、审计字段
-- 3. 唯一性使用 UNIQUE KEY
-- 4. 组织 / 部门 / 菜单层级使用闭包表
-- 5. 数据权限在 SQL 层生效（org_id / dept_id）
-- =====================================================================

-- =========================
-- 1. 组织表 sys_org
-- =========================
CREATE TABLE sys_org
(
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID，自增唯一标识每个组织',
    parent_id   BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '父组织ID，顶级为0',
    org_name    VARCHAR(100)    NOT NULL DEFAULT '' COMMENT '组织名称',
    org_code    VARCHAR(64)     NOT NULL DEFAULT '' COMMENT '组织唯一编码',
    org_type    VARCHAR(50)     NOT NULL DEFAULT '' COMMENT '组织类型（集团/公司/事业部等）',
    org_sort    INT              NOT NULL DEFAULT 0 COMMENT '排序号，数字越小越靠前',
    org_status  TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态：1启用 0禁用',
    delete_flag TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
    version     BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    create_by   VARCHAR(64)     NOT NULL DEFAULT 'system' COMMENT '创建人',
    create_time TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by   VARCHAR(64)     NOT NULL DEFAULT 'system' COMMENT '更新人',
    update_time TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_org_code (org_code, delete_flag) COMMENT '组织编码唯一约束，防止重复',
    KEY idx_parent_del (parent_id, delete_flag) COMMENT '索引：父组织+逻辑删除，快速查询子组织',
    KEY idx_status (org_status, delete_flag) COMMENT '索引：状态+逻辑删除，用于启用组织筛选'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='组织表（高并发版，仅存自身信息）';

-- =========================
-- 2. 组织闭包表 sys_org_closure
-- =========================
CREATE TABLE sys_org_closure
(
    id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    ancestor_id   BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '祖先组织ID',
    descendant_id BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '后代组织ID',
    depth         INT             NOT NULL DEFAULT 0 COMMENT '层级深度：1自身 2子级 3孙级',
    delete_flag   TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    version       BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    create_by     VARCHAR(64)    NOT NULL DEFAULT 'system' COMMENT '创建人',
    create_time   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by     VARCHAR(64)    NOT NULL DEFAULT 'system' COMMENT '更新人',
    update_time   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_org_closure (ancestor_id, descendant_id, delete_flag) COMMENT '祖先-后代唯一，防止重复',
    KEY idx_descendant_id (descendant_id) COMMENT '索引：快速查询指定组织的所有祖先'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='组织层级闭包表';

-- =========================
-- 3. 部门表 sys_dept
-- =========================
CREATE TABLE sys_dept
(
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    parent_id   BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '父部门ID，顶级为0',
    org_id      BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '所属组织ID',
    dept_name   VARCHAR(100)    NOT NULL DEFAULT '' COMMENT '部门名称',
    dept_code   VARCHAR(64)     NOT NULL DEFAULT '' COMMENT '部门唯一编码',
    dept_sort   INT             NOT NULL DEFAULT 0 COMMENT '排序号',
    dept_status TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态：1启用 0禁用',
    delete_flag TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    version     BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    create_by   VARCHAR(64)    NOT NULL DEFAULT 'system' COMMENT '创建人',
    create_time TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by   VARCHAR(64)    NOT NULL DEFAULT 'system' COMMENT '更新人',
    update_time TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_dept_code (dept_code, delete_flag) COMMENT '部门编码唯一',
    KEY idx_org_del (org_id, delete_flag) COMMENT '索引：按组织查询部门',
    KEY idx_parent_del (parent_id, delete_flag) COMMENT '索引：按父部门查询子部门'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- =========================
-- 4. 部门闭包表 sys_dept_closure
-- =========================
CREATE TABLE sys_dept_closure
(
    id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    ancestor_id   BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '祖先部门ID',
    descendant_id BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '后代部门ID',
    depth         INT             NOT NULL DEFAULT 0 COMMENT '层级深度',
    delete_flag   TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    version       BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    create_by     VARCHAR(64)    NOT NULL DEFAULT 'system' COMMENT '创建人',
    create_time   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by     VARCHAR(64)    NOT NULL DEFAULT 'system' COMMENT '更新人',
    update_time   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_dept_closure (ancestor_id, descendant_id, delete_flag) COMMENT '祖先-后代唯一约束',
    KEY idx_descendant_id (descendant_id) COMMENT '索引：快速查询部门祖先'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门层级闭包表';

-- =========================
-- 5. 用户表 sys_user
-- =========================
CREATE TABLE sys_user
(
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    tenant_id       BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '租户ID，多租户场景使用',
    username        VARCHAR(64)     NOT NULL DEFAULT '' COMMENT '登录用户名',
    password        VARCHAR(100)    NOT NULL DEFAULT '' COMMENT '登录密码（加密）',
    nickname        VARCHAR(50)     NOT NULL DEFAULT '' COMMENT '用户昵称',
    phone           VARCHAR(20)     NOT NULL DEFAULT '' COMMENT '手机号',
    email           VARCHAR(100)    NOT NULL DEFAULT '' COMMENT '邮箱',
    avatar          VARCHAR(255)    NOT NULL DEFAULT '' COMMENT '头像URL',
    org_id          BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '所属组织ID',
    dept_id         BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '所属部门ID',
    user_status     TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态：1启用 0禁用',
    last_login_time DATETIME        NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '最后登录时间',
    delete_flag     TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    version         BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    create_by       VARCHAR(64)     NOT NULL DEFAULT 'system' COMMENT '创建人',
    create_time     TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       VARCHAR(64)     NOT NULL DEFAULT 'system' COMMENT '更新人',
    update_time     TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username, delete_flag) COMMENT '用户名唯一约束',
    KEY idx_org_del (org_id, delete_flag) COMMENT '索引：按组织查询用户',
    KEY idx_dept_id (dept_id) COMMENT '索引：按部门查询用户'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- =========================
-- 6. 角色表 sys_role
-- =========================
CREATE TABLE sys_role
(
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    role_name       VARCHAR(50)     NOT NULL DEFAULT '' COMMENT '角色名称',
    role_code       VARCHAR(64)     NOT NULL DEFAULT '' COMMENT '角色唯一编码',
    role_status     TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态：1启用 0禁用',
    role_description VARCHAR(255)   NOT NULL DEFAULT '' COMMENT '角色描述',
    is_builtin      TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否内置角色：1是 0否',
    delete_flag     TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    version         BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    create_by       VARCHAR(64)     NOT NULL DEFAULT 'system' COMMENT '创建人',
    create_time     TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       VARCHAR(64)     NOT NULL DEFAULT 'system' COMMENT '更新人',
    update_time     TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_code (role_code, delete_flag) COMMENT '角色编码唯一'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- =========================
-- 7. 菜单表 sys_menu
-- =========================
CREATE TABLE sys_menu
(
    id             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID，自增唯一标识每个菜单',
    menu_parent_id      BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '父菜单ID，顶级菜单为0',
    menu_name      VARCHAR(50)     NOT NULL COMMENT '菜单名称，用于显示和识别菜单',
    menu_path      VARCHAR(100)    NOT NULL DEFAULT '' COMMENT '菜单路由路径，前端页面访问路径',
    menu_component VARCHAR(100)    NOT NULL DEFAULT '' COMMENT '菜单对应前端组件路径或组件名',
    menu_icon      VARCHAR(50)     NOT NULL DEFAULT '' COMMENT '菜单图标，用于前端显示',
    menu_type      TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '菜单类型：1目录 2菜单 3按钮，用于前端区分',
    menu_sort      INT             NOT NULL DEFAULT 0 COMMENT '菜单排序值，数字越小越靠前',
    visible        TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '菜单是否可见：1可见 0隐藏，前端渲染控制',
    menu_status    TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '菜单状态：1启用 0禁用，控制权限访问',
    delete_flag    TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '逻辑删除标志：0未删除 1已删除',
    version        BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁版本号，用于并发控制',
    create_by      VARCHAR(64)     NOT NULL DEFAULT 'system' COMMENT '创建人',
    create_time    TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by      VARCHAR(64)     NOT NULL DEFAULT 'system' COMMENT '最后更新人',
    update_time    TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_parent_name (menu_parent_id, menu_name, delete_flag) COMMENT '父菜单+菜单名称唯一，防止同级重复',
    UNIQUE KEY uk_menu_path (menu_path, delete_flag) COMMENT '菜单路径唯一，避免路由冲突',
    KEY idx_parent_id (menu_parent_id) COMMENT '索引：按父菜单ID查询，快速获取子菜单',
    KEY idx_type (menu_type) COMMENT '索引：按菜单类型查询，快速过滤目录/菜单/按钮',
    KEY idx_status (menu_status, delete_flag) COMMENT '联合索引：按菜单状态和删除标志查询，优化启用菜单筛选'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统菜单表，存储前端菜单和权限路由';

-- =========================
-- 8. 菜单闭包表 sys_menu_closure
-- =========================
CREATE TABLE sys_menu_closure
(
    id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    ancestor_id   BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '祖先菜单ID',
    descendant_id BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '后代菜单ID',
    depth         INT             NOT NULL DEFAULT 0 COMMENT '层级深度：1自身 2子级 3孙级',
    delete_flag   TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '逻辑删除标志',
    version       BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    create_by     VARCHAR(64)     NOT NULL DEFAULT 'system' COMMENT '创建人',
    create_time   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by     VARCHAR(64)     NOT NULL DEFAULT 'system' COMMENT '最后更新人',
    update_time   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_menu_closure (ancestor_id, descendant_id, delete_flag) COMMENT '祖先-后代菜单唯一约束',
    KEY idx_descendant_id (descendant_id) COMMENT '索引：快速查询菜单祖先',
    KEY idx_ancestor_id (ancestor_id) COMMENT '索引：快速查询菜单子菜单'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单层级闭包表';

-- =========================
-- 9. 权限表 sys_permission
-- =========================
CREATE TABLE sys_permission
(
    id                BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    permission_code   VARCHAR(100) NOT NULL DEFAULT '' COMMENT '权限编码（唯一）',
    permission_name   VARCHAR(100) NOT NULL DEFAULT '' COMMENT '权限名称',
    permission_type   TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '权限类型：1接口 2按钮 3数据',
    permission_status TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态：1启用 0禁用',
    delete_flag       TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '逻辑删除标志',
    version           BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    create_by         VARCHAR(64)  NOT NULL DEFAULT 'system' COMMENT '创建人',
    create_time       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by         VARCHAR(64)  NOT NULL DEFAULT 'system' COMMENT '最后更新人',
    update_time       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_permission_code (permission_code, delete_flag) COMMENT '权限编码唯一约束'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统权限点表';

-- =========================
-- 10. 用户-角色关联表 sys_user_role
-- =========================
CREATE TABLE sys_user_role
(
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id     BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '用户ID',
    role_id     BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '角色ID',
    delete_flag TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '逻辑删除标志',
    version     BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    create_by   VARCHAR(64)     NOT NULL DEFAULT 'system' COMMENT '创建人',
    create_time TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by   VARCHAR(64)     NOT NULL DEFAULT 'system' COMMENT '最后更新人',
    update_time TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_role (user_id, role_id, delete_flag),
    KEY idx_role_id_del (role_id, delete_flag),
    KEY idx_user_id_del (user_id, delete_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户-角色关联表';

-- =========================
-- 11. 角色-菜单关联表 sys_role_menu
-- =========================
CREATE TABLE sys_role_menu
(
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    role_id     BIGINT UNSIGNED NOT NULL COMMENT '角色ID',
    menu_id     BIGINT UNSIGNED NOT NULL COMMENT '菜单ID',
    delete_flag TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '逻辑删除标志',
    version     BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    create_by   VARCHAR(64)     NOT NULL DEFAULT 'system' COMMENT '创建人',
    create_time TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by   VARCHAR(64)     NOT NULL DEFAULT 'system' COMMENT '最后更新人',
    update_time TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_menu (role_id, menu_id, delete_flag),
    KEY idx_role_del (role_id, delete_flag),
    KEY idx_menu_del (menu_id, delete_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色与菜单关联表';

-- =========================
-- 12. 角色-权限关联表 sys_role_permission
-- =========================
CREATE TABLE sys_role_permission
(
    id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    role_id       BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '角色ID',
    permission_id BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '权限ID',
    delete_flag   TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '逻辑删除标志',
    version       BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    create_by     VARCHAR(64)     NOT NULL DEFAULT 'system' COMMENT '创建人',
    create_time   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by     VARCHAR(64)     NOT NULL DEFAULT 'system' COMMENT '最后更新人',
    update_time   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_permission (role_id, permission_id, delete_flag),
    KEY idx_role_del (role_id, delete_flag),
    KEY idx_permission_del (permission_id, delete_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色-权限关联表';

-- =========================
-- 13. 菜单-权限关联表 sys_menu_permission
-- =========================
CREATE TABLE sys_menu_permission
(
    id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    menu_id       BIGINT UNSIGNED NOT NULL COMMENT '菜单ID',
    permission_id BIGINT UNSIGNED NOT NULL COMMENT '权限ID',
    delete_flag   TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '逻辑删除标志',
    version       BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    create_by     VARCHAR(64)     NOT NULL DEFAULT 'system' COMMENT '创建人',
    create_time   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by     VARCHAR(64)     NOT NULL DEFAULT 'system' COMMENT '最后更新人',
    update_time   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_menu_permission (menu_id, permission_id, delete_flag),
    KEY idx_menu_del (menu_id, delete_flag),
    KEY idx_permission_del (permission_id, delete_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单与权限关联表';

-- =========================
-- 14. 数据权限范围定义表 sys_data_scope
-- =========================
CREATE TABLE sys_data_scope
(
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    scope_code  VARCHAR(50)  NOT NULL DEFAULT '' COMMENT '范围编码（ALL/ORG/ORG_AND_CHILD/DEPT/CUSTOM）',
    scope_name  VARCHAR(100) NOT NULL DEFAULT '' COMMENT '范围名称',
    scope_type  TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '范围类型：1-全部 2-本组织 3-本组织及下级 4-本部门 5-自定义',
    scope_description VARCHAR(255) NOT NULL DEFAULT '' COMMENT '范围描述',
    scope_status TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态：1启用 0禁用',
    delete_flag TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '删除标志',
    version     BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    create_by   VARCHAR(64) NOT NULL DEFAULT 'system' COMMENT '创建人',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by   VARCHAR(64) NOT NULL DEFAULT 'system' COMMENT '最后更新人',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_scope_code (scope_code, delete_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据权限范围定义表';

-- =========================
-- 15. 角色-数据权限关联表 sys_role_data_scope
-- =========================
CREATE TABLE sys_role_data_scope
(
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    role_id     BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '角色ID',
    scope_id    BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '数据权限ID',
    delete_flag TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '逻辑删除标志',
    version     BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    create_by   VARCHAR(64) NOT NULL DEFAULT 'system' COMMENT '创建人',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by   VARCHAR(64) NOT NULL DEFAULT 'system' COMMENT '最后更新人',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_scope (role_id, scope_id, delete_flag),
    KEY idx_role_del (role_id, delete_flag),
    KEY idx_scope_del (scope_id, delete_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色-数据权限范围关联表';

-- =========================
-- 16. 角色自定义组织数据权限表 sys_role_custom_org
-- =========================
CREATE TABLE sys_role_custom_org
(
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    role_id     BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '角色ID',
    org_id      BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '组织ID',
    delete_flag TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '逻辑删除标志',
    version     BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    create_by   VARCHAR(64) NOT NULL DEFAULT 'system' COMMENT '创建人',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by   VARCHAR(64) NOT NULL DEFAULT 'system' COMMENT '最后更新人',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_org (role_id, org_id, delete_flag),
    KEY idx_role_del (role_id, delete_flag),
    KEY idx_org_del (org_id, delete_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色自定义组织数据权限表';

-- =========================
-- 17. 角色自定义部门数据权限表 sys_role_custom_dept
-- =========================
CREATE TABLE sys_role_custom_dept
(
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    role_id     BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '角色ID',
    dept_id     BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '部门ID',
    delete_flag TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '逻辑删除标志',
    version     BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    create_by   VARCHAR(64) NOT NULL DEFAULT 'system' COMMENT '创建人',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by   VARCHAR(64) NOT NULL DEFAULT 'system' COMMENT '最后更新人',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_dept (role_id, dept_id, delete_flag),
    KEY idx_role_del (role_id, delete_flag),
    KEY idx_dept_del (dept_id, delete_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色自定义部门数据权限表';


-- =========================
-- 18. 初始化数据
-- =========================
-- =========================
-- 18.1 组织初始化数据
-- =========================
INSERT INTO sys_org (id, org_name, org_code, org_type, org_status, org_sort, delete_flag, version, create_by, create_time, update_by, update_time)
VALUES
(1, '集团总部', 'ORG_ROOT', '集团', 1, 1, 0, 0, 'system', NOW(), 'system', NOW()),
(2, '华东分公司', 'ORG_EAST', '公司', 1, 1, 0, 0, 'system', NOW(), 'system', NOW()),
(3, '华南分公司', 'ORG_SOUTH', '公司', 1, 2, 0, 0, 'system', NOW(), 'system', NOW());

-- =========================
-- 18.2 组织闭包表初始化
-- =========================
INSERT INTO sys_org_closure (ancestor_id, descendant_id, depth, delete_flag, version, create_by, create_time, update_by, update_time)
VALUES
(1,1,0,0,0,'system',NOW(),'system',NOW()),
(1,2,1,0,0,'system',NOW(),'system',NOW()),
(1,3,1,0,0,'system',NOW(),'system',NOW()),
(2,2,0,0,0,'system',NOW(),'system',NOW()),
(3,3,0,0,0,'system',NOW(),'system',NOW());

-- =========================
-- 18.3 部门初始化数据
-- =========================
INSERT INTO sys_dept (id, org_id, dept_name, dept_code, dept_status, dept_sort, delete_flag, version, create_by, create_time, update_by, update_time)
VALUES
(1, 1, '信息技术部', 'DEPT_IT', 1, 1, 0, 0, 'system', NOW(), 'system', NOW()),
(2, 2, '研发部', 'DEPT_RD', 1, 1, 0, 0, 'system', NOW(), 'system', NOW()),
(3, 3, '市场部', 'DEPT_MK', 1, 1, 0, 0, 'system', NOW(), 'system', NOW());

-- =========================
-- 18.4 部门闭包表初始化
-- =========================
INSERT INTO sys_dept_closure (ancestor_id, descendant_id, depth, delete_flag, version, create_by, create_time, update_by, update_time)
VALUES
(1,1,0,0,0,'system',NOW(),'system',NOW()),
(2,2,0,0,0,'system',NOW(),'system',NOW()),
(3,3,0,0,0,'system',NOW(),'system',NOW());

-- =========================
-- 18.5 用户初始化数据（密码示例为明文，可替换为加密）
-- =========================
INSERT INTO sys_user (id, username, password, nickname, phone, email, avatar, user_status, dept_id, org_id, last_login_time, delete_flag, version, create_by, create_time, update_by, update_time)
VALUES
(1, 'admin', 'admin123', '超级管理员', '13800000000', 'admin@test.com', '', 1, 1, 1, '1970-01-01 00:00:00', 0, 0, 'system', NOW(), 'system', NOW()),
(2, 'zhangsan', '123456', '张三', '13800000001', 'zhangsan@test.com', '', 1, 2, 2, '1970-01-01 00:00:00', 0, 0, 'system', NOW(), 'system', NOW());

-- =========================
-- 18.6 角色初始化数据
-- =========================
INSERT INTO sys_role (id, role_name, role_code, role_description, role_status, is_builtin, delete_flag, version, create_by, create_time, update_by, update_time)
VALUES
(1, '超级管理员', 'SUPER_ADMIN', '系统内置超级管理员', 1, 1, 0, 0, 'system', NOW(), 'system', NOW()),
(2, '普通用户', 'NORMAL_USER', '普通业务用户', 1, 0, 0, 0, 'system', NOW(), 'system', NOW());

-- =========================
-- 18.7 用户-角色关联初始化
-- =========================
INSERT INTO sys_user_role (user_id, role_id, delete_flag, version, create_by, create_time, update_by, update_time)
VALUES
(1,1,0,0,'system',NOW(),'system',NOW()),
(2,2,0,0,'system',NOW(),'system',NOW());

-- =========================
-- 18.8 菜单初始化数据
-- =========================
INSERT INTO sys_menu (id, menu_parent_id, menu_name, menu_path, menu_component, menu_icon, menu_type, menu_sort, visible, menu_status, delete_flag, version, create_by, create_time, update_by, update_time)
VALUES
(1,0,'系统管理','/system','','setting',1,1,1,1,0,0,'system',NOW(),'system',NOW()),
(2,1,'用户管理','/system/user','system/user/index','user',2,1,1,1,0,0,'system',NOW(),'system',NOW());

-- =========================
-- 18.9 菜单闭包表初始化
-- =========================
INSERT INTO sys_menu_closure (ancestor_id, descendant_id, depth, delete_flag, version, create_by, create_time, update_by, update_time)
VALUES
(1,1,0,0,0,'system',NOW(),'system',NOW()),
(1,2,1,0,0,'system',NOW(),'system',NOW()),
(2,2,0,0,0,'system',NOW(),'system',NOW());

-- =========================
-- 18.10 权限初始化数据
-- =========================
INSERT INTO sys_permission (id, permission_code, permission_name, permission_type, permission_status, delete_flag, version, create_by, create_time, update_by, update_time)
VALUES
(1,'USER_VIEW','查看用户',1,1,0,0,'system',NOW(),'system',NOW()),
(2,'USER_EDIT','编辑用户',2,1,0,0,'system',NOW(),'system',NOW());

-- =========================
-- 18.11 角色-菜单关联
-- =========================
INSERT INTO sys_role_menu (role_id, menu_id, delete_flag, version, create_by, create_time, update_by, update_time)
VALUES
(1,1,0,0,'system',NOW(),'system',NOW()),
(1,2,0,0,'system',NOW(),'system',NOW());

-- =========================
-- 18.12 角色-权限关联
-- =========================
INSERT INTO sys_role_permission (role_id, permission_id, delete_flag, version, create_by, create_time, update_by, update_time)
VALUES
(1,1,0,0,'system',NOW(),'system',NOW()),
(1,2,0,0,'system',NOW(),'system',NOW());

-- =========================
-- 18.13 菜单-权限关联
-- =========================
INSERT INTO sys_menu_permission (menu_id, permission_id, delete_flag, version, create_by, create_time, update_by, update_time)
VALUES
(2,1,0,0,'system',NOW(),'system',NOW()),
(2,2,0,0,'system',NOW(),'system',NOW());

-- =========================
-- 18.14 数据权限范围初始化
-- =========================
INSERT INTO sys_data_scope (id, scope_code, scope_name, scope_type, scope_description, scope_status, delete_flag, version, create_by, create_time, update_by, update_time)
VALUES
(1,'ALL','全部数据',1,'可访问所有组织数据',1,0,0,'system',NOW(),'system',NOW()),
(2,'ORG','本组织',2,'仅当前组织',1,0,0,'system',NOW(),'system',NOW()),
(3,'ORG_AND_CHILD','本组织及下级',3,'当前组织及其子组织数据',1,0,0,'system',NOW(),'system',NOW()),
(4,'DEPT','本部门',4,'仅当前部门数据',1,0,0,'system',NOW(),'system',NOW()),
(5,'CUSTOM','自定义',5,'自定义范围',1,0,0,'system',NOW(),'system',NOW());

-- =========================
-- 18.15 角色-数据权限关联初始化
-- =========================
INSERT INTO sys_role_data_scope (role_id, scope_id, delete_flag, version, create_by, create_time, update_by, update_time)
VALUES
(1,1,0,0,'system',NOW(),'system',NOW()),
(2,3,0,0,'system',NOW(),'system',NOW());

-- =========================
-- 18.16 角色自定义组织初始化
-- =========================
INSERT INTO sys_role_custom_org (role_id, org_id, delete_flag, version, create_by, create_time, update_by, update_time)
VALUES
(2,2,0,0,'system',NOW(),'system',NOW()),
(2,3,0,0,'system',NOW(),'system',NOW());

-- =========================
-- 18.17 角色自定义部门初始化
-- =========================
INSERT INTO sys_role_custom_dept (role_id, dept_id, delete_flag, version, create_by, create_time, update_by, update_time)
VALUES
(2,2,0,0,'system',NOW(),'system',NOW()),
(2,3,0,0,'system',NOW(),'system',NOW());
