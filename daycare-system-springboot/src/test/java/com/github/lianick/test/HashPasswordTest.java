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

/*
password: 123456, hash: $2a$10$0PXADQYqxs.AZu/GBr522O5DdO1z2Z6XHlUoNRblyKyW/McKYm1Yq, verify: true
password: 123456, hash: $2a$10$KNzCjc4S99xTAU8r1OpHLO5qs6GmXQgvdFTlFok3lOToUtiLAAXX., verify: true
password: 123456, hash: $2a$10$PZgeOaBXFJsQkIpeZxcoqu88Q/twoGAwT9CviTrxCsI3egT0Kyuqu, verify: true
password: 123456, hash: $2a$10$tjOGZ4XdcdrVeyYd17yOM.DwFBMSJEpag6W.Aw7gpRf.w/XOEOqcC, verify: true
password: 123456, hash: $2a$10$rP1yhH20.2AA/WlMaNnyTeO2U.tbc0WnORGFWo7anPpColB7NBGrK, verify: true
password: 123456, hash: $2a$10$ewjCrApxXBC5CWDjYBQSd.tltdphrqs/bdYIPdcCW5eS7DiSA.Cji, verify: true
password: 123456, hash: $2a$10$XxHqW2rKSM/AzI4WP3abYe6aaRMFDDrG04CqvH3QCPKLjMfGkif/i, verify: true
password: 123456, hash: $2a$10$xP4HKIGKe7OHGKDjY7V0T.vZ4D2BaZmegdcRyf7mbtoXfacoAkqhe, verify: true
password: 123456, hash: $2a$10$KDZIFuFWYk8BPuMfL0tAvObjX1pI3k6z84Owb77vSsTuaJnYBlKRC, verify: true
password: 123456, hash: $2a$10$mAVVL5uugxz.mYnS1ox2oeE570g8EEhHQScLXHf0VqX7wu4EVZR4., verify: true
 * */