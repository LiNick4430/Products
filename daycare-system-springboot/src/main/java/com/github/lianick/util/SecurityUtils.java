package com.github.lianick.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;

/**
 * 處理 Spring Security 上下文相關操作的工具類別。
 */
@Component
public class SecurityUtils {

	// 防止外部實例化
	private SecurityUtils() {}
	
	/**
     * 從當前的 SecurityContext 中獲取已認證的使用者帳號。
     * @return 當前登入的使用者帳號 (通常是帳號名稱或 Email)。
     * @throws IllegalStateException 如果使用者未認證。
     */
	public static String getCurrentUsername() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            // 在您已經配置 JwtAuthenticationEntryPoint 處理 401 的情況下，
            // 正常情況下這裡不應該被觸發，但作為工具方法，應包含基本檢查。
            throw new IllegalStateException("無法獲取已認證的使用者，SecurityContext 為空或未認證。");
        }
		
		// getName() 通常回傳 Principal 的字串表示，對於 Spring Security 的 UserDetails 來說，就是帳號名稱。
		return authentication.getName();
	}
	
	/**
     * 從當前的 SecurityContext 中獲取已認證的使用者帳號。
     * @return 當前登入的 Role Number。
     * @throws IllegalStateException 如果使用者未認證。
     */
	public static Long getCurrentRoleNumber() {
		Authentication authentication = getCurrentAuthentication();
	    
	    if (authentication == null || !authentication.isAuthenticated()) {
	        throw new IllegalStateException("無法獲取已認證的使用者角色ID。");
	    }

	    // 從 Details 中獲取 Claims
	    Object details = authentication.getDetails();

	    if (details instanceof Claims) {
	        Claims claims = (Claims) details;
	        Number roleNumber = (Number) claims.get("roleNumber"); // 從 Claims 中提取
	        if (roleNumber != null) {
	            return roleNumber.longValue();
	        }
	    }
	    
	    // 如果 Details 不是 Claims (或為 null)，則拋出錯誤
	    throw new IllegalStateException("無法從 Authentication Details 中識別或解析角色ID。");
	}
	
	/**
     * 從當前的 SecurityContext 中獲取 Authentication。
     * @return 當前登入的 Authentication。
     */
    public static Authentication getCurrentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
	
}
