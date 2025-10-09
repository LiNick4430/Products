package com.github.lianick.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterDTO {	// 註冊用
	private String email;
	private String phoneNumber;
	
	private String username;
	private String password;
	
	private Long roleNumber;
}
