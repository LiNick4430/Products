package com.github.lianick.mail;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import jakarta.transaction.Transactional;

@SpringBootTest		// 載入 Spring Boot 應用程式的完整上下文
@Transactional		// 確保整個測試類別在事務中運行
public class MailTest {

	@Autowired
	private JavaMailSender javaMailSender;
	
	@Test
	// TODO 預設為寫入測試：強制提交事務，不回滾。	如果只是讀取/查詢測試，請移除 @Rollback(false)
	// @Rollback(false)	
	public void sendEmail() {	// 僅 測試 成功發信

		// 帳號 認證碼
		String token = "";
		// 目標信箱
		String toEmail = "test@xxx.com";
		
		// 建立 簡易 訊息
		SimpleMailMessage message = new SimpleMailMessage();
		// 預設 認證服務
		String verfityURL = "http://localhost:8080/api/users/verify?token=" + token;
		
		message.setFrom("test@daycare.com");
		message.setTo(toEmail);
		message.setSubject("帳號啟用信件");
		message.setText("請點擊以下連結已啟用帳號:\n" + verfityURL);
		
		javaMailSender.send(message);
	}
}
