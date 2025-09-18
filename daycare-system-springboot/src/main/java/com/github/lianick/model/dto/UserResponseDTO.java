package com.github.lianick.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 查詢 User
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {

	private Long id;					// 對應 User.userId 
	private String name;				// 對應 User.userName
	
	private String account;				// 帳號名稱
	
	private String role;				// 帳號權限等級
	
	private Boolean isActive;			// 帳號是否已啟用
	
}
