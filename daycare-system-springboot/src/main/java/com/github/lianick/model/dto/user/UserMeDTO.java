package com.github.lianick.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserMeDTO {	// 從 JWT 獲取資料用

	private Long id;			// 回傳用
	private String username;	// 帳號名稱
	private Long roleNumber;	// 角色ID
	private String roleName;	// 角色名
}
