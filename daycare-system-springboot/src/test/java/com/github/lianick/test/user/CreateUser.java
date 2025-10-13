package com.github.lianick.test.user;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.annotation.Rollback;

import com.github.lianick.model.eneity.Role;
import com.github.lianick.model.eneity.UserVerify;
import com.github.lianick.model.eneity.Users;
import com.github.lianick.repository.RoleRepository;
import com.github.lianick.repository.UsersRepository;
import com.github.lianick.repository.UsersVerifyRepository;
import com.github.lianick.util.PasswordSecurity;
import com.github.lianick.util.TokenUUID;

import jakarta.transaction.Transactional;

// 申請帳號
@SpringBootTest
@Transactional
public class CreateUser {

	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private UsersVerifyRepository usersVeriftyRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	@Autowired
	private PasswordSecurity passwordSecurity;
	
	@Autowired
	private TokenUUID tokenUUID;
	
	@Test
	@Rollback(false)
	public void create() {		// 申請帳號流程
		// 0. 變數設定
		// 根據 角色 設定權限
		Role ROLE_PUBLIC = roleRepository.findByName("ROLE_PUBLIC").get();		// 民眾
		Role ROLE_STAFF = roleRepository.findByName("ROLE_STAFF").get();		// 基層
		Role ROLE_MANAGER = roleRepository.findByName("ROLE_MANAGER").get();	// 管理
		// 帳號設定
		String account = "test01";
		String email = account + "@xxx.com";		// 方便測試 email 和 帳號 同名
		String phone = "0900111222";
		// 密碼加密
		String rawPassword = "123456";
		String hashPassord = passwordSecurity.hashPassword(rawPassword);
		// 認證碼
		String token = tokenUUID.generateToekn(); 
		// 驗證碼時間
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime expiryTime = now.plusMinutes(15);		// 過期時間 + 15分
		
		// 1. 建立 帳號
		Users user = new Users();
		user.setEmail(email);
		user.setPhoneNumber(phone);
		user.setAccount(account);
		user.setPassword(hashPassord);
		user.setRole(ROLE_PUBLIC);
		
		usersRepository.save(user);
		
		// 2. 同時 建立 帳號啟動驗證碼
		UserVerify userVerify = new UserVerify();
		userVerify.setToken(token);
		userVerify.setExpiryTime(expiryTime);
		userVerify.setUsers(user);
		
		usersVeriftyRepository.save(userVerify);
		
		// 3.寄出 認證碼		
		SimpleMailMessage message = new SimpleMailMessage();
		String verfityURL = "http://localhost:8080/api/users/verify?token=" + userVerify.getToken();	// 預設 認證 網址
		
		message.setFrom("test@daycare.com");
		message.setTo(user.getEmail());
		message.setSubject("帳號啟用信件");
		message.setText("請點擊以下連結已啟用帳號:\n" + verfityURL);
		
		javaMailSender.send(message);
		
		System.out.println("申請帳號成功");
	}
	
}
