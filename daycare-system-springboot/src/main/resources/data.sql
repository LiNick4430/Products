-- 同時兼容 MySQL + PostgreSQL

-- Table 基本資料填入
-- Table role permission role_permission 為了 RBAC(Role-Based Access Control)
-- 1. 插入基礎角色 (ROLE)
INSERT INTO roles (role_name, role_description, create_date, update_date) VALUES 
('ROLE_PUBLIC', '普通民眾', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('ROLE_STAFF', '基層人員', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('ROLE_MANAGER', '高階主管', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 2. 插入系統權限 (PERMISSION)
INSERT INTO permission (permission_name, permission_description, create_date, update_date) VALUES 
-- 前台功能 (所有人都可見/可操作，但為了安全，部分動作需要登入)
('ANNOUNCEMENT_READ', '系統公告', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('ORGANIZATION_READ', '機構介紹', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PUBLIC_USER_CREATE', '帳號註冊', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('CREATE_CASE', '線上申辦-申請', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('WITHDRAWAL_REQUESTS', '線上申辦-撤銷', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- 後台 - 基層工作人員
('ANNOUNCEMENT_MANAGE', '公告管理 (新增/修改/刪除)', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('ORGANIZATION_MANAGE', '機構管理 (新增/修改/刪除)', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('CLASS_MANAGE', '班級管理 (新增/修改/刪除)', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('CASE_AUDIT', '申請審核', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('WITHDRAWAL_AUDIT', '撤銷審核', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('WAITLIST_READ', '候補清冊', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('CASE_MANAGEMENT', '個案管理 (錄取/退托)', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- 後台 - 高階主管人員
('USER_PUBLIC_MANAGE', '民眾帳號管理', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('USER_ADMIN_MANAGE', '後臺帳號管理', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('REGULATION_MANAGE', '規範說明管理', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('LOTTERY_QUEUE', '補位抽籤執行', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 3. 建立角色-權限關聯 (ROLE_PERMISSION)
-- --- A. 民眾權限 (ROLE_PUBLIC)
INSERT INTO role_permission (role_id, permission_id)
SELECT -- 搜尋資料 放進對應的 role_id, permission_id
	(SELECT role_id FROM roles WHERE role_name = 'ROLE_PUBLIC'), -- 搜尋 role 表 中 role_name 是 'ROLE_PUBLIC' 的 role_id 放入 role_permission 的 role_id
    p.permission_id	-- 把符合資料的 ID 放進去
FROM permission p	-- 遍歷 permission 表格
WHERE p.permission_name IN ( -- 找尋 permission_name 是 下方陣列 的 資料
	'ANNOUNCEMENT_READ',
    'ORGANIZATION_READ',
    'PUBLIC_USER_CREATE',
    'CREATE_CASE',
    'WITHDRAWAL_REQUESTS'
);

-- --- B. 基層工作人員權限 (ROLE_STAFF) ---	201 ~ 207
INSERT INTO role_permission (role_id, permission_id)
SELECT 
    (SELECT role_id FROM roles WHERE role_name = 'ROLE_STAFF'),
    p.permission_id
FROM permission p
WHERE p.permission_name IN (
    'ANNOUNCEMENT_MANAGE', 
    'ORGANIZATION_MANAGE',
    'CLASS_MANAGE', 
    'CASE_AUDIT', 
    'WITHDRAWAL_AUDIT', 
    'WAITLIST_READ', 
    'CASE_MANAGEMENT'
); 

-- --- C. 高階主管人員權限 (ROLE_MANAGER) ---		201 ~ 207, 301 ~ 304
INSERT INTO role_permission (role_id, permission_id)
SELECT 
    (SELECT role_id FROM roles WHERE role_name = 'ROLE_MANAGER'),
    p.permission_id
FROM permission p
WHERE p.permission_name IN (
    'ANNOUNCEMENT_MANAGE', 
    'ORGANIZATION_MANAGE', 
    'CLASS_MANAGE', 
    'CASE_AUDIT', 
    'WITHDRAWAL_AUDIT', 
    'WAITLIST_READ', 
    'CASE_MANAGEMENT',
    'USER_PUBLIC_MANAGE', 
    'USER_ADMIN_MANAGE', 
    'REGULATION_MANAGE', 
    'LOTTERY_QUEUE'
);

-- 4. 插入預設機構資料 (ORGANIZATION)
INSERT INTO organization (organization_name, organization_description, organization_address, organization_phone, organization_email, organization_fax, create_date, update_date ) VALUES
-- 1. 北區-陽光寶貝公托
('台中市北區陽光寶貝公托家園', '致力於提供溫馨、安全的環境，以遊戲啟發幼兒潛能。', '台中市北區學士路100號', '04-22001111', 'sunshine.ntc@daycare.gov.tw', '04-22001112',CURRENT_TIMESTAMP, CURRENT_TIMESTAMP ),
-- 2. 西屯區-彩虹幼兒園
('台中市西屯區彩虹公立幼兒園', '結合自然教學法，培養幼兒獨立思考能力，近公園綠地。', '台中市西屯區台灣大道三段300號', '04-23002222', 'rainbow.wtc@daycare.gov.tw', '04-23002223',CURRENT_TIMESTAMP, CURRENT_TIMESTAMP ),
-- 3. 南區-智慧樹公托
('台中市南區智慧樹社區公托', '專注於幼兒早期教育與感統訓練，提供專業的師資團隊。', '台中市南區復興路一段50號', '04-24003333', 'wisdom.stc@daycare.gov.tw', '04-24003334',CURRENT_TIMESTAMP, CURRENT_TIMESTAMP ),
-- 4. 豐原區-愛心幼兒園
('台中市豐原區愛心公立幼兒園', '在地深耕，提供親切且互動性高的學習環境，服務豐原家庭。', '台中市豐原區中正路20號', '04-25004444', 'love.fyc@daycare.gov.tw', '04-25004445',CURRENT_TIMESTAMP, CURRENT_TIMESTAMP );
    
-- 5. 插入預設帳號 (USERS) 密碼(預設為 123456 的 加密版)
-- 主管帳號 (role_name = 'ROLE_MANAGER')
INSERT INTO users (user_email, user_phone_number, user_account, user_password, user_is_active, role_id, create_date, update_date)
SELECT 
	'manager@system.com', 
    '0910111111', 
    'manager', 
    '$2a$10$0PXADQYqxs.AZu/GBr522O5DdO1z2Z6XHlUoNRblyKyW/McKYm1Yq', 
    TRUE, 
    (SELECT role_id FROM roles WHERE role_name = 'ROLE_MANAGER'),	-- 動態獲取 ID
    CURRENT_TIMESTAMP, 
    CURRENT_TIMESTAMP;

-- 員工帳號 (role_name = 'ROLE_STAFF')
INSERT INTO users (user_email, user_phone_number, user_account, user_password, user_is_active, role_id, create_date, update_date)
SELECT 
	u.user_email,
    u.user_phone_number,
    u.user_account,
    u.user_password,
    u.user_is_active,
    r.role_id,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM (
    SELECT 
		'staff_a@system.com' AS user_email, 
        '0920222222' AS user_phone_number, 
        'staff_a' AS user_account, 
        '$2a$10$KNzCjc4S99xTAU8r1OpHLO5qs6GmXQgvdFTlFok3lOToUtiLAAXX.' AS user_password, 
        TRUE AS user_is_active
	UNION ALL
    SELECT
		'staff_b@system.com', 
        '0930333333', 
        'staff_b', 
        '$2a$10$PZgeOaBXFJsQkIpeZxcoqu88Q/twoGAwT9CviTrxCsI3egT0Kyuqu', 
        TRUE
	UNION ALL
    SELECT
		'staff_c@system.com', 
        '0940444444', 
        'staff_c', 
        '$2a$10$tjOGZ4XdcdrVeyYd17yOM.DwFBMSJEpag6W.Aw7gpRf.w/XOEOqcC', 
        TRUE
	UNION ALL
    SELECT
		'staff_d@system.com', 
        '0950555551', 
        'staff_d', 
        '$2a$10$rP1yhH20.2AA/WlMaNnyTeO2U.tbc0WnORGFWo7anPpColB7NBGrK', 
        TRUE
    ) AS u	-- 建立一個 臨時表格 u 存放資料
CROSS JOIN	-- 將 四行 臨時資料 和 一行 role_id 交叉組合
(SELECT role_id FROM roles WHERE role_name = 'ROLE_STAFF') AS r;	-- 取出 條件下的 role_id

-- 民眾帳號 (role_name = 'ROLE_PUBLIC')
INSERT INTO users (user_email, user_phone_number, user_account, user_password, user_is_active, role_id, create_date, update_date) 
SELECT 
    u.user_email, 
    u.user_phone_number, 
    u.user_account, 
    u.user_password, 
    u.user_is_active,
    r.role_id, 
    CURRENT_TIMESTAMP, 
    CURRENT_TIMESTAMP 
FROM (
    SELECT 
        'public_1@system.com' AS user_email, 
        '0950555552' AS user_phone_number, 
        'public_1' AS user_account, 
        '$2a$10$ewjCrApxXBC5CWDjYBQSd.tltdphrqs/bdYIPdcCW5eS7DiSA.Cji' AS user_password, 
        TRUE AS user_is_active
    UNION ALL
    SELECT 
        'public_2@system.com', 
        '0950555553', 
        'public_2', 
        '$2a$10$XxHqW2rKSM/AzI4WP3abYe6aaRMFDDrG04CqvH3QCPKLjMfGkif/i',
        TRUE
    UNION ALL
    SELECT 
        'public_3@system.com', 
        '0950555554', 
        'public_3', 
        '$2a$10$xP4HKIGKe7OHGKDjY7V0T.vZ4D2BaZmegdcRyf7mbtoXfacoAkqhe',
        TRUE
    UNION ALL
    SELECT 
        'public_4@system.com', 
        '0950555555', 
        'public_4', 
        '$2a$10$KDZIFuFWYk8BPuMfL0tAvObjX1pI3k6z84Owb77vSsTuaJnYBlKRC',
        TRUE
    UNION ALL
    SELECT 
        'public_5@system.com', 
        '0950555556', 
        'public_5', 
        '$2a$10$mAVVL5uugxz.mYnS1ox2oeE570g8EEhHQScLXHf0VqX7wu4EVZR4.',
        TRUE
) AS u -- 民眾資料
CROSS JOIN 
(SELECT role_id FROM roles WHERE role_name = 'ROLE_PUBLIC') AS r;

-- 預設 員工資料
INSERT INTO admin_user (admin_id, admin_name, admin_job_title, organization_id, create_date, update_date)
SELECT 
	u.user_id AS admin_id,
    u_data.admin_name,
    u_data.admin_job_title,
    o.organization_id,
    CURRENT_TIMESTAMP, 
    CURRENT_TIMESTAMP
FROM (
	-- 1. 主管 (Manager)
    SELECT 'manager' AS user_account, '主管A' AS admin_name, '經理' AS admin_job_title, '台中市北區陽光寶貝公托家園' AS org_name
    UNION ALL
    -- 2. 員工 A (Staff A)
    SELECT 'staff_a', '員工甲', '專員', '台中市北區陽光寶貝公托家園'
    UNION ALL
    -- 3. 員工 B (Staff B)
    SELECT 'staff_b', '員工乙', '專員', '台中市西屯區彩虹公立幼兒園'
    UNION ALL
    -- 4. 員工 C (Staff C)
    SELECT 'staff_c', '員工丙', '專員', '台中市南區智慧樹社區公托'
    UNION ALL
    -- 5. 員工 D (Staff D)
    SELECT 'staff_d', '員工丁', '專員', '台中市豐原區愛心公立幼兒園'
) AS u_data	-- 建立 臨時員工資料 四個欄位
JOIN organization AS o
	ON u_data.org_name = o.organization_name	-- 根據機構名稱連結，取得 organization_id
JOIN users AS u
	ON u_data.user_account = u.user_account;	-- 根據登入帳號連結，取得 user_id

-- 預設 民眾資料
INSERT INTO public_user (public_id, public_name, public_national_id_no, public_birthdate, public_registered_address, public_mailing_address, create_date, update_date)
SELECT 
	u.user_id AS public_id,
    u_data.public_name,
    u_data.public_national_id_no,
    u_data.public_birthdate,
    u_data.public_registered_address,
    u_data.public_mailing_address,
    CURRENT_TIMESTAMP, 
    CURRENT_TIMESTAMP
FROM (
	SELECT 'public_1' AS user_account, '林雅雯' AS public_name, 'B234567892' AS public_national_id_no, '1992-05-15' AS public_birthdate, '臺中市北區三民路三段129號' AS public_registered_address, '臺中市北區進化路400號5樓' AS public_mailing_address
	UNION ALL
    SELECT 'public_2', '黃子軒', 'C345678903', '1978-08-01', '臺中市南屯區公益路二段51號', '臺中市南屯區文心路一段200號10樓'
    UNION ALL
    SELECT 'public_3', '張靜宜', 'D456789014', '2001-02-28', '臺中市東區復興路四段186號', '臺中市東區大勇街10巷8號'
    UNION ALL
    SELECT 'public_4', '李冠霖', 'E567890125', '1965-04-10', '臺中市豐原區中正路288號', '臺中市豐原區圓環東路150巷9號'
    UNION ALL
    SELECT 'public_5', '王怡萱', 'F678901236', '1998-12-05', '臺中市大里區國光路二段710號', '臺中市大里區德芳南路300號'
) AS u_data
JOIN users AS u
	ON u_data.user_account = u.user_account;

-- 預設 幼兒資料
INSERT INTO child_info (child_name, child_national_id_no, child_birthdate, child_gender, public_id, create_date, update_date)
SELECT 
	c_data.child_name,
    c_data.child_national_id_no,
    c_data.child_birthdate,
    c_data.child_gender,
    u.user_id AS public_id,
    CURRENT_TIMESTAMP, 
    CURRENT_TIMESTAMP
FROM (
	-- 林雅雯 (public_1)
	SELECT '張以萱' AS child_name, 'H234567812' AS child_national_id_no, '2021-09-18' AS child_birthdate, '女' AS child_gender, 'public_1' AS user_account
	UNION ALL
    SELECT '張以晨', 'H345678923', '2023-04-02', '男', 'public_1'
    UNION ALL
    -- 黃子軒 (public_2)
    SELECT '黃宥睿', 'J123456784', '2020-12-10', '男', 'public_2'
    UNION ALL
    -- 張靜宜 (public_3)
    SELECT '陳芷晴', 'K567890126', '2022-07-25', '女', 'public_3'
    UNION ALL
    SELECT '陳芷語', 'K678901237', '2024-01-14', '女', 'public_3'
    UNION ALL
	-- 李冠霖 (public_4)
    SELECT '李柏睿', 'L789012348', '2019-03-22', '男', 'public_4'
    UNION ALL
    -- 王怡萱 (public_5)
    SELECT '林品希', 'M890123459', '2020-10-02', '女', 'public_5'
    UNION ALL
    SELECT '林品諾', 'M901234560', '2023-03-30', '男', 'public_5'
) AS c_data -- 幼兒臨時資料
JOIN users AS u
	ON c_data.user_account = u.user_account;

-- 預設 規範
INSERT INTO regulations (regulation_type, regulation_content, organization_id, create_date, update_date)
SELECT
	r_data.regulation_type,
    r_data.regulation_content,
    o.organization_id, -- 透過 JOIN 取得正確的 organization_id
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM (
	-- ========= 機構 1: 台中市北區陽光寶貝公托家園 =========
	SELECT 'FEE_SCHEDULE' AS regulation_type,
    '本機構之費用收費表如下：月費 12,000 元，午餐費 1,200 元，活動材料費依季收取。' AS regulation_content,
    '台中市北區陽光寶貝公托家園' AS org_name
    UNION ALL
    SELECT 'LUNCH_POLICY',
	'本機構提供每日三餐點心，午睡時間為 12:30–14:30，由專職老師陪同照護。',
	'台中市北區陽光寶貝公托家園'
    UNION ALL
    SELECT 'ATTENDANCE_RULE',
	'入離園時間為 07:30–18:00。家長若遲到請提前通知，請假以 LINE 官方帳號登記。',
	'台中市北區陽光寶貝公托家園'
	UNION ALL
    SELECT 'EMERGENCY_PLAN',
	'如遇緊急狀況（地震/火災），將依本機構避難路線撤離並立即通知家長。',
	'台中市北區陽光寶貝公托家園'

	-- ========= 機構 2: 台中市西屯區彩虹公立幼兒園 =========
    UNION ALL
	SELECT 'FEE_SCHEDULE',
    '本園區收費標準：月費 13,500 元，午餐費 1,000 元。每學期另收教具費 2,000 元。',
	'台中市西屯區彩虹公立幼兒園'
    UNION ALL
	SELECT 'LUNCH_POLICY',
    '午餐由中央廚房統一配送，午睡時間為 13:00–15:00，採用個人睡墊。',
	'台中市西屯區彩虹公立幼兒園'
    UNION ALL
	SELECT 'ATTENDANCE_RULE',
    '開放入園時間為 08:00–18:30，遲到需簽到。請假須於當日 9:00 前告知。',
	'台中市西屯區彩虹公立幼兒園'
    UNION ALL
	SELECT 'EMERGENCY_PLAN',
    '緊急事件發生時，教師將依標準流程疏散至操場集合點，並以電話通知監護人。',
	'台中市西屯區彩虹公立幼兒園'

	-- ========= 機構 3: 台中市南區智慧樹社區公托 (SBO) =========
    UNION ALL
	SELECT 'FEE_SCHEDULE',
    '本托育中心費用：月費 11,800 元，含點心。午餐費另計 1,100 元，教材費視課程收取。',
	'台中市南區智慧樹社區公托'
    UNION ALL
	SELECT 'LUNCH_POLICY',
    '餐點由營養師設計菜單，午睡 12:45–14:15。午睡室提供空氣清淨機。',
	'台中市南區智慧樹社區公托'
    UNION ALL
	SELECT 'ATTENDANCE_RULE',
    '開放家長 07:45–18:00 接送，若需加時服務請提前申請並依規定加收費用。',
	'台中市南區智慧樹社區公托'
    UNION ALL
	SELECT 'EMERGENCY_PLAN',
    '本中心每季實施一次避難演練，災害發生時依指示至安全區集合並即刻聯繫家長。',
	'台中市南區智慧樹社區公托'

	-- ========= 機構 4: 台中市豐原區愛心公立幼兒園 =========
    UNION ALL
	SELECT 'FEE_SCHEDULE',
    '本教育機構之收費：月費 14,200 元，午餐費 1,300 元。活動費視參加情形另行收取。',
	'台中市豐原區愛心公立幼兒園'
    UNION ALL
	SELECT 'LUNCH_POLICY',
    '提供自製健康餐點，午睡時間為 12:00–14:00，使用固定午睡床並定期清潔。',
	'台中市豐原區愛心公立幼兒園'
    UNION ALL
	SELECT 'ATTENDANCE_RULE',
    '入園時間 07:30–17:45。生病需請假者請提供就醫證明，避免傳染給其他幼兒。',
	'台中市豐原區愛心公立幼兒園'
    UNION ALL
	SELECT 'EMERGENCY_PLAN',
    '本機構設有緊急聯絡網，重大事件時將同步以簡訊及電話通知家長並啟動疏散程序。',
	'台中市豐原區愛心公立幼兒園'
) AS r_data -- 規範臨時資料
JOIN organization AS o
	ON r_data.org_name = o.organization_name; -- 根據機構名稱連結，取得 organization_id

-- 預設 公告
INSERT INTO announcements (announcement_title, announcement_content, announcement_publish_date, announcement_is_published, announcement_expiry_date, organization_id, create_date, update_date)
SELECT
	a_data.announcement_title,
    a_data.announcement_content,
    a_data.announcement_publish_date,
    a_data.announcement_is_published,
    a_data.announcement_expiry_date,
    o.organization_id, -- 透過 JOIN 取得正確的 organization_id
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM (
	-- ========= 機構 1: 台中市北區陽光寶貝公托家園 =========
    -- 1. 已發布公告
    SELECT '12 月份課程通知' AS announcement_title,
	'親愛的家長您好：本月新增音樂律動課程，詳細內容請參閱課表。' AS announcement_content,
	'2025-12-01' AS announcement_publish_date,
    TRUE AS announcement_is_published,
    '2025-12-31' AS announcement_expiry_date,
    '台中市北區陽光寶貝公托家園' AS org_name
    UNION ALL
    -- 2. 已發布公告
    SELECT '春季戶外教學報名開始',
	'本次將至動物園進行戶外教學，請於 3/10 前完成報名。',
	'2025-02-25', TRUE, '2025-03-15',
	'台中市北區陽光寶貝公托家園'
    UNION ALL
    -- 3. 草稿 (未發布)
    SELECT '四月份活動預告（草稿）',
	'4 月預計舉辦親子運動日，細節將於定案後公告。',
	NULL, FALSE, NULL,
	'台中市北區陽光寶貝公托家園'

	-- ========= 機構 2: 台中市西屯區彩虹公立幼兒園 =========
    UNION ALL
    -- 4. 已發布公告
    SELECT '園區設備年度消毒',
    '本園將於 12/10 全面消毒，當日不開放參觀，敬請配合。',
    '2025-12-03', TRUE, '2025-12-17',
    '台中市西屯區彩虹公立幼兒園'
    UNION ALL
    -- 5. 已發布公告
    SELECT '午餐菜單更新公告',
    '四月起午餐改採新供應商，菜色更均衡，請家長放心。',
    '2025-03-02', TRUE, '2025-04-30',
    '台中市西屯區彩虹公立幼兒園'
    UNION ALL
    -- 6. 草稿 (未發布)
    SELECT '招生說明會（草稿）',
    '預計於 5 月舉辦招生說明會，內容尚在修訂中。',
    NULL, FALSE, NULL,
    '台中市西屯區彩虹公立幼兒園'
    UNION ALL
    -- 7. 草稿 (未發布)
    SELECT '上課時間調整說明（草稿）',
    '因應社區交通施工，部分時段可能調整接送動線。',
    NULL, FALSE, NULL,
    '台中市西屯區彩虹公立幼兒園'

	-- ========= 機構 3: 台中市南區智慧樹社區公托 (SBO) =========
    UNION ALL
    -- 8. 已發布公告
    SELECT '校園安全宣導週',
    '本周安排安全教育課程，請家長提醒孩子共同配合。',
    '2025-11-30', TRUE, '2025-12-07',
    '台中市南區智慧樹社區公托'
    UNION ALL
    -- 9. 已發布公告
    SELECT '停水通知',
    '因區域性檢修，3/12 上午部分設施停止供水，但課程照常進行。',
    '2025-03-08', TRUE, '2025-03-12',
    '台中市南區智慧樹社區公托'
    UNION ALL
    -- 10. 草稿 (未發布)
    SELECT '家長會議（草稿）',
    '家長會議初步規劃於 4/20 舉行，細節將後續公布。',
    NULL, FALSE, NULL,
    '台中市南區智慧樹社區公托'

	-- ========= 機構 4: 台中市豐原區愛心公立幼兒園 =========
    UNION ALL
    -- 11. 已發布公告
    SELECT '春假行前提醒',
    '春假期間請家長留意幼兒作息與安全，假期後正常開課。',
    '2025-03-20', TRUE, '2025-03-31',
    '台中市豐原區愛心公立幼兒園'
    UNION ALL
    -- 12. 已發布公告
    SELECT '新學期注意事項',
    '新學期將啟用新版聯絡簿，請家長於第一週協助填寫資訊。',
    '2025-02-15', TRUE, '2025-04-01',
    '台中市豐原區愛心公立幼兒園'
    UNION ALL
    -- 13. 草稿 (未發布)
    SELECT '課後才藝課程調整（草稿）',
    '部分才藝課程正在調整師資與時段，尚未對外公布。',
    NULL, FALSE, NULL,
    '台中市豐原區愛心公立幼兒園'
    UNION ALL
    -- 14. 草稿 (未發布)
    SELECT '攝影紀錄日（草稿）',
    '預計安排攝影師進行學習紀錄拍攝，詳細流程待確認。',
    NULL, FALSE, NULL,
    '台中市豐原區愛心公立幼兒園'
) AS a_data -- 公告臨時資料
JOIN organization AS o
	ON a_data.org_name = o.organization_name; -- 根據機構名稱連結，取得 organization_id

-- 預設 班級資料
INSERT INTO classes
(class_name, class_max_capacity, class_current_count,
class_age_min_months, class_age_max_months,
class_service_start_month, class_service_end_month,
organization_id, version, create_date, update_date)
SELECT
    c_data.class_name, c_data.class_max_capacity, c_data.class_current_count,
    c_data.class_age_min_months, c_data.class_age_max_months,
    c_data.class_service_start_month, c_data.class_service_end_month,
    o.organization_id, -- 透過 JOIN 取得正確的 organization_id
    c_data.version,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM (
    -- ========= 機構 1: 台中市北區陽光寶貝公托家園 (小樹班) =========
    SELECT '小樹班' AS class_name, 12 AS class_max_capacity, 0 AS class_current_count,
           24 AS class_age_min_months, 48 AS class_age_max_months,
           1 AS class_service_start_month, 12 AS class_service_end_month,
           0 AS version,
           '台中市北區陽光寶貝公托家園' AS org_name
    UNION ALL
    -- ========= 機構 2: 台中市西屯區彩虹公立幼兒園 (向日葵班) =========
    SELECT '向日葵班', 15, 0,
           36, 60,
           1, 12,
           0,
           '台中市西屯區彩虹公立幼兒園'
    UNION ALL
    -- ========= 機構 3: 台中市南區智慧樹社區公托 (彩虹班) =========
    SELECT '彩虹班', 10, 0,
           30, 54,
           1, 12,
           0,
           '台中市南區智慧樹社區公托'
    UNION ALL
    -- ========= 機構 4: 台中市豐原區愛心公立幼兒園 (星星班) =========
    SELECT '星星班', 12, 0,
           24, 72,
           1, 12,
           0,
           '台中市豐原區愛心公立幼兒園'
) AS c_data -- 班級臨時資料
JOIN organization AS o
	ON c_data.org_name = o.organization_name; -- 根據機構名稱連結，取得 organization_id

-- 預設 CASE 優先條件 
INSERT INTO priority (priority_name, priority_is_active, create_date, update_date)
VALUES
('低收入戶', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('中低收入戶', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('單親家庭', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('身心障礙兒童', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('身心障礙家長', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('原住民身份', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('家庭突發變故', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('隔代教養', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('多胞胎家庭', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('同一機構兄弟姊妹在學', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('特殊境遇家庭', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('寄養家庭', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 最後的時候 進行存入
COMMIT;