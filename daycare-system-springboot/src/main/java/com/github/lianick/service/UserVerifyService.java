package com.github.lianick.service;

import com.github.lianick.model.eneity.Users;

public interface UserVerifyService {

	/** 產生 帳號驗證碼 同時 寄出驗證信-私用方法 */
	// void generateUserTokenAndSendEmail(Users users, String subject, String apiName);
	
	/** 用於 註冊帳號 忘記密碼 的 方法 */
	void generateUserToken(Users users, String subject, String apiName);
	
	/** 用於 登陸 方法*/
	void generateUserTokenByLogin(Users users, String subject, String apiName);
	
}
