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
-- 為了方便插入亂序數據，我們先暫時關閉外鍵檢查
SET foreign_key_checks = 0;

-- 總共 27 個帳號 (ID 1-27)
INSERT INTO users (user_id, email, phone_number, account, password, is_active, active_date, login_date, role_id, create_date, update_date) VALUES 
-- 民眾 (15 位, role_id=1): 帳號 ID = 1, 2, 4, 7, 10, 11, 14, 15, 17, 18, 20, 22, 24, 25, 27
(1,  'p_random1@mail.com', '0930100001', 'public_r1', '123456', TRUE, NOW(), NULL, 1, NOW(), NOW()),
(2,  'p_random2@mail.com', '0930100002', 'public_r2', '123456', TRUE, NOW(), NULL, 1, NOW(), NOW()),
(4,  'p_random3@mail.com', '0930100004', 'public_r3', '123456', TRUE, NOW(), NULL, 1, NOW(), NOW()),
(7,  'p_random4@mail.com', '0930100007', 'public_r4', '123456', TRUE, NOW(), NULL, 1, NOW(), NOW()),
(10, 'p_random5@mail.com', '0930100010', 'public_r5', '123456', TRUE, NOW(), NULL, 1, NOW(), NOW()),
(11, 'p_random6@mail.com', '0930100011', 'public_r6', '123456', TRUE, NOW(), NULL, 1, NOW(), NOW()),
(14, 'p_random7@mail.com', '0930100014', 'public_r7', '123456', TRUE, NOW(), NULL, 1, NOW(), NOW()),
(15, 'p_random8@mail.com', '0930100015', 'public_r8', '123456', TRUE, NOW(), NULL, 1, NOW(), NOW()),
(17, 'p_random9@mail.com', '0930100017', 'public_r9', '123456', TRUE, NOW(), NULL, 1, NOW(), NOW()),
(18, 'p_random10@mail.com', '0930100018', 'public_r10', '123456', TRUE, NOW(), NULL, 1, NOW(), NOW()),
(20, 'p_random11@mail.com', '0930100020', 'public_r11', '123456', TRUE, NOW(), NULL, 1, NOW(), NOW()),
(22, 'p_random12@mail.com', '0930100022', 'public_r12', '123456', TRUE, NOW(), NULL, 1, NOW(), NOW()),
(24, 'p_random13@mail.com', '0930100024', 'public_r13', '123456', TRUE, NOW(), NULL, 1, NOW(), NOW()),
(25, 'p_random14@mail.com', '0930100025', 'public_r14', '123456', TRUE, NOW(), NULL, 1, NOW(), NOW()),
(27, 'p_random15@mail.com', '0930100027', 'public_r15', '123456', TRUE, NOW(), NULL, 1, NOW(), NOW()),

-- 基層人員 (12 位, role_id=2): 帳號 ID = 3, 5, 6, 9, 12, 13, 16, 19, 21, 23, 26, 8
(3,  's_random1@daycare.tw', '0920200003', 'staff_r1', '123456', TRUE, NOW(), NULL, 2, NOW(), NOW()), -- 機構 1
(5,  's_random2@daycare.tw', '0920200005', 'staff_r2', '123456', TRUE, NOW(), NULL, 2, NOW(), NOW()), -- 機構 1
(6,  's_random3@daycare.tw', '0920200006', 'staff_r3', '123456', TRUE, NOW(), NULL, 2, NOW(), NOW()), -- 機構 1
(9,  's_random4@daycare.tw', '0920200009', 'staff_r4', '123456', TRUE, NOW(), NULL, 2, NOW(), NOW()), -- 機構 2
(12, 's_random5@daycare.tw', '0920200012', 'staff_r5', '123456', TRUE, NOW(), NULL, 2, NOW(), NOW()), -- 機構 2
(13, 's_random6@daycare.tw', '0920200013', 'staff_r6', '123456', TRUE, NOW(), NULL, 2, NOW(), NOW()), -- 機構 2
(16, 's_random7@daycare.tw', '0920200016', 'staff_r7', '123456', TRUE, NOW(), NULL, 2, NOW(), NOW()), -- 機構 3
(19, 's_random8@daycare.tw', '0920200019', 'staff_r8', '123456', TRUE, NOW(), NULL, 2, NOW(), NOW()), -- 機構 3
(21, 's_random9@daycare.tw', '0920200021', 'staff_r9', '123456', TRUE, NOW(), NULL, 2, NOW(), NOW()), -- 機構 3
(23, 's_random10@daycare.tw', '0920200023', 'staff_r10', '123456', TRUE, NOW(), NULL, 2, NOW(), NOW()),-- 機構 4
(26, 's_random11@daycare.tw', '0920200026', 'staff_r11', '123456', TRUE, NOW(), NULL, 2, NOW(), NOW()),-- 機構 4
(8,  's_random12@daycare.tw', '0920200008', 'staff_r12', '123456', TRUE, NOW(), NULL, 2, NOW(), NOW()), -- 機構 4

-- 高級人員 (4 位, role_id=3): 帳號 ID = 28, 29, 30, 31 (使用 28, 29, 30, 31 以免和前面的 ID 混淆)
(28, 'm_random1@tccg.gov.tw', '0910100028', 'manager_r1', '123456', TRUE, NOW(), NULL, 3, NOW(), NOW()), -- 機構 1
(29, 'm_random2@tccg.gov.tw', '0910100029', 'manager_r2', '123456', TRUE, NOW(), NULL, 3, NOW(), NOW()), -- 機構 2
(30, 'm_random3@tccg.gov.tw', '0910100030', 'manager_r3', '123456', TRUE, NOW(), NULL, 3, NOW(), NOW()), -- 機構 3
(31, 'm_random4@tccg.gov.tw', '0910100031', 'manager_r4', '123456', TRUE, NOW(), NULL, 3, NOW(), NOW()); -- 機構 4

-- 6. 插入員工資訊 (USER_ADMIN)
-- A. 高級主管 (user_id: 28, 29, 30, 31)
INSERT INTO admin_user (admin_id, user_id, admin_name, title, organization_id, create_date, update_date) VALUES 
(1, 28, '張高階', '處長', 1, NOW(), NOW()), -- 機構 1
(2, 29, '李高階', '副處長', 2, NOW(), NOW()), -- 機構 2
(3, 30, '王高階', '主任', 3, NOW(), NOW()), -- 機構 3
(4, 31, '趙高階', '督導', 4, NOW(), NOW()); -- 機構 4

-- B. 基層工作人員 (user_id: 3, 5, 6, 8, 9, 12, 13, 16, 19, 21, 23, 26)
-- 每個機構分配 3 位員工
INSERT INTO admin_user (admin_id, user_id, admin_name, title, organization_id, create_date, update_date) VALUES 
(5, 3,  '陳基層1', '行政人員', 1, NOW(), NOW()),
(6, 5,  '林基層2', '教保員', 1, NOW(), NOW()),
(7, 6,  '黃基層3', '行政人員', 1, NOW(), NOW()), -- 機構 1: (3人)
(8, 9,  '吳基層4', '教保員', 2, NOW(), NOW()),
(9, 12, '蔡基層5', '行政人員', 2, NOW(), NOW()),
(10, 13, '謝基層6', '教保員', 2, NOW(), NOW()), -- 機構 2: (3人)
(11, 16, '許基層7', '行政人員', 3, NOW(), NOW()),
(12, 19, '羅基層8', '教保員', 3, NOW(), NOW()),
(13, 21, '高基層9', '行政人員', 3, NOW(), NOW()), -- 機構 3: (3人)
(14, 23, '周基層10', '教保員', 4, NOW(), NOW()),
(15, 26, '孫基層11', '行政人員', 4, NOW(), NOW()),
(16, 8,  '錢基層12', '教保員', 4, NOW(), NOW()); -- 機構 4: (3人)

-- 7. 插入民眾資訊 (USER_PUBLIC) 
INSERT INTO public_user (public_id, user_id, public_name, national_id_no, birthdate, registered_address, mailing_address, create_date, update_date) VALUES 
(1, 1, '劉民眾一', 'A123456781', '1990-01-01', '台中市南屯區黎明路', '台中市南屯區黎明路', NOW(), NOW()),
(2, 2, '鄭民眾二', 'B223456782', '1991-02-02', '台中市西屯區台灣大道', '台中市西屯區台灣大道', NOW(), NOW()),
(3, 4, '蕭民眾三', 'C123456783', '1992-03-03', '台中市北區學士路', '台中市北區學士路', NOW(), NOW()),
(4, 7, '鄧民眾四', 'D223456784', '1993-04-04', '台中市東區自由路', '台中市東區自由路', NOW(), NOW()),
(5, 10, '簡民眾五', 'E123456785', '1994-05-05', '台中市南區復興路', '台中市南區復興路', NOW(), NOW()),
(6, 11, '白民眾六', 'F223456786', '1995-06-06', '台中市太平區中山路', '台中市太平區中山路', NOW(), NOW()),
(7, 14, '歐民眾七', 'G123456787', '1996-07-07', '台中市大里區中興路', '台中市大里區中興路', NOW(), NOW()),
(8, 15, '康民眾八', 'H223456788', '1997-08-08', '台中市豐原區中正路', '台中市豐原區中正路', NOW(), NOW()),
(9, 17, '尹民眾九', 'I123456789', '1998-09-09', '台中市潭子區潭興路', '台中市潭子區潭興路', NOW(), NOW()),
(10, 18, '龔民眾十', 'J223456790', '1999-10-10', '台中市清水區中山路', '台中市清水區中山路', NOW(), NOW()),
(11, 20, '錢民眾十一', 'K123456791', '2000-11-11', '台中市龍井區沙田路', '台中市龍井區沙田路', NOW(), NOW()),
(12, 22, '孫民眾十二', 'L223456792', '2001-12-12', '台中市霧峰區中正路', '台中市霧峰區中正路', NOW(), NOW()),
(13, 24, '李民眾十三', 'M123456793', '2002-01-13', '台中市大甲區經國路', '台中市大甲區經國路', NOW(), NOW()),
(14, 25, '吳民眾十四', 'N223456794', '2003-02-14', '台中市神岡區中山路', '台中市神岡區中山路', NOW(), NOW()),
(15, 27, '許民眾十五', 'O123456795', '2004-03-15', '台中市后里區甲后路', '台中市后里區甲后路', NOW(), NOW());

-- 恢復外鍵檢查
SET foreign_key_checks = 1;