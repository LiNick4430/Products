package com.github.lianick.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterDTO {	// 註冊 民眾帳號
	
	private Long id;
	
	private String email;
	private String phoneNumber;
	
	private String username;
	private String password;
	
	private Long roleNumber;
	
	
}
