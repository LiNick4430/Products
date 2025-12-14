package com.github.lianick.util;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.github.lianick.model.eneity.Announcements;
import com.github.lianick.model.eneity.ChildInfo;
import com.github.lianick.model.eneity.Classes;
import com.github.lianick.model.eneity.Organization;
import com.github.lianick.model.eneity.Permission;
import com.github.lianick.model.eneity.Regulations;
import com.github.lianick.model.eneity.Role;
import com.github.lianick.model.eneity.UserAdmin;
import com.github.lianick.model.eneity.UserPublic;
import com.github.lianick.model.eneity.Users;
import com.github.lianick.model.enums.RegulationType;
import com.github.lianick.repository.AnnouncementsRepository;
import com.github.lianick.repository.ChildInfoRepository;
import com.github.lianick.repository.ClassesRepository;
import com.github.lianick.repository.OrganizationRepository;
import com.github.lianick.repository.PermissionRepository;
import com.github.lianick.repository.RegulationsRepository;
import com.github.lianick.repository.RoleRepository;
import com.github.lianick.repository.UserAdminRepository;
import com.github.lianick.repository.UserPublicRepository;
import com.github.lianick.repository.UsersRepository;

/**
 * 雲端 使用的 初始化 資料 程序
 * */

@Service
@Profile("render")	// 只有雲端 render profile 才會跑
public class DataInitializer implements CommandLineRunner{

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PermissionRepository permissionRepository;

	@Autowired
	private OrganizationRepository organizationRepository;

	@Autowired
	private UsersRepository usersRepository;

	@Autowired
	private UserAdminRepository userAdminRepository;

	@Autowired
	private UserPublicRepository userPublicRepository;

	@Autowired
	private ChildInfoRepository childInfoRepository;

	@Autowired
	private AnnouncementsRepository announcementsRepository;

	@Autowired
	private RegulationsRepository regulationsRepository;

	@Autowired
	private ClassesRepository classesRepository;

	@Override
	public void run(String... args) throws Exception {

		// 1. 建立 權限 (如果已存在可以先檢查)
		createPermissionIfNotExists("ANNOUNCEMENT_READ", "系統公告");
		createPermissionIfNotExists("ORGANIZATION_READ", "機構介紹");
		createPermissionIfNotExists("PUBLIC_USER_CREATE", "帳號註冊");
		createPermissionIfNotExists("CREATE_CASE", "線上申辦-申請");
		createPermissionIfNotExists("WITHDRAWAL_REQUESTS", "線上申辦-撤銷");

		createPermissionIfNotExists("ANNOUNCEMENT_MANAGE", "公告管理 (新增/修改/刪除)");
		createPermissionIfNotExists("ORGANIZATION_MANAGE", "機構管理 (新增/修改/刪除)");
		createPermissionIfNotExists("CLASS_MANAGE", "班級管理 (新增/修改/刪除)");
		createPermissionIfNotExists("CASE_AUDIT", "申請審核");
		createPermissionIfNotExists("WITHDRAWAL_AUDIT", "撤銷審核");
		createPermissionIfNotExists("WAITLIST_READ", "候補清冊");
		createPermissionIfNotExists("CASE_MANAGEMENT", "個案管理 (錄取/退托)");

		createPermissionIfNotExists("USER_PUBLIC_MANAGE", "民眾帳號管理");
		createPermissionIfNotExists("USER_ADMIN_MANAGE", "後臺帳號管理");
		createPermissionIfNotExists("REGULATION_MANAGE", "規範說明管理");
		createPermissionIfNotExists("LOTTERY_QUEUE", "補位抽籤執行");

		// 2. 建立 角色
		Role publicRole = createRoleIfNotExists("ROLE_PUBLIC", "普通民眾");
		Role staffRole = createRoleIfNotExists("ROLE_STAFF", "基層人員");
		Role managerRole = createRoleIfNotExists("ROLE_MANAGER", "高階主管");

		// 3. 角色 綁定 權限
		assignPermissionsToRole(publicRole, "ANNOUNCEMENT_READ", "ORGANIZATION_READ",
				"PUBLIC_USER_CREATE", "CREATE_CASE", "WITHDRAWAL_REQUESTS");

		assignPermissionsToRole(staffRole, "ANNOUNCEMENT_MANAGE", "ORGANIZATION_MANAGE", "CLASS_MANAGE",
				"CASE_AUDIT", "WITHDRAWAL_AUDIT", "WAITLIST_READ", "CASE_MANAGEMENT");

		assignPermissionsToRole(managerRole, "ANNOUNCEMENT_MANAGE", "ORGANIZATION_MANAGE", "CLASS_MANAGE",
				"CASE_AUDIT", "WITHDRAWAL_AUDIT", "WAITLIST_READ", "CASE_MANAGEMENT",
				"USER_PUBLIC_MANAGE", "USER_ADMIN_MANAGE", "REGULATION_MANAGE", "LOTTERY_QUEUE");

		// 4. 建立 機構
		Organization org1 = createOrganizationIfNotExists("台中市北區陽光寶貝公托家園", "致力於提供溫馨、安全的環境，以遊戲啟發幼兒潛能。", "台中市北區學士路100號", "04-22001111", "sunshine.ntc@daycare.gov.tw", "04-22001112");
		Organization org2 = createOrganizationIfNotExists("台中市西屯區彩虹公立幼兒園", "結合自然教學法，培養幼兒獨立思考能力，近公園綠地。", "台中市西屯區台灣大道三段300號", "04-23002222", "rainbow.wtc@daycare.gov.tw", "04-23002223");
		Organization org3 = createOrganizationIfNotExists("台中市南區智慧樹社區公托", "專注於幼兒早期教育與感統訓練，提供專業的師資團隊。", "台中市南區復興路一段50號", "04-24003333", "wisdom.stc@daycare.gov.tw", "04-24003334");
		Organization org4 = createOrganizationIfNotExists("台中市豐原區愛心公立幼兒園", "在地深耕，提供親切且互動性高的學習環境，服務豐原家庭。", "台中市豐原區中正路20號", "04-25004444", "love.fyc@daycare.gov.tw", "04-25004445");
		organizationRepository.flush();	// 預先存 供給 後面 ID使用

		// 5. 建立 使用者
		Users userManager = createUsersIfNotExists("manager", "$2a$10$0PXADQYqxs.AZu/GBr522O5DdO1z2Z6XHlUoNRblyKyW/McKYm1Yq", "manager@system.com", "0910111111", true, managerRole);

		Users userStaffA = createUsersIfNotExists("staff_a", "$2a$10$KNzCjc4S99xTAU8r1OpHLO5qs6GmXQgvdFTlFok3lOToUtiLAAXX.", "staff_a@system.com", "0920222222", true, staffRole);
		Users userStaffB = createUsersIfNotExists("staff_b", "$2a$10$PZgeOaBXFJsQkIpeZxcoqu88Q/twoGAwT9CviTrxCsI3egT0Kyuqu", "staff_b@system.com", "0930333333", true, staffRole);
		Users userStaffC = createUsersIfNotExists("staff_c", "$2a$10$tjOGZ4XdcdrVeyYd17yOM.DwFBMSJEpag6W.Aw7gpRf.w/XOEOqcC", "staff_c@system.com", "0940444444", true, staffRole);
		Users userStaffD = createUsersIfNotExists("staff_d", "$2a$10$rP1yhH20.2AA/WlMaNnyTeO2U.tbc0WnORGFWo7anPpColB7NBGrK", "staff_d@system.com", "0950555551", true, staffRole);

		Users userP1 = createUsersIfNotExists("public_1", "$2a$10$ewjCrApxXBC5CWDjYBQSd.tltdphrqs/bdYIPdcCW5eS7DiSA.Cji", "public_1@system.com", "0950555552", true, publicRole);
		Users userP2 = createUsersIfNotExists("public_2", "$2a$10$XxHqW2rKSM/AzI4WP3abYe6aaRMFDDrG04CqvH3QCPKLjMfGkif/i", "public_2@system.com", "0950555553", true, publicRole);
		Users userP3 = createUsersIfNotExists("public_3", "$2a$10$xP4HKIGKe7OHGKDjY7V0T.vZ4D2BaZmegdcRyf7mbtoXfacoAkqhe", "public_3@system.com", "0950555554", true, publicRole);
		Users userP4 = createUsersIfNotExists("public_4", "$2a$10$KDZIFuFWYk8BPuMfL0tAvObjX1pI3k6z84Owb77vSsTuaJnYBlKRC", "public_4@system.com", "0950555555", true, publicRole);
		Users userP5 = createUsersIfNotExists("public_5", "$2a$10$mAVVL5uugxz.mYnS1ox2oeE570g8EEhHQScLXHf0VqX7wu4EVZR4.", "public_5@system.com", "0950555556", true, publicRole);

		// 6. 建立 主管/員工 使用者
		createUserAdminIfNotExists(userManager, "主管A", "經理", org1);

		createUserAdminIfNotExists(userStaffA, "員工甲", "專員", org1);
		createUserAdminIfNotExists(userStaffB, "員工乙", "專員", org2);
		createUserAdminIfNotExists(userStaffC, "員工丙", "專員", org3);
		createUserAdminIfNotExists(userStaffD, "員工丁", "專員", org4);

		// 7. 建立 民眾 使用者
		UserPublic userPublic1 = createUserPublicIfNotExists(userP1, "林雅雯", "B234567892", LocalDate.parse("1992-05-15"), "臺中市北區三民路三段129號", "臺中市北區進化路400號5樓");
		UserPublic userPublic2 = createUserPublicIfNotExists(userP2, "黃子軒", "C345678903", LocalDate.parse("1978-08-01"), "臺中市南屯區公益路二段51號", "臺中市南屯區文心路一段200號10樓");
		UserPublic userPublic3 = createUserPublicIfNotExists(userP3, "張靜宜", "D456789014", LocalDate.parse("2001-02-28"), "臺中市東區復興路四段186號", "臺中市東區大勇街10巷8號");
		UserPublic userPublic4 = createUserPublicIfNotExists(userP4, "李冠霖", "E567890125", LocalDate.parse("1965-04-10"), "臺中市豐原區中正路288號", "臺中市豐原區圓環東路150巷9號");
		UserPublic userPublic5 = createUserPublicIfNotExists(userP5, "王怡萱", "F678901236", LocalDate.parse("1998-12-05"), "臺中市大里區國光路二段710號", "臺中市大里區德芳南路300號");

		// 8. 建立 幼兒
		createChildInfoIfNotExists(userPublic1, "張以萱", "H234567812", LocalDate.parse("2021-09-18"), "女" );
		createChildInfoIfNotExists(userPublic1, "張以晨", "H345678923", LocalDate.parse("2023-04-02"), "男");

		createChildInfoIfNotExists(userPublic2, "黃宥睿", "J123456784", LocalDate.parse("2020-12-10"), "男");

		createChildInfoIfNotExists(userPublic3, "陳芷晴", "K567890126", LocalDate.parse("2022-07-25"), "女");
		createChildInfoIfNotExists(userPublic3, "陳芷語", "K678901237", LocalDate.parse("2024-01-14"), "女");

		createChildInfoIfNotExists(userPublic4, "李柏睿", "L789012348", LocalDate.parse("2019-03-22"), "男");

		createChildInfoIfNotExists(userPublic5, "林品希", "M890123459", LocalDate.parse("2020-10-02"), "女");
		createChildInfoIfNotExists(userPublic5, "林品諾", "M901234560", LocalDate.parse("2023-03-30"), "男");

		// 9. 建立 規範
		// ========= 機構 1: 台中市北區陽光寶貝公托家園 =========
		createRegulationIfNotExists(org1, RegulationType.FEE_SCHEDULE, "本機構之費用收費表如下：月費 12,000 元，午餐費 1,200 元，活動材料費依季收取。");
		createRegulationIfNotExists(org1, RegulationType.LUNCH_POLICY, "本機構提供每日三餐點心，午睡時間為 12:30–14:30，由專職老師陪同照護。");
		createRegulationIfNotExists(org1, RegulationType.ATTENDANCE_RULE, "入離園時間為 07:30–18:00。家長若遲到請提前通知，請假以 LINE 官方帳號登記。");
		createRegulationIfNotExists(org1, RegulationType.EMERGENCY_PLAN, "如遇緊急狀況（地震/火災），將依本機構避難路線撤離並立即通知家長。");

		// ========= 機構 2: 台中市西屯區彩虹公立幼兒園 =========
		createRegulationIfNotExists(org2, RegulationType.FEE_SCHEDULE, "本園區收費標準：月費 13,500 元，午餐費 1,000 元。每學期另收教具費 2,000 元。");
		createRegulationIfNotExists(org2, RegulationType.LUNCH_POLICY, "午餐由中央廚房統一配送，午睡時間為 13:00–15:00，採用個人睡墊。");
		createRegulationIfNotExists(org2, RegulationType.ATTENDANCE_RULE, "開放入園時間為 08:00–18:30，遲到需簽到。請假須於當日 9:00 前告知。");
		createRegulationIfNotExists(org2, RegulationType.EMERGENCY_PLAN, "緊急事件發生時，教師將依標準流程疏散至操場集合點，並以電話通知監護人。");

		// ========= 機構 3: 台中市南區智慧樹社區公托 =========
		createRegulationIfNotExists(org3, RegulationType.FEE_SCHEDULE, "本托育中心費用：月費 11,800 元，含點心。午餐費另計 1,100 元，教材費視課程收取。");
		createRegulationIfNotExists(org3, RegulationType.LUNCH_POLICY, "餐點由營養師設計菜單，午睡 12:45–14:15。午睡室提供空氣清淨機。");
		createRegulationIfNotExists(org3, RegulationType.ATTENDANCE_RULE, "開放家長 07:45–18:00 接送，若需加時服務請提前申請並依規定加收費用。");
		createRegulationIfNotExists(org3, RegulationType.EMERGENCY_PLAN, "本中心每季實施一次避難演練，災害發生時依指示至安全區集合並即刻聯繫家長。");

		// ========= 機構 4: 台中市豐原區愛心公立幼兒園 =========
		createRegulationIfNotExists(org4, RegulationType.FEE_SCHEDULE, "本教育機構之收費：月費 14,200 元，午餐費 1,300 元。活動費視參加情形另行收取。");
		createRegulationIfNotExists(org4, RegulationType.LUNCH_POLICY, "提供自製健康餐點，午睡時間為 12:00–14:00，使用固定午睡床並定期清潔。");
		createRegulationIfNotExists(org4, RegulationType.ATTENDANCE_RULE, "入園時間 07:30–17:45。生病需請假者請提供就醫證明，避免傳染給其他幼兒。");
		createRegulationIfNotExists(org4, RegulationType.EMERGENCY_PLAN, "本機構設有緊急聯絡網，重大事件時將同步以簡訊及電話通知家長並啟動疏散程序。");

		// 10. 建立 公告
		// ========= 機構 1: 台中市北區陽光寶貝公托家園 =========
		createAnnouncementIfNotExists(org1, "12 月份課程通知", 
				"親愛的家長您好：本月新增音樂律動課程，詳細內容請參閱課表。", 
				true, LocalDate.parse("2025-12-01"), LocalDate.parse("2025-12-31"));

		createAnnouncementIfNotExists(org1, "春季戶外教學報名開始", 
				"本次將至動物園進行戶外教學，請於 3/10 前完成報名。", 
				true, LocalDate.parse("2025-02-25"), LocalDate.parse("2025-03-15"));

		createAnnouncementIfNotExists(org1, "四月份活動預告（草稿）", 
				"4 月預計舉辦親子運動日，細節將於定案後公告。", 
				false, null, null);

		// ========= 機構 2: 台中市西屯區彩虹公立幼兒園 =========
		createAnnouncementIfNotExists(org2, "園區設備年度消毒", 
				"本園將於 12/10 全面消毒，當日不開放參觀，敬請配合。", 
				true, LocalDate.parse("2025-12-03"), LocalDate.parse("2025-12-17"));

		createAnnouncementIfNotExists(org2, "午餐菜單更新公告", 
				"四月起午餐改採新供應商，菜色更均衡，請家長放心。", 
				true, LocalDate.parse("2025-03-02"), LocalDate.parse("2025-04-30"));

		createAnnouncementIfNotExists(org2, "招生說明會（草稿）", 
				"預計於 5 月舉辦招生說明會，內容尚在修訂中。", 
				false, null, null);

		createAnnouncementIfNotExists(org2, "上課時間調整說明（草稿）", 
				"因應社區交通施工，部分時段可能調整接送動線。", 
				false, null, null);

		// ========= 機構 3: 台中市南區智慧樹社區公托 =========
		createAnnouncementIfNotExists(org3, "校園安全宣導週", 
				"本周安排安全教育課程，請家長提醒孩子共同配合。", 
				true, LocalDate.parse("2025-11-30"), LocalDate.parse("2025-12-07"));

		createAnnouncementIfNotExists(org3, "停水通知", 
				"因區域性檢修，3/12 上午部分設施停止供水，但課程照常進行。", 
				true, LocalDate.parse("2025-03-08"), LocalDate.parse("2025-03-12"));

		createAnnouncementIfNotExists(org3, "家長會議（草稿）", 
				"家長會議初步規劃於 4/20 舉行，細節將後續公布。", 
				false, null, null);

		// ========= 機構 4: 台中市豐原區愛心公立幼兒園 =========
		createAnnouncementIfNotExists(org4, "春假行前提醒", 
				"春假期間請家長留意幼兒作息與安全，假期後正常開課。", 
				true, LocalDate.parse("2025-03-20"), LocalDate.parse("2025-03-31"));

		createAnnouncementIfNotExists(org4, "新學期注意事項", 
				"新學期將啟用新版聯絡簿，請家長於第一週協助填寫資訊。", 
				true, LocalDate.parse("2025-02-15"), LocalDate.parse("2025-04-01"));

		createAnnouncementIfNotExists(org4, "課後才藝課程調整（草稿）", 
				"部分才藝課程正在調整師資與時段，尚未對外公布。", 
				false, null, null);

		createAnnouncementIfNotExists(org4, "攝影紀錄日（草稿）", 
				"預計安排攝影師進行學習紀錄拍攝，詳細流程待確認。", 
				false, null, null);

		// 11. 建立 班級
		// ========= 機構 1: 台中市北區陽光寶貝公托家園 =========
		createClassIfNotExists(org1, "小樹班", 12, 24, 48, 1, 12);

		// ========= 機構 2: 台中市西屯區彩虹公立幼兒園 =========
		createClassIfNotExists(org2, "向日葵班", 15, 36, 60, 1, 12);

		// ========= 機構 3: 台中市南區智慧樹社區公托 =========
		createClassIfNotExists(org3, "彩虹班", 10, 30, 54, 1, 12);

		// ========= 機構 4: 台中市豐原區愛心公立幼兒園 =========
		createClassIfNotExists(org4, "星星班", 12, 24, 72, 1, 12);

	}

	// 檢查 權限 是否存在, 不存在 即建立 的 方法
	private Permission createPermissionIfNotExists(String name, String description) {
		return permissionRepository.findByName(name).orElseGet(() -> {
			Permission permission = new Permission();
			permission.setName(name);
			permission.setDescription(description);
			return permissionRepository.save(permission);
		});
	}

	// 檢查 角色 是否存在, 不存在 即建立 的 方法
	private Role createRoleIfNotExists(String name, String description) {
		return roleRepository.findByName(name).orElseGet(() -> {
			Role role = new Role();
			role.setName(name);
			role.setDescription(description);
			role.setPermissions(new HashSet<Permission>());
			return roleRepository.save(role);
		});
	}

	// 將 角色 和 權限 結合起來的方法
	private void assignPermissionsToRole(Role role, String... permissionNames) {
		Set<Permission> permissions = new HashSet<Permission>();
		for (String permissionName : permissionNames) {
			permissionRepository.findByName(permissionName).ifPresent(permissions::add);
		}
		role.setPermissions(permissions);
		roleRepository.save(role); // 更新角色的權限
	}

	// 檢查 機構 是否存在, 不存在 即建立 的 方法
	private Organization createOrganizationIfNotExists(String name, String description, String address,String email, String phone, String fax) {
		return organizationRepository.findByName(name).orElseGet(() -> {
			Organization organization = new Organization();
			organization.setName(name);
			organization.setDescription(description);
			organization.setAddress(address);
			organization.setEmail(email);
			organization.setPhoneNumber(phone);
			organization.setFax(fax);
			return organizationRepository.save(organization);
		});
	}

	// 檢查 使用者 是否存在, 不存在 即建立 的 方法
	private Users createUsersIfNotExists(String account, String password, String email, String phone, Boolean isActive, Role role) {
		return usersRepository.findByAccount(account).orElseGet(() -> {
			Users users = new Users();
			users.setAccount(account);
			users.setPassword(password);
			users.setEmail(email);
			users.setPhoneNumber(phone);
			users.setRole(role);
			users.setIsActive(isActive);
			return usersRepository.save(users);
		});
	}

	// 檢查 員工使用者 是否存在, 不存在 即建立 的 方法
	private UserAdmin createUserAdminIfNotExists(Users users, String name, String jobTitle, Organization organization) {
		return userAdminRepository.findByUsers(users).orElseGet(() -> {
			UserAdmin userAdmin = new UserAdmin();
			userAdmin.setUsers(users);
			userAdmin.setName(name);
			userAdmin.setJobTitle(jobTitle);
			userAdmin.setOrganization(organization);
			return userAdminRepository.save(userAdmin);
		});
	}

	// 檢查 民眾使用者 是否存在, 不存在 即建立 的 方法
	private UserPublic createUserPublicIfNotExists(Users users, String name, String nationalIdNo, LocalDate birthdate, String registeredAddress, String mailingAddress) {
		return userPublicRepository.findByUsers(users).orElseGet(() -> {
			UserPublic userPublic = new UserPublic();
			userPublic.setUsers(users);
			userPublic.setName(name);
			userPublic.setNationalIdNo(nationalIdNo);
			userPublic.setBirthdate(birthdate);
			userPublic.setRegisteredAddress(registeredAddress);
			userPublic.setMailingAddress(mailingAddress);
			userPublic.setChildren(new HashSet<ChildInfo>());
			return userPublicRepository.save(userPublic);
		});
	}

	// 檢查 幼兒 是否存在, 不存在 即建立 的 方法
	private ChildInfo createChildInfoIfNotExists(UserPublic userPublic, String name, String nationalIdNo, LocalDate birthDate, String gender) {
		return childInfoRepository.findByNationalIdNo(nationalIdNo).orElseGet(() -> {
			ChildInfo childInfo = new ChildInfo();
			childInfo.setUserPublic(userPublic);
			childInfo.setName(name);
			childInfo.setNationalIdNo(nationalIdNo);
			childInfo.setBirthDate(birthDate);
			childInfo.setGender(gender);

			return childInfoRepository.save(childInfo);
		});
	}

	// 檢查 規範 是否存在, 不存在 即建立 的 方法
	private Regulations createRegulationIfNotExists(Organization organization, RegulationType regulationType, String content) {
		return regulationsRepository.findByOrganizationAndType(organization.getOrganizationId(), regulationType.getCode()).orElseGet(() -> {
			Regulations regulations = new Regulations();
			regulations.setOrganization(organization);
			regulations.setType(regulationType);
			regulations.setContent(content);
			return regulationsRepository.save(regulations);
		});
	}

	// 檢查 規範 是否存在, 不存在 即建立 的 方法
	private Announcements createAnnouncementIfNotExists(Organization organization, String title, String content, Boolean isPublished, LocalDate publishDate, LocalDate expiryDate) {
		Announcements announcements = new Announcements();
		announcements.setOrganization(organization);
		announcements.setTitle(title);
		announcements.setContent(content);
		announcements.setIsPublished(isPublished);
		announcements.setPublishDate(publishDate);
		announcements.setExpiryDate(expiryDate);
		return announcementsRepository.save(announcements);
	}

	// 檢查 班級 是否存在, 不存在 即建立 的 方法
	private Classes createClassIfNotExists(Organization organization, String name, Integer maxCapacity, Integer ageMinMonths, Integer ageMaxMonths, Integer serviceStartMonth, Integer serviceEndMonth) {
		return classesRepository.findByNameAndOrganizationId(name, organization.getOrganizationId()).orElseGet(() -> {
			Classes classes = new Classes();
			classes.setOrganization(organization);
			classes.setName(name);
			classes.setMaxCapacity(maxCapacity);
			classes.setAgeMinMonths(ageMinMonths);
			classes.setAgeMaxMonths(ageMaxMonths);
			classes.setServiceStartMonth(serviceStartMonth);
			classes.setServiceEndMonth(serviceEndMonth);
			return classesRepository.save(classes);
		});
	}
}
