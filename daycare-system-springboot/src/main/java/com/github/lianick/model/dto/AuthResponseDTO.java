package com.github.lianick.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//登入或刷新Token後，回傳給前端的標準回應結構
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDTO {

	private String accessToken;		// 新的 Access Token (短效期)
	private String refreshToken;	// 新的 Refresh Token 字串 (長效期)
	private String username;        // 用戶帳號
	private String roleName;		// 角色名字
	
}
