package com.github.lianick.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.github.lianick.model.eneity.Permission;
import com.github.lianick.model.eneity.Role;
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
		
		// 1. 建立權限 (如果已存在可以先檢查)
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
        
        // 2. 建立角色
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
}
