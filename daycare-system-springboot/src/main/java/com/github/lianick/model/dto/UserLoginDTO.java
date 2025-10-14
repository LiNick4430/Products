package com.github.lianick.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDTO implements PasswordAwareDTO{		// 帳號登入用
	private String username;	// 帳號名稱
	private String password;	// 明文密碼
	
	@Override
	public String getRawPassword() {
		return this.password;
	}
}
