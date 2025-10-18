package com.github.lianick.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		http
			// 停用 CSRF 保護，因為我們是 REST API，通常使用 Token 進行驗證
			.csrf(csrf -> csrf.disable())
			
			// 定義請求的授權規則
			.authorizeHttpRequests(authorize -> authorize
					
					// 測試環境 使用 全部通過
					.anyRequest().permitAll()
					
					/*	正式環境 使用 有權限設定
					// 1. 公開端點：允許任何人存取以下路徑
					.requestMatchers(
							"/user/register/",			// 註冊
							"/auth/login/",				// 登入
							"/email/verify",			// 帳號驗證
							"/email/send/password/",	// 發送忘記密碼信
							"/email/reset/password"		// 忘記密碼驗證
					).permitAll()
					
					// 2. 其他所有請求：都必須經過身份驗證
					.anyRequest().authenticated()
					*/
			);
		
		return http.build();
	}
	
	/**
     * 將 BCryptPasswordEncoder 註冊為 Spring Bean
     */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
