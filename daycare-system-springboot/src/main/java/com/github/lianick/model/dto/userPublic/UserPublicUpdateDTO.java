package com.github.lianick.model.dto.userPublic;

import com.github.lianick.model.dto.user.PasswordAwareDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserPublicUpdateDTO implements PasswordAwareDTO{	// 更新 民眾資料的時候用的

	private String username;				// 帳號名
	private String password;				// 1. 密碼驗證用
	
	// 2. 更新 所需要的資料
	private String newName;
	private String newRegisteredAddress;
	private String newMailingAddress;
	
	@Override
	public String getRawPassword() {
		return this.password;
	}
	
}
