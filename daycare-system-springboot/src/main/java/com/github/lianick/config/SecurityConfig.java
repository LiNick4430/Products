package com.github.lianick.config;

import java.util.List;

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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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
	
	@Autowired
	private FrontendProperties frontendProperties;
	/**
     * 將 BCryptPasswordEncoder 註冊為 Spring Bean
     */
	@Bean	// @Bean 預設 Public
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	/**
	 * 集中處理 Controller 上的 @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
	 * */
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		
		// 允許前端的來源 ( 前端來源 )
		configuration.setAllowedOrigins(List.of(frontendProperties.getUrl()));
		
		// 允許的方法 (全部)
		configuration.setAllowedMethods(List.of("*"));
		
		// 允許的標頭 (全部)
		configuration.setAllowedHeaders(List.of("*"));
		
		// 允許帶有憑證 為了給 JWT使用
		configuration.setAllowCredentials(true);
		
		// 註冊到所有路徑
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		
		return source;
	}
	
	/**
	 * 安全設定 過濾使用的方法
	 * */
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		http
			// 1. 停用 CSRF 保護 (JWT 認證不需要 Session)
			.csrf(csrf -> csrf.disable())
			
			// 2. CORS 設定
			//	使用上面定義的 corsConfigurationSource() Bean
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
				
					// 0. API 文件使用 ： 允許任何人存取以下路徑
					.requestMatchers(
							SecurityPaths.DOCS.toArray(new String[0])
					).permitAll()
					
					// 1. 公開 API ： 允許任何人存取以下路徑
					.requestMatchers(
							SecurityPaths.PUBLIC.toArray(new String[0])
					).permitAll()
					
					// 2. 認證 API ： 都必須經過身份驗證
					.requestMatchers(
							SecurityPaths.AUTHENTICATED.toArray(new String[0])
					).authenticated()
					
					// 3. 其他未定義路徑
					.anyRequest().denyAll()
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
