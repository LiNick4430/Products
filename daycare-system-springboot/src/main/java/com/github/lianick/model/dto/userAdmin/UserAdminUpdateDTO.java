package com.github.lianick.model.dto.userAdmin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserAdminUpdateDTO {
	
	private Long id;						// 帳號ID/員工ID
	private String username;				// 帳號名
	
	// 原本資料
	private Long roleNumber;				// 角色號碼
	private String name;					// 員工姓名
	private String jobTitle;				// 員工職稱
	
	private Long organizationId;			// 所屬機構 ID
	private String organizationName;		// 所屬機構 名字
	
	// 更新資料
	private Long newRoleNumber;				// 角色號碼
	private String newName;					// 員工姓名
	private String newJobTitle;				// 員工職稱
	
	private Long newOrganizationId;			// 所屬機構 ID
	private String newOrganizationName;		// 所屬機構 名字
}
