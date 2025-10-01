-- Table 基本資料填入
-- 1. Table role permission role_permission 為了 RBAC(Role-Based Access Control)

-- 1. 插入基礎角色 (ROLE)
INSERT INTO role (role_id, role_name, role_description, create_date, update_date) VALUES 
(1, 'ROLE_PUBLIC', '普通民眾', NOW(), NOW()),
(2, 'ROLE_STAFF', '基層工作人員', NOW(), NOW()),
(3, 'ROLE_MANAGER', '高階主管人員', NOW(), NOW());

-- 2. 插入系統權限 (PERMISSION)
INSERT INTO permission (permission_id, permission_name, permission_description, create_date, update_date) VALUES 
-- 前台功能 (所有人都可見/可操作，但為了安全，部分動作需要登入)
(101, 'FRONTEND_ANNOUNCEMENT_READ', '讀取系統公告', NOW(), NOW()),
(102, 'FRONTEND_INSTITUTION_READ', '讀取機構介紹', NOW(), NOW()),
(103, 'FRONTEND_REGISTER_CREATE', '帳號註冊 (未登入可操作)', NOW(), NOW()),
(104, 'APPLY_CREATE', '線上申辦-建立申請 (需登入)', NOW(), NOW()),
(105, 'APPLY_WITHDRAW', '線上申辦-撤銷申請 (需登入)', NOW(), NOW()),

-- 後台 - 基層工作人員
(201, 'ANNOUNCEMENT_MANAGE', '公告管理 (新增/修改/刪除)', NOW(), NOW()),
(202, 'INSTITUTION_MANAGE', '機構管理 (新增/修改/刪除)', NOW(), NOW()),
(203, 'CLASS_MANAGE', '班級管理 (新增/修改/刪除)', NOW(), NOW()),
(204, 'APPLY_REVIEW', '申請審核 (初審)', NOW(), NOW()),
(205, 'WITHDRAW_REVIEW', '撤銷審核', NOW(), NOW()),
(206, 'WAITLIST_READ', '候補清冊查看', NOW(), NOW()),
(207, 'CASE_MANAGEMENT', '個案管理 (錄取/退托)', NOW(), NOW()),

-- 後台 - 高階主管人員
(301, 'USER_PUBLIC_MANAGE', '民眾帳號管理', NOW(), NOW()),
(302, 'USER_ADMIN_MANAGE', '後臺帳號管理', NOW(), NOW()),
(303, 'SPECIFICATION_MANAGE', '規範說明管理', NOW(), NOW()),
(304, 'WAITLIST_LOTTERY', '補位抽籤執行', NOW(), NOW());

-- 3. 建立角色-權限關聯 (ROLE_PERMISSION)
-- --- A. 民眾權限 (ROLE_PUBLIC) ---	101, 102, 103, 104, 105
INSERT INTO role_permission (role_id, permission_id, create_date, update_date) VALUES
(1, 101, NOW(), NOW()), -- FRONTEND_ANNOUNCEMENT_READ
(1, 102, NOW(), NOW()), -- FRONTEND_INSTITUTION_READ
(1, 103, NOW(), NOW()), -- FRONTEND_REGISTER_CREATE
(1, 104, NOW(), NOW()), -- APPLY_CREATE (需登入)
(1, 105, NOW(), NOW()); -- APPLY_WITHDRAW (需登入)

-- --- B. 基層工作人員權限 (ROLE_STAFF) ---	201 ~ 207
INSERT INTO role_permission (role_id, permission_id, create_date, update_date) VALUES
(2, 201, NOW(), NOW()), -- ANNOUNCEMENT_MANAGE
(2, 202, NOW(), NOW()), -- INSTITUTION_MANAGE
(2, 203, NOW(), NOW()), -- CLASS_MANAGE
(2, 204, NOW(), NOW()), -- APPLY_REVIEW
(2, 205, NOW(), NOW()), -- WITHDRAW_REVIEW
(2, 206, NOW(), NOW()), -- WAITLIST_READ
(2, 207, NOW(), NOW()); -- CASE_MANAGEMENT

-- --- C. 高階主管人員權限 (ROLE_MANAGER) ---		201 ~ 207, 301 ~ 304
INSERT INTO role_permission (role_id, permission_id, create_date, update_date) VALUES
(3, 201, NOW(), NOW()), (3, 202, NOW(), NOW()), (3, 203, NOW(), NOW()), 
(3, 204, NOW(), NOW()), (3, 205, NOW(), NOW()), (3, 206, NOW(), NOW()), (3, 207, NOW(), NOW()), 
(3, 301, NOW(), NOW()), -- USER_PUBLIC_MANAGE
(3, 302, NOW(), NOW()), -- USER_ADMIN_MANAGE
(3, 303, NOW(), NOW()), -- SPECIFICATION_MANAGE
(3, 304, NOW(), NOW()); -- WAITLIST_LOTTERY

-- 2. 填入 幾個 User 給他各自的權限