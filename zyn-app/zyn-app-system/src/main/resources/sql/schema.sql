-- ============================================================
-- Zyn 系统模块 - PostgreSQL Schema
-- 优化版：字段命名精简、类型合理、索引优化、NOT NULL 收紧
-- ============================================================

-- 清理旧表（按依赖顺序反向删除）
DROP TABLE IF EXISTS sys_user_org CASCADE;
DROP TABLE IF EXISTS sys_role_permission CASCADE;
DROP TABLE IF EXISTS sys_user_role CASCADE;
DROP TABLE IF EXISTS sys_resource CASCADE;
DROP TABLE IF EXISTS sys_permission CASCADE;
DROP TABLE IF EXISTS sys_organization CASCADE;
DROP TABLE IF EXISTS sys_role CASCADE;
DROP TABLE IF EXISTS sys_user CASCADE;
DROP TABLE IF EXISTS sys_file CASCADE;

-- ============================================================
-- 1. 用户表
-- 数据规模：~1000 行
-- ============================================================
CREATE TABLE sys_user (
    id              VARCHAR(64)  PRIMARY KEY,
    username        VARCHAR(64)  NOT NULL,
    password        VARCHAR(256) NOT NULL,
    nickname        VARCHAR(64),
    real_name       VARCHAR(64),
    email           VARCHAR(128),
    phone           VARCHAR(32),
    avatar          VARCHAR(256),
    gender          SMALLINT     NOT NULL DEFAULT 0,   -- 0=未知 1=男 2=女
    status          SMALLINT     NOT NULL DEFAULT 1,   -- 0=禁用 1=启用
    login_ip        VARCHAR(45),                       -- IPv4(15) / IPv6(45)
    login_time      TIMESTAMP,
    pwd_update_time TIMESTAMP,
    login_attempts  SMALLINT     NOT NULL DEFAULT 0,
    lock_time       TIMESTAMP,
    remark          VARCHAR(512),
    version         SMALLINT     NOT NULL DEFAULT 0,
    deleted         BOOLEAN      NOT NULL DEFAULT FALSE,
    create_by       VARCHAR(64),
    create_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by       VARCHAR(64),
    update_time     TIMESTAMP,
    CONSTRAINT uk_user_username UNIQUE (username)
);

CREATE INDEX idx_user_phone ON sys_user(phone) WHERE phone IS NOT NULL;
CREATE INDEX idx_user_email ON sys_user(email) WHERE email IS NOT NULL;
CREATE INDEX idx_user_status ON sys_user(status) WHERE deleted = FALSE;

COMMENT ON TABLE sys_user IS '用户';
COMMENT ON COLUMN sys_user.login_attempts IS '连续登录失败次数，达到阈值后锁定';
COMMENT ON COLUMN sys_user.lock_time IS '账号锁定时间，NULL 表示未锁定';

-- ============================================================
-- 2. 角色表
-- 数据规模：~50 行
-- ============================================================
CREATE TABLE sys_role (
    id          VARCHAR(64)  PRIMARY KEY,
    code        VARCHAR(64)  NOT NULL,
    name        VARCHAR(128) NOT NULL,
    type        SMALLINT     NOT NULL DEFAULT 1,   -- 0=内置 1=自定义
    sort_order  SMALLINT     NOT NULL DEFAULT 0,
    status      SMALLINT     NOT NULL DEFAULT 1,   -- 0=禁用 1=启用
    data_scope  SMALLINT     NOT NULL DEFAULT 1,   -- 1=全部 2=本部门 3=本部门及下级 4=仅本人
    remark      VARCHAR(512),
    version     SMALLINT     NOT NULL DEFAULT 0,
    deleted     BOOLEAN      NOT NULL DEFAULT FALSE,
    create_by   VARCHAR(64),
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by   VARCHAR(64),
    update_time TIMESTAMP,
    CONSTRAINT uk_role_code UNIQUE (code)
);

CREATE INDEX idx_role_status ON sys_role(status) WHERE deleted = FALSE;

COMMENT ON TABLE sys_role IS '角色';
COMMENT ON COLUMN sys_role.type IS '角色类型：0=内置 1=自定义';
COMMENT ON COLUMN sys_role.data_scope IS '数据权限：1=全部 2=本部门 3=本部门及下级 4=仅本人';

-- ============================================================
-- 3. 权限/菜单表
-- 数据规模：~200 行
-- ============================================================
CREATE TABLE sys_permission (
    id          VARCHAR(64)  PRIMARY KEY,
    parent_id   VARCHAR(64),                      -- NULL = 根节点
    code        VARCHAR(128) NOT NULL,
    name        VARCHAR(128) NOT NULL,
    type        SMALLINT     NOT NULL,            -- 1=目录 2=菜单 3=按钮 4=API
    path        VARCHAR(256),
    sort_order  SMALLINT     NOT NULL DEFAULT 0,
    visible     BOOLEAN      NOT NULL DEFAULT TRUE,
    status      SMALLINT     NOT NULL DEFAULT 1,
    remark      VARCHAR(512),
    version     SMALLINT     NOT NULL DEFAULT 0,
    deleted     BOOLEAN      NOT NULL DEFAULT FALSE,
    create_by   VARCHAR(64),
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by   VARCHAR(64),
    update_time TIMESTAMP,
    CONSTRAINT uk_permission_code UNIQUE (code)
);

CREATE INDEX idx_permission_parent ON sys_permission(parent_id);
CREATE INDEX idx_permission_type ON sys_permission(type) WHERE deleted = FALSE;

COMMENT ON TABLE sys_permission IS '权限/菜单';
COMMENT ON COLUMN sys_permission.parent_id IS '父权限 ID，NULL 表示根节点';
COMMENT ON COLUMN sys_permission.type IS '权限类型：1=目录 2=菜单 3=按钮 4=API';

-- ============================================================
-- 4. 组织/部门表
-- 数据规模：~200 行
-- ============================================================
CREATE TABLE sys_organization (
    id          VARCHAR(64)  PRIMARY KEY,
    parent_id   VARCHAR(64),                      -- NULL = 根节点
    code        VARCHAR(64)  NOT NULL,
    name        VARCHAR(128) NOT NULL,
    type        SMALLINT     NOT NULL DEFAULT 1,   -- 1=公司 2=部门 3=团队
    leader_id   VARCHAR(64),
    phone       VARCHAR(32),
    email       VARCHAR(128),
    sort_order  SMALLINT     NOT NULL DEFAULT 0,
    status      SMALLINT     NOT NULL DEFAULT 1,
    remark      VARCHAR(512),
    version     SMALLINT     NOT NULL DEFAULT 0,
    deleted     BOOLEAN      NOT NULL DEFAULT FALSE,
    create_by   VARCHAR(64),
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by   VARCHAR(64),
    update_time TIMESTAMP,
    CONSTRAINT uk_organization_code UNIQUE (code)
);

CREATE INDEX idx_organization_parent ON sys_organization(parent_id);

COMMENT ON TABLE sys_organization IS '组织/部门';
COMMENT ON COLUMN sys_organization.parent_id IS '父组织 ID，NULL 表示根节点';

-- ============================================================
-- 5. API 资源表
-- 数据规模：~500 行
-- ============================================================
CREATE TABLE sys_resource (
    id             VARCHAR(64)  PRIMARY KEY,
    permission_id  VARCHAR(64),
    name           VARCHAR(128) NOT NULL,
    type           SMALLINT     NOT NULL DEFAULT 1,   -- 1=API 2=按钮
    request_method VARCHAR(16)  NOT NULL,
    request_path   VARCHAR(256) NOT NULL,
    status         SMALLINT     NOT NULL DEFAULT 1,
    remark         VARCHAR(512),
    version        SMALLINT     NOT NULL DEFAULT 0,
    deleted        BOOLEAN      NOT NULL DEFAULT FALSE,
    create_by      VARCHAR(64),
    create_time    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by      VARCHAR(64),
    update_time    TIMESTAMP,
    CONSTRAINT uk_resource_method_path UNIQUE (request_method, request_path)
);

CREATE INDEX idx_resource_permission ON sys_resource(permission_id);

COMMENT ON TABLE sys_resource IS 'API 资源';
COMMENT ON COLUMN sys_resource.request_method IS 'HTTP 方法：GET/POST/PUT/DELETE';

-- ============================================================
-- 6. 用户-角色关联表（物理删除）
-- 数据规模：~2000 行
-- ============================================================
CREATE TABLE sys_user_role (
    id          VARCHAR(64) PRIMARY KEY,
    user_id     VARCHAR(64) NOT NULL,
    role_id     VARCHAR(64) NOT NULL,
    create_by   VARCHAR(64),
    create_time TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_role UNIQUE (user_id, role_id)
);

-- uk_user_role 已覆盖 user_id 查询，role_id 需单独索引
CREATE INDEX idx_user_role_role ON sys_user_role(role_id);

COMMENT ON TABLE sys_user_role IS '用户-角色关联';

-- ============================================================
-- 7. 角色-权限关联表（物理删除）
-- 数据规模：~5000 行
-- ============================================================
CREATE TABLE sys_role_permission (
    id            VARCHAR(64) PRIMARY KEY,
    role_id       VARCHAR(64) NOT NULL,
    permission_id VARCHAR(64) NOT NULL,
    create_by     VARCHAR(64),
    create_time   TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_role_permission UNIQUE (role_id, permission_id)
);

-- uk_role_permission 已覆盖 role_id 查询，permission_id 需单独索引
CREATE INDEX idx_role_perm_perm ON sys_role_permission(permission_id);

COMMENT ON TABLE sys_role_permission IS '角色-权限关联';

-- ============================================================
-- 8. 用户-组织关联表（物理删除）
-- 数据规模：~1000 行
-- ============================================================
CREATE TABLE sys_user_org (
    id          VARCHAR(64) PRIMARY KEY,
    user_id     VARCHAR(64) NOT NULL,
    org_id      VARCHAR(64) NOT NULL,
    create_by   VARCHAR(64),
    create_time TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_org UNIQUE (user_id, org_id)
);

-- uk_user_org 已覆盖 user_id 查询，org_id 需单独索引
CREATE INDEX idx_user_org_org ON sys_user_org(org_id);

COMMENT ON TABLE sys_user_org IS '用户-组织关联';

-- ============================================================
-- 9. 文件管理表（物理删除）
-- 数据规模：~10000 行
-- ============================================================
CREATE TABLE sys_file (
    id              VARCHAR(64)  PRIMARY KEY,
    original_name   VARCHAR(256) NOT NULL,
    storage_key     VARCHAR(512) NOT NULL,
    file_size       BIGINT       NOT NULL DEFAULT 0,
    content_type    VARCHAR(128),
    extension       VARCHAR(16),
    md5             VARCHAR(64),
    biz_type        VARCHAR(64),
    biz_id          VARCHAR(64),
    sort_order      SMALLINT     NOT NULL DEFAULT 0,
    uploader_id     VARCHAR(64),
    remark          VARCHAR(512),
    create_by       VARCHAR(64),
    create_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by       VARCHAR(64),
    update_time     TIMESTAMP,
    CONSTRAINT uk_file_storage_key UNIQUE (storage_key)
);

CREATE INDEX idx_file_biz ON sys_file(biz_type, biz_id) WHERE biz_type IS NOT NULL;
CREATE INDEX idx_file_md5 ON sys_file(md5) WHERE md5 IS NOT NULL;
CREATE INDEX idx_file_uploader ON sys_file(uploader_id) WHERE uploader_id IS NOT NULL;

COMMENT ON TABLE sys_file IS '文件管理';
COMMENT ON COLUMN sys_file.storage_key IS '存储路径/key（StorageService）';
COMMENT ON COLUMN sys_file.md5 IS '文件 MD5，用于去重';
COMMENT ON COLUMN sys_file.biz_type IS '业务类型（avatar/article/attachment）';
