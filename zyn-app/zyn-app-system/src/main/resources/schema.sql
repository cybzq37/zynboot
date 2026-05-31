-- ============================================================
-- Zyn System Module - Schema (H2 / PostgreSQL 兼容)
-- ============================================================

-- ----------------------------
-- Table: sys_user
-- ----------------------------
CREATE TABLE IF NOT EXISTS sys_user (
    id              VARCHAR(64)  PRIMARY KEY,
    username        VARCHAR(64)  NOT NULL,
    password        VARCHAR(256),
    nickname        VARCHAR(64),
    real_name       VARCHAR(64),
    email           VARCHAR(128),
    phone           VARCHAR(32),
    avatar          VARCHAR(512),
    gender          SMALLINT     DEFAULT 0,
    status          SMALLINT     DEFAULT 1,
    login_ip        VARCHAR(64),
    login_time      TIMESTAMP,
    pwd_update_time TIMESTAMP,
    login_attempts  INT          DEFAULT 0,
    lock_time       TIMESTAMP,
    remark          VARCHAR(512),
    version         INT          DEFAULT 0,
    deleted         BOOLEAN      DEFAULT FALSE,
    create_by       VARCHAR(64),
    create_time     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    update_by       VARCHAR(64),
    update_time     TIMESTAMP,
    CONSTRAINT uk_sys_user_username UNIQUE (username)
);

-- ----------------------------
-- Table: sys_role
-- ----------------------------
CREATE TABLE IF NOT EXISTS sys_role (
    id          VARCHAR(64)  PRIMARY KEY,
    role_code   VARCHAR(64)  NOT NULL,
    role_name   VARCHAR(128),
    role_type   SMALLINT     DEFAULT 1,
    sort        INT          DEFAULT 0,
    status      SMALLINT     DEFAULT 1,
    data_scope  SMALLINT     DEFAULT 1,
    remark      VARCHAR(512),
    version     INT          DEFAULT 0,
    deleted     BOOLEAN      DEFAULT FALSE,
    create_by   VARCHAR(64),
    create_time TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    update_by   VARCHAR(64),
    update_time TIMESTAMP,
    CONSTRAINT uk_sys_role_role_code UNIQUE (role_code)
);

-- ----------------------------
-- Table: sys_permission
-- ----------------------------
CREATE TABLE IF NOT EXISTS sys_permission (
    id          VARCHAR(64)  PRIMARY KEY,
    parent_id   VARCHAR(64)  DEFAULT '0',
    perm_code   VARCHAR(128) NOT NULL,
    perm_name   VARCHAR(128),
    perm_type   SMALLINT     NOT NULL,
    path        VARCHAR(256),
    component   VARCHAR(256),
    icon        VARCHAR(128),
    sort        INT          DEFAULT 0,
    visible     BOOLEAN      DEFAULT TRUE,
    status      SMALLINT     DEFAULT 1,
    remark      VARCHAR(512),
    version     INT          DEFAULT 0,
    deleted     BOOLEAN      DEFAULT FALSE,
    create_by   VARCHAR(64),
    create_time TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    update_by   VARCHAR(64),
    update_time TIMESTAMP,
    CONSTRAINT uk_sys_permission_perm_code UNIQUE (perm_code)
);

-- ----------------------------
-- Table: sys_organization
-- ----------------------------
CREATE TABLE IF NOT EXISTS sys_organization (
    id          VARCHAR(64)  PRIMARY KEY,
    parent_id   VARCHAR(64)  DEFAULT '0',
    org_code    VARCHAR(64)  NOT NULL,
    org_name    VARCHAR(128),
    org_type    SMALLINT     DEFAULT 1,
    leader_id   VARCHAR(64),
    phone       VARCHAR(32),
    email       VARCHAR(128),
    sort        INT          DEFAULT 0,
    status      SMALLINT     DEFAULT 1,
    remark      VARCHAR(512),
    version     INT          DEFAULT 0,
    deleted     BOOLEAN      DEFAULT FALSE,
    create_by   VARCHAR(64),
    create_time TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    update_by   VARCHAR(64),
    update_time TIMESTAMP,
    CONSTRAINT uk_sys_organization_org_code UNIQUE (org_code)
);

-- ----------------------------
-- Table: sys_resource
-- ----------------------------
CREATE TABLE IF NOT EXISTS sys_resource (
    id             VARCHAR(64)  PRIMARY KEY,
    permission_id  VARCHAR(64),
    res_name       VARCHAR(128),
    res_type       SMALLINT     DEFAULT 1,
    request_method VARCHAR(16),
    request_path   VARCHAR(256),
    status         SMALLINT     DEFAULT 1,
    remark         VARCHAR(512),
    version        INT          DEFAULT 0,
    deleted        BOOLEAN      DEFAULT FALSE,
    create_by      VARCHAR(64),
    create_time    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    update_by      VARCHAR(64),
    update_time    TIMESTAMP
);

-- ----------------------------
-- Table: sys_user_role (关联表)
-- ----------------------------
CREATE TABLE IF NOT EXISTS sys_user_role (
    id          VARCHAR(64) PRIMARY KEY,
    user_id     VARCHAR(64) NOT NULL,
    role_id     VARCHAR(64) NOT NULL,
    create_by   VARCHAR(64),
    create_time TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_sys_user_role UNIQUE (user_id, role_id)
);

-- ----------------------------
-- Table: sys_role_permission (关联表)
-- ----------------------------
CREATE TABLE IF NOT EXISTS sys_role_permission (
    id            VARCHAR(64) PRIMARY KEY,
    role_id       VARCHAR(64) NOT NULL,
    permission_id VARCHAR(64) NOT NULL,
    create_by     VARCHAR(64),
    create_time   TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_sys_role_permission UNIQUE (role_id, permission_id)
);

-- ----------------------------
-- Table: sys_user_org (关联表)
-- ----------------------------
CREATE TABLE IF NOT EXISTS sys_user_org (
    id          VARCHAR(64) PRIMARY KEY,
    user_id     VARCHAR(64) NOT NULL,
    org_id      VARCHAR(64) NOT NULL,
    create_by   VARCHAR(64),
    create_time TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_sys_user_org UNIQUE (user_id, org_id)
);

-- ----------------------------
-- Table: sys_audit_log
-- ----------------------------
CREATE TABLE IF NOT EXISTS sys_audit_log (
    id             BIGSERIAL    PRIMARY KEY,
    user_id        VARCHAR(64),
    username       VARCHAR(64),
    operation      VARCHAR(128),
    method         VARCHAR(256),
    params         TEXT,
    ip             VARCHAR(64),
    user_agent     VARCHAR(512),
    status         SMALLINT     DEFAULT 1,
    error_msg      TEXT,
    duration_ms    BIGINT,
    create_time    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);
