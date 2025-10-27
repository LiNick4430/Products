package com.github.lianick.config;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler{

	/**
	 * 為什麼需要轉發 (Forwarding)？
	 * 
	 * 1. Spring Security 的 Filter 鏈在 Spring MVC 的 DispatcherServlet 之前運行。
	 * 2. 當 AccessDeniedException 在 Filter 鏈中發生時，它不會自動進入 GlobalExceptionHandler。
	 * 3. AccessDeniedHandler 的職責是在 Filter 鏈中攔截這個異常。
	 * 4. 要將控制權交給 Spring MVC，我們必須像處理一個普通的 HTTP 請求一樣，轉發到一個由 DispatcherServlet 處理的 URL (/error/access-denied)。
	 * 5. 當 DispatcherServlet 處理 /error/access-denied 時，它會發現請求屬性中綁定了 AccessDeniedException，從而觸發 @ControllerAdvice 中的 @ExceptionHandler(AccessDeniedException.class)，完成錯誤格式的統一輸出。
	 * 
	 * */
	
	@Override
	// 核心思想：「在 Filter 層面攔截安全異常，然後模擬一個內部請求轉發給 MVC 體系，讓應用程式的統一錯誤處理器接管，從而實現統一的 JSON 錯誤格式輸出。」
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
		
		// 設置狀態碼為 403
        response.setStatus(HttpStatus.FORBIDDEN.value());
        
        // 將 Spring Security 拋出的 AccessDeniedException 綁定到當前請求的屬性中。
        request.setAttribute("jakarta.servlet.error.exception", accessDeniedException);
        
        // 觸發 Spring MVC 異常處理流程。 它將當前的請求和響應物件轉發給 /error/access-denied 這個 Spring MVC 端點。
        request.getRequestDispatcher("/error/access-denied").forward(request, response);
	}

}
