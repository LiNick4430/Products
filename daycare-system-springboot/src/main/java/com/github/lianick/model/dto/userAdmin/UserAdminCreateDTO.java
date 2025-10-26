package com.github.lianick.model.dto.userAdmin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserAdminCreateDTO {			// 註冊 員工帳號
	
	private Long id;						// 帳號ID/員工ID
	
	// 註冊資料
	private String username;				// 帳號名
	private String password;				// 密碼
	private Long roleNumber;				// 角色號碼
	private String email;
	private String phoneNumber;
	
	// 基本資料
	private String name;					// 員工姓名
	private String jobTitle;				// 員工職稱
	private Long organizationId;			// 所屬機構 ID
	private String organizationName;		// 所屬機構 名字
}
