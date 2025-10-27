package com.github.lianick.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomErrorController {

	// 不需要實際執行任何業務邏輯
    // 被 CustomAccessDeniedHandler 轉發 到這個位置
    // 讓 GlobalExceptionHandler 捕獲到 request.getAttribute("jakarta.servlet.error.exception") 中的異常。
    @RequestMapping("/error/access-denied")
    public void handleAccessDeniedTransfer() {
        // 一個空方法 
    	// GlobalExceptionHandler 會接管
    }
}
