package com.github.lianick.test.user;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import com.github.lianick.model.eneity.UserVerify;
import com.github.lianick.model.eneity.Users;
import com.github.lianick.repository.UsersRepository;
import com.github.lianick.repository.UsersVerifyRepository;

import jakarta.transaction.Transactional;

// 驗證信箱成功後 啟用帳號
@SpringBootTest
@Transactional
public class VerifyUser2 {

	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private UsersVerifyRepository usersVeriftyRepository;
	
	@Test
	@Rollback(false)
	public void verify() {
		// 測試用變數
		String token = "35ac823fff89454ab835c8a389e66bfa";
		LocalDateTime now = LocalDateTime.now();
		
		// 1. 使用 Token 紀錄 找尋 UsersVerify 紀錄
		Optional<UserVerify> optUserVerify = usersVeriftyRepository.findByToken(token);
		if (optUserVerify.isEmpty()) {
			System.out.println("驗證碼無效或不存在");
		    return;
		}
		UserVerify userVerify = optUserVerify.get();
		Users users = userVerify.getUsers();
		
		// 2. 判斷 Token 狀態
		if (userVerify.getIsUsed()) {
			System.out.println("驗證碼已經被使用");
		    return;
		}
		if (userVerify.getExpiryTime().isBefore(now)) {
			System.out.println("驗證碼已經過期");
		    return;
		}
		
		// 3. 判斷 Users 狀態 以防止重複使用
		if (users.getIsActive()) {
			System.out.println("帳號已經驗證完成");
		    return;
		}
		
		// 4. 啟用帳號
		userVerify.setIsUsed(true);
		users.setIsActive(true);
		users.setActiveDate(now);
		
		usersVeriftyRepository.save(userVerify);
		usersRepository.save(users);
		
		System.out.println("帳號啟用成功");
		
	}
	
}
