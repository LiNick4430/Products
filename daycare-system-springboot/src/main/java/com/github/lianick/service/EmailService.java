package com.github.lianick.service;

// 定義 郵件服務的契約
public interface EmailService {

	/**
     * 發送帳號認證郵件
     * @param toEmail 收件者信箱
     * @param subject 郵件主旨
     * @param verificationLink 認證連結（包含 Token）
     */
	void sendVerificationEmail(String toEmail, String subject, String verificationLink);
}
