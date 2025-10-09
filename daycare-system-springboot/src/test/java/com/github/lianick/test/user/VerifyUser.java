package com.github.lianick.test.user;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import com.github.lianick.model.eneity.Users;
import com.github.lianick.repository.UsersRepository;

import jakarta.transaction.Transactional;

// 驗證信箱成功後 啟用帳號
@SpringBootTest
@Transactional
public class VerifyUser {

	@Autowired
	private UsersRepository usersRepository;
	
	@Test
	@Rollback(false)
	public void verify() {
		// 測試用變數
		Long id = 8L;
		
		Optional<Users> optUser = usersRepository.findById(id);
		if (optUser.isEmpty()) {
			System.out.println("此 ID 不存在");
			return;
		}
		Users user = optUser.get();
		
		if (user.getIsActive().equals(true)) {
			System.out.println("ID 己經 驗證完成");
			return;
		}
		
		user.setIsActive(true);
		user.setActiveDate(LocalDateTime.now());
		
		usersRepository.save(user);
		
		System.out.println("帳號啟用成功");
		
	}
	
}
