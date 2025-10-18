package com.github.lianick.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDeleteDTO implements PasswordAwareDTO{	// 刪除用
	private String username;
	private String password;
	
	@Override
	public String getRawPassword() {
		return this.password;
	}
}
