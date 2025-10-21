package com.github.lianick.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDTO implements PasswordAwareDTO{		// 帳號登入用
	private Long id;			// 回傳用
	
	private String username;	// 輸入用 帳號名稱
	private String password;	// 輸入用 明文密碼
	private Long roleNumber;	// 角色ID
	private String roleName;	// 角色名
	
	private String token;		// JWT 使用
	
	@Override
	public String getRawPassword() {
		return this.password;
	}
}
