package com.github.lianick.config;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lianick.exception.ErrorCode;
import com.github.lianick.response.ApiResponse;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint{	// AuthenticationEntryPoint 負責處理 「未經認證的請求」或「認證失敗的請求」。

	private final ObjectMapper objectMapper = new ObjectMapper();
	
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		
		// 1. 檢查 JwtAuthenticationFilter 中設定的錯誤屬性
		Object error = request.getAttribute(JwtAuthenticationFilter.AUTH_ERROR_ATTRIBUTE);
		
		// 默認錯誤訊息
		ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
		String errorMessage = "Access Denied / Not Authenticated";
		
		if (error != null) {
			// 2. 如果是 Access Token 過期
			if (error instanceof ExpiredJwtException) {
				errorCode = ErrorCode.JWT_EXPIRED; // 前端會根據這個代碼觸發 Refresh Token
				errorMessage = "Access Token is expired";
			}
			// 3. 如果是 JWT 相關錯誤(格式錯誤 簽名錯誤)
			// 因為會彈出 JwtException | IllegalArgumentException 用 Exception 接
			else if (error instanceof Exception) {
				errorCode = ErrorCode.JWT_INVALID;
                errorMessage = "Invalid JWT Token or signature";
			}
		}
		
		// 4. 設定 HTTP 401 狀態碼
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		
		// 5. 設定 Content Type 為 JSON
		response.setContentType("application/json;charset=UTF-8");
		
		// 6. 寫入 Json 回應
		ApiResponse<?> errorResponse = ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), errorCode, errorMessage);
		
		response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
		response.getWriter().flush();
	}
}
