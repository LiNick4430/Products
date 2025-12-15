package com.github.lianick.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@RestController
public class CustomErrorController {

	// 不需要實際執行任何業務邏輯	
	// 會被 任何 HTTP 請求使用
    // 被 CustomAccessDeniedHandler 轉發 到這個位置
    // 讓 GlobalExceptionHandler 捕獲到 request.getAttribute("jakarta.servlet.error.exception") 中的異常。
	@RequestMapping(value = "/error/access-denied", 
            method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PATCH,
                      RequestMethod.PUT, RequestMethod.DELETE})
    public void handleAccessDeniedTransfer() {
        // 一個空方法 
    	// GlobalExceptionHandler 會接管
    }
}
