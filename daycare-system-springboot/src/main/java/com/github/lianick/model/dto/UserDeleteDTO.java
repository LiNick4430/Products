package com.github.lianick.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDeleteDTO {	// 刪除用
	private Long id;
	private String email;
	private String phoneNumber;
	
	private String username;
	private String password;
}
