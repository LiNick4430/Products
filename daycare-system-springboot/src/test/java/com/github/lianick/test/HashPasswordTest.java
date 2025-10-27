package com.github.lianick.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.github.lianick.util.PasswordSecurity;

@SpringBootTest		// 載入 Spring Boot 應用程式的完整上下文
public class HashPasswordTest {		// 用於生成 預備帳號的 HASH

	@Autowired
	private PasswordSecurity passwordSecurity;
	
	@Test	
	public void generatePasswordHash() {
		String rawPassword = "123456";
		
		for (int i = 0; i < 10; i++) {
			String hash = passwordSecurity.hashPassword(rawPassword);
			String verify = String.valueOf(passwordSecurity.verifyPassword(rawPassword, hash));
			System.out.printf("password: %s, hash: %s, verify: %s%n", rawPassword, hash, verify);
		}
		
	}
}
