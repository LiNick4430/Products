package com.github.lianick.service;

import com.github.lianick.model.dto.AuthResponseDTO;
import com.github.lianick.model.eneity.RefreshToken;
import com.github.lianick.model.eneity.Users;

// 定義 JWT 中 刷新TOKEN
public interface RefreshTokenService {

	// 登入 生成新的 TOKEN
	RefreshToken generateToken(Users users);
	
	// 刷新 舊有的 TOKEN
	AuthResponseDTO updateRefreshToken(String oldRefreshTokenString);
	
	// 登出 刪除全部的 TOKEN
	void deleteRefreshToken(String account);
}
