package com.github.lianick.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserVerifyDTO {	// 帳號 認證用

	private String username;	// 被認證 帳號
	
	private String toekn;		// 認證碼
}
