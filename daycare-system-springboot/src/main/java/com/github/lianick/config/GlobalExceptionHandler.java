package com.github.lianick.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

import com.github.lianick.exception.MailSendFailureException;
import com.github.lianick.exception.UserNoFoundException;
import com.github.lianick.response.ApiResponse;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	// @ExceptionHandler(UserNoFoundException.class) 	指定要捕獲的異常類型
	// @ResponseStatus(HttpStatus.UNAUTHORIZED)     	設定響應的 HTTP 狀態碼
    // @ResponseBody                                	告訴 Spring 將返回值（ApiResponse）直接寫入 HTTP 響應體

	// 處理找不到使用者異常 (401 或 404)
	@ExceptionHandler(UserNoFoundException.class) 	
	@ResponseStatus(HttpStatus.UNAUTHORIZED)     		// 401 或 404
    @ResponseBody                                	
	public ApiResponse<?> handleUserNoFoundException(UserNoFoundException ex) {
		return new ApiResponse<>(false, ex.getMessage(), null);
	}
	
	// 無法發送電子信箱的異常 (401 或 404)
	@ExceptionHandler(MailSendFailureException.class) 		
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)   // 500
    @ResponseBody                                		
	public ApiResponse<?> handleMailSendFailureException(MailSendFailureException ex) {
		return new ApiResponse<>(false, "服務器內部錯誤：無法發送電子郵件，請稍後重試。" , null);
	}
}
