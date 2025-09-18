package com.github.lianick.model.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//User註冊時 用的 DTO
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationDTO {

	private Long id;					// 對應 User.userId 
	private String name;				// 對應 User.userName
	private String idNumber; 			// 對應 User.nationalIdNo
	private LocalDateTime birthday;		// 對應 User.birthdate
	
	private String registeredAddress;	// 戶籍地址
	private String mailingAddress;		// 通訊地址
	private String email;				// e-mail
	private String phoneNumber;			// 手機號碼
	
	private String account;				// 帳號名稱
	private String password;			// 未加密的明文密碼 加密後 對應 User.password
	
	private String role;				// 帳號權限等級
}
