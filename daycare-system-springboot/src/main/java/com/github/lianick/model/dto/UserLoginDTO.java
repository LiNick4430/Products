package com.github.lianick.model.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UserLoginDTO {		// 帳號登入用
	private String username;	// 帳號名稱
	private String rawPassword;	// 明文密碼
}
