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
    
-- 5. 插入預設帳號 (USERS) 密碼(預設為 123456 的 加密版)
INSERT INTO users (user_email, user_phone_number, user_account, user_password, user_is_active, role_id, create_date, update_date) VALUES
-- 1. 主管帳號 (ROLE_ID = 3) 
('manager@system.com', '0910111111', 'manager', '$2a$10$0PXADQYqxs.AZu/GBr522O5DdO1z2Z6XHlUoNRblyKyW/McKYm1Yq', TRUE, 3,NOW(), NOW()),
-- 2-5. 員工帳號 (ROLE_ID = 2) 
('staff_a@system.com', '0920222222', 'staff_a', '$2a$10$KNzCjc4S99xTAU8r1OpHLO5qs6GmXQgvdFTlFok3lOToUtiLAAXX.', TRUE, 2,NOW(), NOW()),
('staff_b@system.com', '0930333333', 'staff_b', '$2a$10$PZgeOaBXFJsQkIpeZxcoqu88Q/twoGAwT9CviTrxCsI3egT0Kyuqu', TRUE, 2,NOW(), NOW()),
('staff_c@system.com', '0940444444', 'staff_c', '$2a$10$tjOGZ4XdcdrVeyYd17yOM.DwFBMSJEpag6W.Aw7gpRf.w/XOEOqcC', TRUE, 2,NOW(), NOW()),
('staff_d@system.com', '0950555551', 'staff_d', '$2a$10$rP1yhH20.2AA/WlMaNnyTeO2U.tbc0WnORGFWo7anPpColB7NBGrK', TRUE, 2,NOW(), NOW()),
-- 6-10. 民眾帳號 (ROLE_ID = 1)
('public_2@system.com', '0950555552', 'public_2', '$2a$10$ewjCrApxXBC5CWDjYBQSd.tltdphrqs/bdYIPdcCW5eS7DiSA.Cji', TRUE, 1,NOW(), NOW()),
('public_3@system.com', '0950555553', 'public_3', '$2a$10$XxHqW2rKSM/AzI4WP3abYe6aaRMFDDrG04CqvH3QCPKLjMfGkif/i', TRUE, 1,NOW(), NOW()),
('public_4@system.com', '0950555554', 'public_4', '$2a$10$xP4HKIGKe7OHGKDjY7V0T.vZ4D2BaZmegdcRyf7mbtoXfacoAkqhe', TRUE, 1,NOW(), NOW()),
('public_5@system.com', '0950555555', 'public_5', '$2a$10$KDZIFuFWYk8BPuMfL0tAvObjX1pI3k6z84Owb77vSsTuaJnYBlKRC', TRUE, 1,NOW(), NOW()),
('public_6@system.com', '0950555556', 'public_6', '$2a$10$mAVVL5uugxz.mYnS1ox2oeE570g8EEhHQScLXHf0VqX7wu4EVZR4.', TRUE, 1,NOW(), NOW());
    
-- 預設 員工資料
INSERT INTO admin_user (admin_id, admin_name, admin_job_title, organization_id, create_date, update_date) VALUES
-- 1. 主管 (Manager)
(1, '主管A', '經理', 1 ,NOW(), NOW()),
-- 2. 員工 A (Staff A)
(2, '員工甲', '專員', 1 ,NOW(), NOW()),
-- 3. 員工 B (Staff B)
(3, '員工乙', '專員', 2 ,NOW(), NOW()),
-- 4. 員工 C (Staff C)
(4, '員工丙', '專員', 3 ,NOW(), NOW()),
-- 5. 員工 D (Staff D)
(5, '員工丁', '專員', 4 ,NOW(), NOW());

-- 預設 民眾資料
INSERT INTO public_user (public_id, public_name, public_national_id_no, public_birthdate, public_registered_address, public_mailing_address, create_date, update_date) VALUES
(6, '林雅雯', 'B234567892', '1992-05-15', '臺中市北區三民路三段129號', '臺中市北區進化路400號5樓', NOW(), NOW()),
(7, '黃子軒', 'C345678903', '1978-08-01', '臺中市南屯區公益路二段51號', '臺中市南屯區文心路一段200號10樓', NOW(), NOW()),
(8, '張靜宜', 'D456789014', '2001-02-28', '臺中市東區復興路四段186號', '臺中市東區大勇街10巷8號', NOW(), NOW()),
(9, '李冠霖', 'E567890125', '1965-04-10', '臺中市豐原區中正路288號', '臺中市豐原區圓環東路150巷9號', NOW(), NOW()),
(10, '王怡萱', 'F678901236', '1998-12-05', '臺中市大里區國光路二段710號', '臺中市大里區德芳南路300號', NOW(), NOW());

-- 預設 幼兒資料
INSERT INTO child_info 
(child_name, child_national_id_no, child_birthdate, child_gender, public_id, create_date, update_date)
VALUES

-- 公民 6：林雅雯
('張以萱', 'H234567812', '2021-09-18', '女', 6, NOW(), NOW()),
('張以晨', 'H345678923', '2023-04-02', '男', 6, NOW(), NOW()),

-- 公民 7：黃子軒
('黃宥睿', 'J123456784', '2020-12-10', '男', 7, NOW(), NOW()),

-- 公民 8：張靜宜
('陳芷晴', 'K567890126', '2022-07-25', '女', 8, NOW(), NOW()),
('陳芷語', 'K678901237', '2024-01-14', '女', 8, NOW(), NOW()),

-- 公民 9：李冠霖
('李柏睿', 'L789012348', '2019-03-22', '男', 9, NOW(), NOW()),

-- 公民 10：王怡萱
('林品希', 'M890123459', '2020-10-02', '女', 10, NOW(), NOW()),
('林品諾', 'M901234560', '2023-03-30', '男', 10, NOW(), NOW());

-- 預設 規範
INSERT INTO regulations (regulation_type, regulation_content, organization_id, create_date, update_date) VALUES

-- ========= 機構 1 =========
('FEE_SCHEDULE',
 '本機構之費用收費表如下：月費 12,000 元，午餐費 1,200 元，活動材料費依季收取。',
 1, NOW(), NOW()),
('LUNCH_POLICY',
 '本機構提供每日三餐點心，午睡時間為 12:30–14:30，由專職老師陪同照護。',
 1, NOW(), NOW()),
('ATTENDANCE_RULE',
 '入離園時間為 07:30–18:00。家長若遲到請提前通知，請假以 LINE 官方帳號登記。',
 1, NOW(), NOW()),
('EMERGENCY_PLAN',
 '如遇緊急狀況（地震/火災），將依本機構避難路線撤離並立即通知家長。',
 1, NOW(), NOW()),

-- ========= 機構 2 =========
('FEE_SCHEDULE',
 '本園區收費標準：月費 13,500 元，午餐費 1,000 元。每學期另收教具費 2,000 元。',
 2, NOW(), NOW()),
('LUNCH_POLICY',
 '午餐由中央廚房統一配送，午睡時間為 13:00–15:00，採用個人睡墊。',
 2, NOW(), NOW()),
('ATTENDANCE_RULE',
 '開放入園時間為 08:00–18:30，遲到需簽到。請假須於當日 9:00 前告知。',
 2, NOW(), NOW()),
('EMERGENCY_PLAN',
 '緊急事件發生時，教師將依標準流程疏散至操場集合點，並以電話通知監護人。',
 2, NOW(), NOW()),

-- ========= SBO 機構 3 =========
('FEE_SCHEDULE',
 '本托育中心費用：月費 11,800 元，含點心。午餐費另計 1,100 元，教材費視課程收取。',
 3, NOW(), NOW()),
('LUNCH_POLICY',
 '餐點由營養師設計菜單，午睡 12:45–14:15。午睡室提供空氣清淨機。',
 3, NOW(), NOW()),
('ATTENDANCE_RULE',
 '開放家長 07:45–18:00 接送，若需加時服務請提前申請並依規定加收費用。',
 3, NOW(), NOW()),
('EMERGENCY_PLAN',
 '本中心每季實施一次避難演練，災害發生時依指示至安全區集合並即刻聯繫家長。',
 3, NOW(), NOW()),

-- ========= 機構 4 =========
('FEE_SCHEDULE',
 '本教育機構之收費：月費 14,200 元，午餐費 1,300 元。活動費視參加情形另行收取。',
 4, NOW(), NOW()),
('LUNCH_POLICY',
 '提供自製健康餐點，午睡時間為 12:00–14:00，使用固定午睡床並定期清潔。',
 4, NOW(), NOW()),
('ATTENDANCE_RULE',
 '入園時間 07:30–17:45。生病需請假者請提供就醫證明，避免傳染給其他幼兒。',
 4, NOW(), NOW()),
('EMERGENCY_PLAN',
 '本機構設有緊急聯絡網，重大事件時將同步以簡訊及電話通知家長並啟動疏散程序。',
 4, NOW(), NOW());

-- 預設 公告
INSERT INTO announcements 
(announcement_title, announcement_content, announcement_publish_date, announcement_is_published, announcement_expiry_date, organization_id, create_date, update_date)
VALUES

-- ======== 機構 1 ========
('3 月份課程通知',
 '親愛的家長您好：本月新增音樂律動課程，詳細內容請參閱課表。',
 '2025-03-01', true, NULL, 1, NOW(), NOW()),

('春季戶外教學報名開始',
 '本次將至動物園進行戶外教學，請於 3/10 前完成報名。',
 '2025-02-25', true, '2025-03-15', 1, NOW(), NOW()),

('四月份活動預告（草稿）',
 '4 月預計舉辦親子運動日，細節將於定案後公告。',
 NULL, false, NULL, 1, NOW(), NOW()),


-- ======== 機構 2 ========
('園區設備年度消毒',
 '本園將於 3/5 全面消毒，當日不開放參觀，敬請配合。',
 '2025-03-03', true, NULL, 2, NOW(), NOW()),

('午餐菜單更新公告',
 '四月起午餐改採新供應商，菜色更均衡，請家長放心。',
 '2025-03-02', true, '2025-04-30', 2, NOW(), NOW()),

('招生說明會（草稿）',
 '預計於 5 月舉辦招生說明會，內容尚在修訂中。',
 NULL, false, NULL, 2, NOW(), NOW()),

('上課時間調整說明（草稿）',
 '因應社區交通施工，部分時段可能調整接送動線。',
 NULL, false, NULL, 2, NOW(), NOW()),


-- ======== 機構 3 ========
('校園安全宣導週',
 '本周安排安全教育課程，請家長提醒孩子共同配合。',
 '2025-02-28', true, NULL, 3, NOW(), NOW()),

('停水通知',
 '因區域性檢修，3/12 上午部分設施停止供水，但課程照常進行。',
 '2025-03-08', true, '2025-03-12', 3, NOW(), NOW()),

('家長會議（草稿）',
 '家長會議初步規劃於 4/20 舉行，細節將後續公布。',
 NULL, false, NULL, 3, NOW(), NOW()),


-- ======== 機構 4 ========
('春假行前提醒',
 '春假期間請家長留意幼兒作息與安全，假期後正常開課。',
 '2025-03-20', true, NULL, 4, NOW(), NOW()),

('新學期注意事項',
 '新學期將啟用新版聯絡簿，請家長於第一週協助填寫資訊。',
 '2025-02-15', true, '2025-04-01', 4, NOW(), NOW()),

('課後才藝課程調整（草稿）',
 '部分才藝課程正在調整師資與時段，尚未對外公布。',
 NULL, false, NULL, 4, NOW(), NOW()),

('攝影紀錄日（草稿）',
 '預計安排攝影師進行學習紀錄拍攝，詳細流程待確認。',
 NULL, false, NULL, 4, NOW(), NOW());

-- 預設 班級資料
INSERT INTO classes
(class_name, class_max_capacity, class_current_count,
 class_age_min_months, class_age_max_months,
 class_service_start_month, class_service_end_month,
 organization_id, version, create_date, update_date)
VALUES

-- 機構 1
('小樹班', 12, 0,
 24, 48,
 1, 12,
 1, 0, NOW(), NOW()),

-- 機構 2
('向日葵班', 15, 0,
 36, 60,
 1, 12,
 2, 0, NOW(), NOW()),

-- 機構 3
('彩虹班', 10, 0,
 30, 54,
 1, 12,
 3, 0, NOW(), NOW()),

-- 機構 4
('星星班', 12, 0, 
 24, 72,
 1, 12,
 4, 0, NOW(), NOW());

-- 預設 CASE 優先條件 
INSERT INTO priority (priority_name, priority_is_active, create_date, update_date)
VALUES
('低收入戶', true, NOW(), NOW()),
('中低收入戶', true, NOW(), NOW()),
('單親家庭', true, NOW(), NOW()),
('身心障礙兒童', true, NOW(), NOW()),
('身心障礙家長', true, NOW(), NOW()),
('原住民身份', true, NOW(), NOW()),
('家庭突發變故', true, NOW(), NOW()),
('隔代教養', true, NOW(), NOW()),
('多胞胎家庭', true, NOW(), NOW()),
('同一機構兄弟姊妹在學', true, NOW(), NOW()),
('特殊境遇家庭', true, NOW(), NOW()),
('寄養家庭', true, NOW(), NOW());

-- 最後的時候 進行存入
COMMIT;