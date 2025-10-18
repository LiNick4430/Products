package com.github.lianick.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.github.lianick.exception.MailSendFailureException;
import com.github.lianick.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {

	@Autowired
	private JavaMailSender javaMailSender;
	
	@Value("${spring.mail.username}")
	private String fromEmail;
	
	@Override
	public void sendVerificationEmail (String toEmail, String subject, String verificationLink){
		SimpleMailMessage message = new SimpleMailMessage();
		
		message.setFrom(fromEmail);
		message.setTo(toEmail);
		message.setSubject(subject);
		
		String mailContext = 
				"您好，請點擊請點擊以下連結:\n\n"
				+ verificationLink
				+ "\n\n此連結將在 15 分鐘後失效。";
		
		message.setText(mailContext);
		
		try {
			javaMailSender.send(message);
		} catch (Exception e) {
			throw new MailSendFailureException("無法發送認證信，請檢查伺服器配置。", e);
		}
		
	}
}
