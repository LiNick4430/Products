package com.github.lianick.config;

import java.util.List;

public class SecurityPaths {

	public static final List<String> PUBLIC = List.of(
				// 前端工程師 用的 ENUM 接口
				"/enum/**",
				// 錯誤處理 相關
				"/error/access-denied",
				// 使用者 相關
				"/user/register/**",		// 註冊
				"/user/reset/password/**",	// (忘記密碼)重設密碼
				// 認證 相關
				"/auth/login/**",					// 登入
				"/auth/access/token/refresh/**",	// access token 刷新
				// 信箱 相關
				"/email/send/password/**",	// 發送忘記密碼信
				"/email/verify",			// 帳號驗證
				"/email/reset/password",	// 忘記密碼驗證
				// 機構 相關
				"/organization/find/**",	// 尋找 特定機構資料
				// 公告 相關
				"/announcement/find/all/**",		// 搜尋 全部公告
				"/announcement/find/**",			// 搜尋 特定公告
				"/announcement/download/doc/**",	// 下載 公告附件
				// 規範 相關
				"/regulation/find/all/**",	// 搜尋 全部規範
				"/regulation/find/**"		// 搜尋 特定規範
				
				// TODO 新的 public 添加
			);
	
	public static final List<String> AUTHENTICATED = List.of(
			// UserController
			"/user/**",
			
			// UserPublicController
			"/public/user/**",	
			
			// UserAdminController
			"/admin/user/**",
			
			// ChildInfoController
			"/child/**",
			
			// DocumentPublicController
			"/document/**",
			
			// AuthContorller
			"/auth/**",
			
			// OrganizationController
			"/organization/**",
			
			// ClassController
			"/class/**",
			
			// AnnouncementController
			"/announcement/**",
			
			// RegulationController
			"/regulation/**"
			
			// TODO 新的 Controller 添加
			
			);
}
