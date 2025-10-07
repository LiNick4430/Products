-- Table 基本資料填入
-- Table role permission role_permission 為了 RBAC(Role-Based Access Control)
-- 1. 插入基礎角色 (ROLE)
INSERT INTO role (role_id, role_name, role_description, create_date, update_date) VALUES 
(1, 'ROLE_PUBLIC', '普通民眾', NOW(), NOW()),
(2, 'ROLE_STAFF', '基層人員', NOW(), NOW()),
(3, 'ROLE_MANAGER', '高階主管', NOW(), NOW());

-- 2. 插入系統權限 (PERMISSION)
INSERT INTO permission (permission_id, permission_name, permission_description, create_date, update_date) VALUES 
-- 前台功能 (所有人都可見/可操作，但為了安全，部分動作需要登入)
(101, 'ANNOUNCEMENT_READ', '系統公告', NOW(), NOW()),
(102, 'ORGANIZATION_READ', '機構介紹', NOW(), NOW()),
(103, 'PUBLIC_USER_CREATE', '帳號註冊', NOW(), NOW()),
(104, 'CREATE_CASE', '線上申辦-申請', NOW(), NOW()),
(105, 'WITHDRAWAL_REQUESTS', '線上申辦-撤銷', NOW(), NOW()),

-- 後台 - 基層工作人員
(201, 'ANNOUNCEMENT_MANAGE', '公告管理 (新增/修改/刪除)', NOW(), NOW()),
(202, 'ORGANIZATION_MANAGE', '機構管理 (新增/修改/刪除)', NOW(), NOW()),
(203, 'CLASS_MANAGE', '班級管理 (新增/修改/刪除)', NOW(), NOW()),
(204, 'CASE_AUDIT', '申請審核', NOW(), NOW()),
(205, 'WITHDRAWAL_AUDIT', '撤銷審核', NOW(), NOW()),
(206, 'WAITLIST_READ', '候補清冊', NOW(), NOW()),
(207, 'CASE_MANAGEMENT', '個案管理 (錄取/退托)', NOW(), NOW()),

-- 後台 - 高階主管人員
(301, 'USER_PUBLIC_MANAGE', '民眾帳號管理', NOW(), NOW()),
(302, 'USER_ADMIN_MANAGE', '後臺帳號管理', NOW(), NOW()),
(303, 'REGULATION_MANAGE', '規範說明管理', NOW(), NOW()),
(304, 'LOTTERY_QUEUE', '補位抽籤執行', NOW(), NOW());

-- 3. 建立角色-權限關聯 (ROLE_PERMISSION)
-- --- A. 民眾權限 (ROLE_PUBLIC) ---	101, 102, 103, 104, 105
INSERT INTO role_permission (role_id, permission_id) VALUES
(1, 101), (1, 102), (1, 103), (1, 104), (1, 105); 

-- --- B. 基層工作人員權限 (ROLE_STAFF) ---	201 ~ 207
INSERT INTO role_permission (role_id, permission_id) VALUES
(2, 201), (2, 202),(2, 203), (2, 204), (2, 205), (2, 206), (2, 207); 

-- --- C. 高階主管人員權限 (ROLE_MANAGER) ---		201 ~ 207, 301 ~ 304
INSERT INTO role_permission (role_id, permission_id) VALUES
(3, 201), (3, 202), (3, 203), (3, 204), (3, 205), (3, 206), (3, 207), 
(3, 301), (3, 302), (3, 303), (3, 304);

-- 4. 插入預設機構資料 (ORGANIZATION)

    
-- 5. 插入預設帳號 (USERS) 密碼暫時為明文，實作密碼加密後必須替換！
