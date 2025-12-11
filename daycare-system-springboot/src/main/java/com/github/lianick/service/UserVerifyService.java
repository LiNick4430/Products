package com.github.lianick.service;

import com.github.lianick.model.eneity.Users;

public interface UserVerifyService {

	/** 用於 註冊帳號 忘記密碼 的 方法 */
	void generateUserToken(Users users, String subject, String apiName);
	
	/** 用於 登陸 方法*/
	void generateUserTokenByLogin(Users users, String subject, String apiName);
	
}
