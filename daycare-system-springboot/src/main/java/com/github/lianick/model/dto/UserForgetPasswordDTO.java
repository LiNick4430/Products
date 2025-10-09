package com.github.lianick.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserForgetPasswordDTO {	// 忘記密碼 用
	private Long id;
	private String email;
	private String phoneNumber;
	
	private String username;
}
