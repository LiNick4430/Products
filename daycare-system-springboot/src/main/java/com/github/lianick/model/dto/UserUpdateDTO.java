package com.github.lianick.model.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// User 登入後 更新 基本資料用
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDTO {

	private Long id;					// 對應 User.userId
	private String name;				// 對應 User.userName
	
	private String idNumber; 			// 對應 User.nationalIdNo
	private LocalDateTime birthday;		// 對應 User.birthdate
	
	private String registeredAddress;	// 戶籍地址
	private String mailingAddress;		// 通訊地址
	private String email;				// e-mail
	private String phoneNumber;			// 手機號碼
	
}
