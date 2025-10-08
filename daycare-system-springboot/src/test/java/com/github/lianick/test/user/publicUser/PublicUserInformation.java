package com.github.lianick.test.user.publicUser;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import com.github.lianick.model.eneity.UserPublic;
import com.github.lianick.model.eneity.Users;
import com.github.lianick.repository.UserPublicRepository;
import com.github.lianick.repository.UsersRepository;

import jakarta.transaction.Transactional;

// 當通過驗證的帳號 就可以填寫基本資料
@SpringBootTest
@Transactional
public class PublicUserInformation {

	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private UserPublicRepository userPublicRepository;
	
	@Test
	@Rollback(false)	// 強制提交事務，不回滾
	public void setInformation() {
		// 測試用變數
		Long userId = 2L;
		
		// 查詢 該 ID 是否已經啟用 且 並未和 UserPublic/UserAdmin 連結
		Optional<Users> optUser = usersRepository.findActivePublicUserById(userId);
		if (optUser.isEmpty()) {
			System.out.printf("id = %d 未通過認證或不存在\n", userId);
			return;
		}
		Users user = optUser.get();
		
		// 檢查 是否 重複創建
		Optional<UserPublic> existingPublicInfo = userPublicRepository.findByUsers(user);
		if (existingPublicInfo.isPresent()) {
			System.out.printf("用戶 ID = %d 的基本資料已存在，無需重複填寫。\n", userId);
		}
		
		// 民眾基本資料
		UserPublic userPublic = new UserPublic();
		userPublic.setUsers(user);
		userPublic.setName("民眾B");
		userPublic.setNationalIdNo("民眾B");		// 測試用
		userPublic.setBirthdate(LocalDate.parse("1991-01-01"));
		userPublic.setRegisteredAddress("台中市");
		userPublic.setMailingAddress("台中市");
		
		userPublicRepository.save(userPublic);
		
		System.out.println("基本資料填寫成功");
	}
}
