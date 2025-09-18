package com.github.lianick.model.dto;

import lombok.Getter;
import lombok.Setter;

// User 登入時 用來檢測 是否登入成功的
@Getter
@Setter
public class UserLoginDTO {
	private String account;				// 帳號名稱
	private String password;			// 未加密的明文密碼 加密後 對應 User.password
}
