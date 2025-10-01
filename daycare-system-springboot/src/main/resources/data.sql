-- Table 基本資料填入

-- Table role permission role_permission 為了 RBAC(Role-Based Access Control)
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

-- 4. 插入預設機構資料 (ORGANIZATION)
INSERT INTO organization 
    (organization_id, organization_name, organization_description, organization_address, organization_phone, organization_email, organization_fax, create_date, update_date)
VALUES 
    (1, 
    '台中市西屯公托中心', 
    '位於台中市西屯區的公立托育中心，環境優美，師資優良。', 
    '台中市西屯區台灣大道三段XX號', 
    '04-23123456', 
    'xitun_daycare@tccg.gov.tw', 
    NULL, 
    NOW(), 
    NOW()),
    
    (2, 
    '台中市北區育仁托兒所', 
    '服務台中市北區的幼兒，注重多元智能發展。', 
    '台中市北區學士路YY號', 
    '04-22001234', 
    'beiqu_yuren@tccg.gov.tw', 
    NULL, 
    NOW(), 
    NOW()),
    
    (3, 
    '台中市南屯區樂活幼兒園', 
    '提供南屯區家庭優質、安全的托育服務。', 
    '台中市南屯區黎明路一段ZZ號', 
    '04-23887766', 
    'nantun_lehuo@tccg.gov.tw', 
    NULL, 
    NOW(), 
    NOW()),
    
    (4, 
    '台中市東區太陽花托嬰中心', 
    '專為0-2歲幼兒設計的專業托嬰機構。', 
    '台中市東區自由路四段AA號', 
    '04-22225566', 
    'dongqu_sunflower@tccg.gov.tw', 
    NULL, 
    NOW(), 
    NOW());
    
-- 5. 插入預設帳號 (USERS) 密碼暫時為明文，實作密碼加密後必須替換！
-- A. 高級主管人員 (3 位, role_id=3)
INSERT INTO users (user_id, email, phone_number, account, password, is_active, active_date, login_date, role_id, create_date, update_date) VALUES 
(1, 'manager1@tccg.gov.tw', '0910100001', 'manager1', '123456', TRUE, NOW(), NULL, 3, NOW(), NOW()),
(2, 'manager2@tccg.gov.tw', '0910100002', 'manager2', '123456', TRUE, NOW(), NULL, 3, NOW(), NOW()),
(3, 'manager3@tccg.gov.tw', '0910100003', 'manager3', '123456', TRUE, NOW(), NULL, 3, NOW(), NOW());

-- B. 基層工作人員 (10 位, role_id=2)
-- 分配給四個機構 (機構ID: 1, 2, 3, 4)
INSERT INTO users (user_id, email, phone_number, account, password, is_active, active_date, login_date, role_id, create_date, update_date) VALUES 
(4, 'staff1@daycare.tw', '0920200004', 'staff1', '123456', TRUE, NOW(), NULL, 2, NOW(), NOW()), -- 機構 1 (西屯)
(5, 'staff2@daycare.tw', '0920200005', 'staff2', '123456', TRUE, NOW(), NULL, 2, NOW(), NOW()), -- 機構 1 (西屯)
(6, 'staff3@daycare.tw', '0920200006', 'staff3', '123456', TRUE, NOW(), NULL, 2, NOW(), NOW()), -- 機構 2 (北區)
(7, 'staff4@daycare.tw', '0920200007', 'staff4', '123456', TRUE, NOW(), NULL, 2, NOW(), NOW()), -- 機構 2 (北區)
(8, 'staff5@daycare.tw', '0920200008', 'staff5', '123456', TRUE, NOW(), NULL, 2, NOW(), NOW()), -- 機構 3 (南屯)
(9, 'staff6@daycare.tw', '0920200009', 'staff6', '123456', TRUE, NOW(), NULL, 2, NOW(), NOW()), -- 機構 3 (南屯)
(10, 'staff7@daycare.tw', '0920200010', 'staff7', '123456', TRUE, NOW(), NULL, 2, NOW(), NOW()), -- 機構 4 (東區)
(11, 'staff8@daycare.tw', '0920200011', 'staff8', '123456', TRUE, NOW(), NULL, 2, NOW(), NOW()), -- 機構 4 (東區)
(12, 'staff9@daycare.tw', '0920200012', 'staff9', '123456', TRUE, NOW(), NULL, 2, NOW(), NOW()), -- 機構 1 (西屯)
(13, 'staff10@daycare.tw', '0920200013', 'staff10', '123456', TRUE, NOW(), NULL, 2, NOW(), NOW());-- 機構 2 (北區)

-- C. 民眾帳號 (10 位, role_id=1)
INSERT INTO users (user_id, email, phone_number, account, password, is_active, active_date, login_date, role_id, create_date, update_date) VALUES 
(14, 'public1@gmail.com', '0930300014', 'public1', '123456', TRUE, NOW(), NULL, 1, NOW(), NOW()),
(15, 'public2@gmail.com', '0930300015', 'public2', '123456', TRUE, NOW(), NULL, 1, NOW(), NOW()),
(16, 'public3@gmail.com', '0930300016', 'public3', '123456', TRUE, NOW(), NULL, 1, NOW(), NOW()),
(17, 'public4@gmail.com', '0930300017', 'public4', '123456', TRUE, NOW(), NULL, 1, NOW(), NOW()),
(18, 'public5@gmail.com', '0930300018', 'public5', '123456', TRUE, NOW(), NULL, 1, NOW(), NOW()),
(19, 'public6@gmail.com', '0930300019', 'public6', '123456', TRUE, NOW(), NULL, 1, NOW(), NOW()),
(20, 'public7@gmail.com', '0930300020', 'public7', '123456', TRUE, NOW(), NULL, 1, NOW(), NOW()),
(21, 'public8@gmail.com', '0930300021', 'public8', '123456', TRUE, NOW(), NULL, 1, NOW(), NOW()),
(22, 'public9@gmail.com', '0930300022', 'public9', '123456', TRUE, NOW(), NULL, 1, NOW(), NOW()),
(23, 'public10@gmail.com', '0930300023', 'public10', '123456', TRUE, NOW(), NULL, 1, NOW(), NOW());

-- 6. 插入員工資訊 (USER_ADMIN)
-- A. 高級主管 (user_id: 1~3)。他們沒有機構限制，但為了資料完整性，我們假設他們隸屬於一個"市政府總部"機構ID=5 (如果你有這個機構，如果沒有，則暫時都給機構 1)
-- 由於你只建立了 1-4 機構，我們將他們暫時設為機構 1 (西屯)
INSERT INTO admin_user (admin_id, user_id, admin_name, title, organization_id, create_date, update_date) VALUES 
(1, 1, '張高階', '處長', 1, NOW(), NOW()),
(2, 2, '李高階', '副處長', 1, NOW(), NOW()),
(3, 3, '王高階', '主任', 1, NOW(), NOW());

-- B. 基層工作人員 (user_id: 4~13)，分配到不同機構
INSERT INTO admin_user (admin_id, user_id, admin_name, title, organization_id, create_date, update_date) VALUES 
(4, 4, '陳基層1', '行政人員', 1, NOW(), NOW()),
(5, 5, '林基層2', '教保員', 1, NOW(), NOW()),
(6, 6, '黃基層3', '行政人員', 2, NOW(), NOW()),
(7, 7, '吳基層4', '教保員', 2, NOW(), NOW()),
(8, 8, '蔡基層5', '行政人員', 3, NOW(), NOW()),
(9, 9, '謝基層6', '教保員', 3, NOW(), NOW()),
(10, 10, '許基層7', '行政人員', 4, NOW(), NOW()),
(11, 11, '羅基層8', '教保員', 4, NOW(), NOW()),
(12, 12, '高基層9', '行政人員', 1, NOW(), NOW()),
(13, 13, '周基層10', '教保員', 2, NOW(), NOW());

-- 7. 插入民眾資訊 (USER_PUBLIC) 
INSERT INTO public_user (public_id, user_id, public_name, national_id_no, birthdate, registered_address, mailing_address, create_date, update_date) VALUES 
(1, 14, '劉民眾一', 'A123456781', '1990-01-01', '台中市南屯區黎明路', '台中市南屯區黎明路', NOW(), NOW()),
(2, 15, '鄭民眾二', 'B223456782', '1991-02-02', '台中市西屯區台灣大道', '台中市西屯區台灣大道', NOW(), NOW()),
(3, 16, '蕭民眾三', 'C123456783', '1992-03-03', '台中市北區學士路', '台中市北區學士路', NOW(), NOW()),
(4, 17, '鄧民眾四', 'D223456784', '1993-04-04', '台中市東區自由路', '台中市東區自由路', NOW(), NOW()),
(5, 18, '簡民眾五', 'E123456785', '1994-05-05', '台中市南區復興路', '台中市南區復興路', NOW(), NOW()),
(6, 19, '白民眾六', 'F223456786', '1995-06-06', '台中市太平區中山路', '台中市太平區中山路', NOW(), NOW()),
(7, 20, '歐民眾七', 'G123456787', '1996-07-07', '台中市大里區中興路', '台中市大里區中興路', NOW(), NOW()),
(8, 21, '康民眾八', 'H223456788', '1997-08-08', '台中市豐原區中正路', '台中市豐原區中正路', NOW(), NOW()),
(9, 22, '尹民眾九', 'I123456789', '1998-09-09', '台中市潭子區潭興路', '台中市潭子區潭興路', NOW(), NOW()),
(10, 23, '龔民眾十', 'J223456790', '1999-10-10', '台中市清水區中山路', '台中市清水區中山路', NOW(), NOW());