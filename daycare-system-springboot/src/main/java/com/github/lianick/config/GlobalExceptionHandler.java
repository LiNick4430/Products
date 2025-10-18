package com.github.lianick.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

import com.github.lianick.exception.MailSendFailureException;
import com.github.lianick.exception.TokenFailureException;
import com.github.lianick.exception.UserExistException;
import com.github.lianick.exception.UserNoFoundException;
import com.github.lianick.exception.ValueMissException;
import com.github.lianick.response.ApiResponse;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	// @ExceptionHandler(UserNoFoundException.class) 	指定要捕獲的異常類型
	// @ResponseStatus(HttpStatus.UNAUTHORIZED)     	設定響應的 HTTP 狀態碼
    // @ResponseBody                                	告訴 Spring 將返回值（ApiResponse）直接寫入 HTTP 響應體

	// 處理找不到使用者 的 異常 (401)
	@ExceptionHandler(UserNoFoundException.class) 	
	@ResponseStatus(HttpStatus.UNAUTHORIZED)     		// 401
    @ResponseBody                                	
	public ApiResponse<?> handleUserNoFoundException(UserNoFoundException ex) {
		int statusCode = HttpStatus.UNAUTHORIZED.value();
		return new ApiResponse<>(statusCode, ex.getMessage(), null);
	}
	
	// 使用者已經存在 的 異常 (401)
	@ExceptionHandler(UserExistException.class) 	
	@ResponseStatus(HttpStatus.CONFLICT)     			// 409
    @ResponseBody                                	
	public ApiResponse<?> handleUserExistException(UserExistException ex) {
		int statusCode = HttpStatus.CONFLICT.value();
		return new ApiResponse<>(statusCode, ex.getMessage(), null);
	}
	
	// 無法發送電子信箱 的 異常 (500)
	@ExceptionHandler(MailSendFailureException.class) 		
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)   // 500
    @ResponseBody                                		
	public ApiResponse<?> handleMailSendFailureException(MailSendFailureException ex) {
		int statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
		return new ApiResponse<>(statusCode, "服務器內部錯誤：無法發送電子郵件，請稍後重試。" , null);
	}
	
	// token 驗證相關 的 錯誤 (1. 無效不存在 2. 過期 3. 已經被使用)
	@ExceptionHandler(TokenFailureException.class) 		
	@ResponseStatus(HttpStatus.BAD_REQUEST)   			// 400
    @ResponseBody                                		
	public ApiResponse<?> handleTokenFailureException(TokenFailureException ex) {
		int statusCode = HttpStatus.BAD_REQUEST.value();
		return new ApiResponse<>(statusCode, ex.getMessage() , null);
	}
	
	// 回報數值缺少 相關 的 錯誤 
	@ExceptionHandler(ValueMissException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)   			// 400
    @ResponseBody                                		
	public ApiResponse<?> handleValueMissException(ValueMissException ex) {
		int statusCode = HttpStatus.BAD_REQUEST.value();
		return new ApiResponse<>(statusCode, ex.getMessage() , null);
	}
}
