package com.github.lianick.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 密碼加密與驗證工具類別 
 * 使用 BCrypt 演算法。
 */
@Component
public class PasswordSecurity {

	// 透過 @Autowired 注入 SecurityConfig 中定義的 PasswordEncoder Bean(BCryptPasswordEncoder)
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	// 產生密碼的雜湊值 (含 salt) BCrypt
	// 演算法會自動處理 salt 的產生與雜湊運算。
	// @param rawPassword 未加密的明文密碼
	// @return 雜湊後的密碼字串
	public String hashPassword(String rawPassword) {
		return passwordEncoder.encode(rawPassword);
	}
	
	// 驗證密碼是否正確
	// @param rawPassword 		使用者輸入的明文密碼
	// @param encodedPassword  	資料庫中儲存的雜湊密碼
	// @return 是否相符 True=相符 False=不相符
	public boolean verifyPassword(String rawPassword, String encodedPassword) {
		return passwordEncoder.matches(rawPassword, encodedPassword);
	}
	
	// 測試用
	public static void main(String[] args) {
		
		PasswordSecurity passwordSecurity = new PasswordSecurity();
		
		String password = "1234";	// 明文 密碼
		String hash = passwordSecurity.hashPassword(password);
		System.out.printf("password: %s, hash: %s%n", password, hash);
		
		String password2 = "5678";
		String hash2 = passwordSecurity.hashPassword(password2);
		System.out.printf("password2: %s, hash2: %s%n", password2, hash2);
		
		// 檢測看看 是否相符
		System.out.println(String.valueOf(passwordSecurity.verifyPassword(password, hash)));		// true
		System.out.println(String.valueOf(passwordSecurity.verifyPassword(password, hash2)));	// false
		System.out.println(String.valueOf(passwordSecurity.verifyPassword(password2, hash)));	// false
		System.out.println(String.valueOf(passwordSecurity.verifyPassword(password2, hash2)));	// true
	}
}
