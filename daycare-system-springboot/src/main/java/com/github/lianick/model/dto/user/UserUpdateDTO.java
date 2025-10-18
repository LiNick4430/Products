package com.github.lianick.model.dto.user;

import com.github.lianick.model.dto.PasswordAwareDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDTO implements PasswordAwareDTO {	// 更新資料用
	private Long id;

	private String email;				// 從資料庫取出的 舊信箱
	private String phoneNumber;			// 從資料庫取出的 舊電話號碼

	private String newPhoneNumber;		// 用於 4. update
	
	private String username;
	private String password;			// 用於 1. checkPassword
	private String newPassword;			// 用於 4. update 
	
	@Override
	public String getRawPassword() {
		return this.password;
	}
}
