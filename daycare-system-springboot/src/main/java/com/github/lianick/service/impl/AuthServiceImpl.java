package com.github.lianick.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.github.lianick.model.dto.AuthResponseDTO;
import com.github.lianick.model.dto.user.UserLoginDTO;
import com.github.lianick.model.eneity.RefreshToken;
import com.github.lianick.model.eneity.Users;
import com.github.lianick.service.AuthService;
import com.github.lianick.service.RefreshTokenService;
import com.github.lianick.service.UserService;
import com.github.lianick.util.JwtUtil;
import com.github.lianick.util.SecurityUtils;

import jakarta.transaction.Transactional;

@Service
@Transactional				// 確保 完整性 
public class AuthServiceImpl implements AuthService{

	@Autowired
	private UserService userService;
	
	@Autowired
	private RefreshTokenService refreshTokenService;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Override
	public AuthResponseDTO login(UserLoginDTO userLoginDTO) {
		// 1. 登入驗證 前處理
		Users users = userService.loginUser(userLoginDTO);
		
		// 2. 生成 RefreshToken
		RefreshToken refreshToken = refreshTokenService.generateToken(users);
		
		// 3. 生成 accessToken
		String accessToken = jwtUtil.generateToken(
				users.getAccount(), 
				users.getUserId(), 
				users.getRole().getRoleId(),
				users.getRole().getName()
				);
		
		// 4. 整合回應
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		authResponseDTO.setId(users.getUserId());
		authResponseDTO.setUsername(users.getAccount());
		authResponseDTO.setRefreshToken(refreshToken.getToken());
		authResponseDTO.setAccessToken(accessToken);
		
		return authResponseDTO;
	}

	@Override
	public void logout() {
		/* 從 JWT 獲取身份：確認操作者身份
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String currentUsername = authentication.getName(); // JWT 中解析出來的帳號
	    */
		String currentUsername = SecurityUtils.getCurrentUsername();
		
		refreshTokenService.deleteRefreshToken(currentUsername);
	}

}
