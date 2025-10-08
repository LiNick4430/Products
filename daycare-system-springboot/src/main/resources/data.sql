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
INSERT INTO organization ( organization_id, organization_name, organization_description, organization_address, organization_phone, organization_email, organization_fax, create_date, update_date ) VALUES
-- 1. 北區-陽光寶貝公托
( 1, '台中市北區陽光寶貝公托家園', '致力於提供溫馨、安全的環境，以遊戲啟發幼兒潛能。', '台中市北區學士路100號', '04-22001111', 'sunshine.ntc@daycare.gov.tw', '04-22001112',NOW(), NOW() ),
-- 2. 西屯區-彩虹幼兒園
( 2, '台中市西屯區彩虹公立幼兒園', '結合自然教學法，培養幼兒獨立思考能力，近公園綠地。', '台中市西屯區台灣大道三段300號', '04-23002222', 'rainbow.wtc@daycare.gov.tw', '04-23002223',NOW(), NOW() ),
-- 3. 南區-智慧樹公托
(3, '台中市南區智慧樹社區公托', '專注於幼兒早期教育與感統訓練，提供專業的師資團隊。', '台中市南區復興路一段50號', '04-24003333', 'wisdom.stc@daycare.gov.tw', '04-24003334',NOW(), NOW() ),
-- 4. 豐原區-愛心幼兒園
(4, '台中市豐原區愛心公立幼兒園', '在地深耕，提供親切且互動性高的學習環境，服務豐原家庭。', '台中市豐原區中正路20號', '04-25004444', 'love.fyc@daycare.gov.tw', '04-25004445',NOW(), NOW() );
    
-- 5. 插入預設帳號 (USERS) 密碼暫時為明文，實作密碼加密後必須替換！
