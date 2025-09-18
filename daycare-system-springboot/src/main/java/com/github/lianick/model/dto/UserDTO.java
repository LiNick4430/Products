package com.github.lianick.model.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

	private Long userId;				// 系統產生的 流水編號
	private String name;				// 姓名
	private String nationalIdNo; 		// 身分證字號
	private LocalDateTime birthdate;	// 生日
	
	private String registeredAddress;	// 戶籍地址
	private String mailingAddress;		// 通訊地址
	private String email;				// e-mail
	private String phoneNumber;			// 手機號碼
	
	private String account;				// 帳號名稱
	private String password;			// 密碼(hash)
	
	private String role;				// 帳號權限等級
	
	private Boolean isActive;			// 帳號是否已啟用 (Email驗證後設為true)
	
}
