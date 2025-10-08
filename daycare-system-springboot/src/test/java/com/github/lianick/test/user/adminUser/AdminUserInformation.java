package com.github.lianick.test.user.adminUser;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import com.github.lianick.model.eneity.Organization;
import com.github.lianick.model.eneity.UserAdmin;
import com.github.lianick.model.eneity.Users;
import com.github.lianick.repository.OrganizationRepository;
import com.github.lianick.repository.UserAdminRepository;
import com.github.lianick.repository.UsersRepository;

import jakarta.transaction.Transactional;

// 當通過驗證的帳號 就可以填寫基本資料
@SpringBootTest
@Transactional
public class AdminUserInformation {

	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private UserAdminRepository userAdminRepository;
	
	@Autowired
	private OrganizationRepository organizationRepository;
	
	@Test
	@Rollback(false)	// 強制提交事務，不回滾
	public void setInformation() {
		// 測試用變數
		Long userId = 3L;
		Long organizationId = 1L;
		
		// 查詢 該 ID 是否已經啟用 且 並未和 UserPublic/UserAdmin 連結
		Optional<Users> optUser = usersRepository.findActiveAdminUserById(userId);
		// 查詢 Organization ID 是否存在
		Optional<Organization> optOrganization = organizationRepository.findById(organizationId);
		if (optUser.isEmpty()) {
			System.out.printf("User id = %d 未通過認證或不存在\n", userId);
			return;
		}
		if (optOrganization.isEmpty()) {
			System.out.printf("Organization id = %d 不存在\n", organizationId);
			return;
		}
		Users user = optUser.get();
		Organization organization = optOrganization.get();
		
		// 檢查 ID 是否 重複創建
		Optional<UserAdmin> existingAdminInfo = userAdminRepository.findByUsers(user);
		if (existingAdminInfo.isPresent()) {
			System.out.printf("員工 ID = %d 的基本資料已存在，無需重複填寫。\n", userId);
			return;
		}
		
		// 員工基本資料
		UserAdmin userAdmin = new UserAdmin();
		userAdmin.setUsers(user);
		userAdmin.setName("員工A");
		userAdmin.setJobTitle("櫃台");
		userAdmin.setOrganization(organization);
		
		userAdminRepository.save(userAdmin);
		
		System.out.println("基本資料填寫成功");
	}
}
