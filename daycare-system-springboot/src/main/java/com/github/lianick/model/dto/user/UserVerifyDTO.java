package com.github.lianick.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserVerifyDTO {	// 帳號 認證用
	private Long id;			// 回傳用
	
	private String token;		// 認證碼
}
