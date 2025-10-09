package com.github.lianick.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

import com.github.lianick.exception.UserNoFoundException;
import com.github.lianick.response.ApiResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(UserNoFoundException.class) 	// 指定要捕獲的異常類型
	@ResponseStatus(HttpStatus.UNAUTHORIZED)     	// 設定響應的 HTTP 狀態碼 (401 或 404)
    @ResponseBody                                	// 告訴 Spring 將返回值（ApiResponse）直接寫入 HTTP 響應體
	public ApiResponse<?> handleUserNoFoundException(UserNoFoundException ex) {
		return new ApiResponse<>(false, ex.getMessage(), null);
	}
}
