package com.github.lianick.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)	// 保持 @PreAuthorize 和 @PostAuthorize 的功能
public class SecurityConfig {

	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;
	
	@Autowired
	private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	
	/**
     * 將 BCryptPasswordEncoder 註冊為 Spring Bean
     */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		http
			// 1. 停用 CSRF 保護 (JWT 認證不需要 Session)
			.csrf(csrf -> csrf.disable())
			
			// 2. CORS 設定（使用預設）
			.cors(Customizer.withDefaults())
			
			// 【新】401 錯誤處理：將認證失敗/未認證的錯誤交給 EntryPoint 處理
            .exceptionHandling(e -> e.authenticationEntryPoint(jwtAuthenticationEntryPoint))
			
			// 3. 設置授權規則
			.authorizeHttpRequests(authorize -> authorize
				
					// 1. 公開端點：允許任何人存取以下路徑
					.requestMatchers(
							"/user/register/",			// 註冊
							"/user/reset/password/",	// (忘記密碼)重設密碼
							"/auth/login/",				// 登入
							"/email/verify",			// 帳號驗證
							"/email/send/password/",	// 發送忘記密碼信
							"/email/reset/password"		// 忘記密碼驗證
					).permitAll()
					
					// 2. 其他所有請求：都必須經過身份驗證
					.anyRequest().authenticated()
			)
			
			// 4. 設置 Session 為無狀態
			.sessionManagement(session -> 
				session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)

			// 5. 加入 JWT 過濾器
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
	
}
