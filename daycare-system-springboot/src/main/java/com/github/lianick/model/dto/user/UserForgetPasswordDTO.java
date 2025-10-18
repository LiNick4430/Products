package com.github.lianick.model.dto.user;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserForgetPasswordDTO{	// 忘記密碼 用
	private Long id;			// 回傳用
	
	private String username;	// 用於 1. 寄認證信 3. 通過驗證時 更新密碼時使用
	private String password;	// 用於 3. 通過驗證時 更新密碼時使用
	private String token;		// 用於 2. token 驗證 3. 通過驗證時 更新密碼時使用
	
	private LocalDateTime expiryTime;	// 用於 2. 給 前端 看 token 過期時間 = 網頁到期時間
}
