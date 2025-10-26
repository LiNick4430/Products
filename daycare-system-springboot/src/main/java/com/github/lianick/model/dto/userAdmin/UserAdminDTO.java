package com.github.lianick.model.dto.userAdmin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserAdminDTO {
	
	private Long id;						// 帳號ID/員工ID
	private String username;				// 帳號名
	
	// 基本資料
	private String name;					// 員工姓名
	private String jobTitle;				// 員工職稱
	
	private Long organizationId;			// 所屬機構 ID
	private String organizationName;		// 所屬機構 名字
}
