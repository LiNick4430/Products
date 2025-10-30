package com.github.lianick.service;


import com.github.lianick.model.dto.AuthResponseDTO;
import com.github.lianick.model.dto.user.UserLoginDTO;

// 負責整合 流程
public interface AuthService {

	/** 負責 整合 登入流程 */
	AuthResponseDTO login(UserLoginDTO userLoginDTO);
	
	/** 登出<p>
	 * 需要 @PreAuthorize("isAuthenticated()")
	 * */
	void logout();
}
