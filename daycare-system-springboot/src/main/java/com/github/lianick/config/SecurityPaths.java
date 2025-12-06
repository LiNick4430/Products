package com.github.lianick.config;

import java.util.List;

public class SecurityPaths {

	public static final List<String> PUBLIC = List.of(
				// 前端工程師 用的 ENUM 接口
				"/enum/document/type",
				"/enum/application/method",
				"/enum/regulation/type",
				"/enum/preference/order",
				"/enum/case/status",
				"/enum/case/organization/status",
				"/enum/lottery/queue/status",
				"/enum/lottery/result/status",
				"/enum/withdrawal/request/status",
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
			"/user/update/**",
		    "/user/delete/**",
		    "/user/me/**",
			
			// UserPublicController
		    "/public/user/me/**",
		    "/public/user/find/all/**",
		    "/public/user/find/**",
		    "/public/user/information/**",
		    "/public/user/update/**",
		    "/public/user/delete/**",	
			
			// UserAdminController
		    "/admin/user/me/**",
		    "/admin/user/find/all/**",
		    "/admin/user/find/**",
		    "/admin/user/create/**",
		    "/admin/user/update/**",
		    "/admin/user/delete/**",
			
			// ChildInfoController
		    "/child/find/all/**",
		    "/child/find/**",
		    "/child/information/**",
		    "/child/update/**",
		    "/child/delete/**",
			
			// DocumentPublicController
		    "/document/public/find/**",
		    "/document/admin/find/**",
		    "/document/case/find/**",
		    "/document/public/create/**",
		    "/document/case/create/**",
		    "/document/public/link/case/**",
		    "/document/admin/verify/**",
		    "/document/public/delete/**",
			
			// AuthContorller
		    "/auth/logout/**",
		    "/auth/check/password/**",
		    "/auth/public/check/password/**",
			
			// OrganizationController
		    "/organization/download/doc/**",
		    "/organization/create/**",
		    "/organization/upload/doc/**",
		    "/organization/update/**",
		    "/organization/delete/doc/**",
		    "/organization/delete/**",
			
			// ClassController
			"/class/find/**",
		    "/class/create/**",
		    "/class/link/case/**",
		    "/class/delete/**",
			
			// AnnouncementController
		    "/announcement/create/**",
		    "/announcement/update/**",
		    "/announcement/upload/doc/**",
		    "/announcement/delete/doc/**",
		    "/announcement/delete/**",
		    "/announcement/find/not/publish/**",
		    "/announcement/publish/**",
			
			// RegulationController
		    "/regulation/create/**",
		    "/regulation/update/**",
		    "/regulation/delete/**",
			
		    // CaseController
		    "/case/create/**"
		    
			// TODO 新的 Controller 添加
		    
		    
			);
}
