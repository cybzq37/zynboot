-- ============================================================
-- Zyn 系统模块 - 初始化数据
-- ============================================================

-- 内置角色
INSERT INTO sys_role (id, role_code, role_name, role_type, sort, status, data_scope) VALUES
('role-root',  'root',  '超级用户', 2, 0, 1, 1),
('role-admin', 'admin', '管理员',   2, 1, 1, 1),
('role-user',  'user',  '普通用户', 2, 2, 1, 4);

-- 超级用户（root，绕过权限检查）
INSERT INTO sys_user (id, username, password, nickname, real_name, status) VALUES
('user-root', 'root', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', '超级用户', 'Root', 1);

-- 管理员（走 RBAC）
INSERT INTO sys_user (id, username, password, nickname, real_name, status) VALUES
('user-admin', 'admin', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', '管理员', 'Admin', 1);

-- 用户角色关联
INSERT INTO sys_user_role (id, user_id, role_id) VALUES
('ur-root', 'user-root', 'role-root'),
('ur-admin', 'user-admin', 'role-admin');

-- 根组织
INSERT INTO sys_organization (id, parent_id, org_code, org_name, org_type, status) VALUES
('org-root', '0', 'root', '总公司', 1, 1);

-- 用户组织关联
INSERT INTO sys_user_org (id, user_id, org_id) VALUES
('uo-root', 'user-root', 'org-root'),
('uo-admin', 'user-admin', 'org-root');

-- 基础权限
INSERT INTO sys_permission (id, parent_id, perm_code, perm_name, perm_type, path, sort, status) VALUES
('perm-sys',  '0',        'system',           '系统管理', 1, '/system',         1, 1),
('perm-user', 'perm-sys', 'system:user',      '用户管理', 2, '/system/user',    1, 1),
('perm-role', 'perm-sys', 'system:role',      '角色管理', 2, '/system/role',    2, 1),
('perm-perm', 'perm-sys', 'system:perm',      '权限管理', 2, '/system/perm',    3, 1),
('perm-org',  'perm-sys', 'system:org',       '组织管理', 2, '/system/org',     4, 1),
('perm-log',  'perm-sys', 'system:log',       '审计日志', 2, '/system/log',     5, 1);

-- 用户管理按钮
INSERT INTO sys_permission (id, parent_id, perm_code, perm_name, perm_type, status) VALUES
('perm-user-query',  'perm-user', 'system:user:query',  '用户查询', 3, 1),
('perm-user-create', 'perm-user', 'system:user:create', '用户新增', 3, 1),
('perm-user-update', 'perm-user', 'system:user:update', '用户编辑', 3, 1),
('perm-user-delete', 'perm-user', 'system:user:delete', '用户删除', 3, 1);

-- 角色管理按钮
INSERT INTO sys_permission (id, parent_id, perm_code, perm_name, perm_type, status) VALUES
('perm-role-query',  'perm-role', 'system:role:query',  '角色查询', 3, 1),
('perm-role-create', 'perm-role', 'system:role:create', '角色新增', 3, 1),
('perm-role-update', 'perm-role', 'system:role:update', '角色编辑', 3, 1),
('perm-role-delete', 'perm-role', 'system:role:delete', '角色删除', 3, 1);

-- 权限管理按钮
INSERT INTO sys_permission (id, parent_id, perm_code, perm_name, perm_type, status) VALUES
('perm-perm-query',  'perm-perm', 'system:perm:query',  '权限查询', 3, 1),
('perm-perm-create', 'perm-perm', 'system:perm:create', '权限新增', 3, 1),
('perm-perm-update', 'perm-perm', 'system:perm:update', '权限编辑', 3, 1),
('perm-perm-delete', 'perm-perm', 'system:perm:delete', '权限删除', 3, 1);

-- 组织管理按钮
INSERT INTO sys_permission (id, parent_id, perm_code, perm_name, perm_type, status) VALUES
('perm-org-query',  'perm-org', 'system:org:query',  '组织查询', 3, 1),
('perm-org-create', 'perm-org', 'system:org:create', '组织新增', 3, 1),
('perm-org-update', 'perm-org', 'system:org:update', '组织编辑', 3, 1),
('perm-org-delete', 'perm-org', 'system:org:delete', '组织删除', 3, 1);

-- admin 拥有全部权限
INSERT INTO sys_role_permission (id, role_id, permission_id) VALUES
('rp-1',  'role-admin', 'perm-sys'),
('rp-2',  'role-admin', 'perm-user'),
('rp-3',  'role-admin', 'perm-role'),
('rp-4',  'role-admin', 'perm-perm'),
('rp-5',  'role-admin', 'perm-org'),
('rp-6',  'role-admin', 'perm-log'),
('rp-7',  'role-admin', 'perm-user-query'),
('rp-8',  'role-admin', 'perm-user-create'),
('rp-9',  'role-admin', 'perm-user-update'),
('rp-10', 'role-admin', 'perm-user-delete'),
('rp-11', 'role-admin', 'perm-role-query'),
('rp-12', 'role-admin', 'perm-role-create'),
('rp-13', 'role-admin', 'perm-role-update'),
('rp-14', 'role-admin', 'perm-role-delete'),
('rp-15', 'role-admin', 'perm-perm-query'),
('rp-16', 'role-admin', 'perm-perm-create'),
('rp-17', 'role-admin', 'perm-perm-update'),
('rp-18', 'role-admin', 'perm-perm-delete'),
('rp-19', 'role-admin', 'perm-org-query'),
('rp-20', 'role-admin', 'perm-org-create'),
('rp-21', 'role-admin', 'perm-org-update'),
('rp-22', 'role-admin', 'perm-org-delete');

-- root 角色不需要权限关联（代码里直接绕过）
