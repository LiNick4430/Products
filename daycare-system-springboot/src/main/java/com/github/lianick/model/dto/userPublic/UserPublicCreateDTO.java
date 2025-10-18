package com.github.lianick.model.dto.userPublic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserPublicCreateDTO {	// 創建 民眾資料的時候用的

	private Long id;						// 帳號ID/民眾ID
	private String username;				// 帳號名
	private Long roleNumber;				// 角色號碼
	
	// 基本資料
	private String name;
	private String nationalIdNo;
	private String birthdate;
	private String registeredAddress;
	private String mailingAddress;
}
