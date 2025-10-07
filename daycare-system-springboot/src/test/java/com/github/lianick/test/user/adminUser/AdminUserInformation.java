package com.github.lianick.test.user.adminUser;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import com.github.lianick.model.eneity.UserAdmin;
import com.github.lianick.model.eneity.UserPublic;
import com.github.lianick.model.eneity.Users;
import com.github.lianick.repository.UserAdminRepository;
import com.github.lianick.repository.UsersRepository;

import jakarta.transaction.Transactional;

// 當通過驗證的帳號 就可以填寫基本資料
@SpringBootTest
//確保整個測試類別在事務中運行
@Transactional
public class AdminUserInformation {

	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private UserAdminRepository userAdminRepository;
	
	@Test
	@Rollback(false)	// 強制提交事務，不回滾
	public void setInformation() {
		// 測試用變數
		Long userId = 3L;
		
		// 查詢 該 ID 是否已經啟用 且 並未和 UserPublic/UserAdmin 連結
		Optional<Users> optUser = usersRepository.findActiveAdminUserById(userId);
		if (optUser.isEmpty()) {
			System.out.printf("id = %d 未通過認證或不存在\n", userId);
			return;
		}
		Users user = optUser.get();
		
		// 檢查 是否 重複創建
		Optional<UserPublic> existingPublicInfo = userAdminRepository.findByUsers(user);
		if (existingPublicInfo.isPresent()) {
			System.out.printf("用戶 ID = %d 的基本資料已存在，無需重複填寫。\n", userId);
		}
		
		// 員工基本資料
		UserAdmin userAdmin = new UserAdmin();
		userAdmin.setUsers(user);
		userAdmin.setName("員工A");
		
		
		System.out.println("基本資料填寫成功");
	}
}
