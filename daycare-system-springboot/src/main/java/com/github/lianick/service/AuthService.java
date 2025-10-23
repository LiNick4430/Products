package com.github.lianick.service;

import org.springframework.security.access.prepost.PreAuthorize;

import com.github.lianick.model.dto.AuthResponseDTO;
import com.github.lianick.model.dto.user.UserLoginDTO;

// 負責整合 流程
public interface AuthService {

	// 負責 整合 登入流程
	AuthResponseDTO login(UserLoginDTO userLoginDTO);
	
	// 登出
	@PreAuthorize("isAuthenticated()")	// 確保只有持有有效 JWT 的用戶才能存取
	void logout();
}
