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
	
	@Autowired
	private CustomAccessDeniedHandler customAccessDeniedHandler;
	
	/**
     * 將 BCryptPasswordEncoder 註冊為 Spring Bean
     */
	@Bean	// @Bean 預設 Public
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean	// @Bean 預設 Public
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		http
			// 1. 停用 CSRF 保護 (JWT 認證不需要 Session)
			.csrf(csrf -> csrf.disable())
			
			// 2. CORS 設定（使用預設）
			.cors(Customizer.withDefaults())
			
			
            .exceptionHandling(e -> e
            		// 認證失敗
            		// 401 錯誤處理：將 認證失敗/未認證 的錯誤交給 EntryPoint 處理	(仍然在 Security Filter)
            		.authenticationEntryPoint(jwtAuthenticationEntryPoint)	
            		
            		// 授權失敗
            		// 403 錯誤處理：將 權限不足 的錯誤交給 CustomAccessDeniedHandler 處理	(已經進入 DispatcherServlet)
            		.accessDeniedHandler(customAccessDeniedHandler)
            		)
			
			// 3. 設置授權規則
			.authorizeHttpRequests(authorize -> authorize
				
					// 1. 公開端點：允許任何人存取以下路徑
					.requestMatchers(
							"/error/access-denied",								// 錯誤處理
							"/user/register/", "/user/register",				// 註冊
							"/user/reset/password/", "/user/reset/password",	// (忘記密碼)重設密碼
							"/auth/login/",	"/auth/login",						// 登入
							"/auth/access/token/refresh/", "/auth/access/token/refresh",	// access token 刷新
							"/email/send/password/", "/email/send/password",				// 發送忘記密碼信
							"/email/verify",			// 帳號驗證
							"/email/reset/password",		// 忘記密碼驗證
							"/organization/find/", "/organization/find"	// 尋找 特定機構資料
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
