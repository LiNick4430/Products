package com.github.lianick.model.dto.organization;

import com.github.lianick.model.dto.user.PasswordAwareDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationDeleteDTO implements PasswordAwareDTO{

	private Long id;			// 機構ID
	
	private String username;	// 刪除者的帳號
	private String password;	// 刪除者的密碼

	@Override
	public String getRawPassword() {
		return this.password;
	}
	
}
