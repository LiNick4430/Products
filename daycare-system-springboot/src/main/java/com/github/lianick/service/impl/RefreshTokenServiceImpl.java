package com.github.lianick.service.impl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.lianick.exception.TokenFailureException;
import com.github.lianick.model.dto.AuthResponseDTO;
import com.github.lianick.model.eneity.RefreshToken;
import com.github.lianick.model.eneity.Users;
import com.github.lianick.repository.RefreshTokenRepository;
import com.github.lianick.service.RefreshTokenService;
import com.github.lianick.util.JwtUtil;
import com.github.lianick.util.TokenUUID;

@Service
@Transactional				// 確保 完整性 
public class RefreshTokenServiceImpl implements RefreshTokenService{
	
	@Autowired
	private RefreshTokenRepository refreshTokenRepository;
	
	@Autowired
	private TokenUUID tokenUUID;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private EntityFetcher entityFetcher;
	
	@Override
	public RefreshToken generateToken(Users users) {
		// 1. 軟刪除用戶所有現存的有效 Refresh Token (單點登入強制作廢)
		refreshTokenRepository.markAllTokenAsDeleteByAccount(users.getAccount());
		
		// 2. 生成唯一的 Token 字串
		String token = tokenUUID.generateToken();
		
		// 3. 計算新的過期時間 (例如：30 天後)
		Instant expiryTime = Instant.now().plus(30, ChronoUnit.DAYS);
		
		// 4. 生成 refreshToken
		RefreshToken refreshToken = new RefreshToken();
		refreshToken.setToken(token);
		refreshToken.setExpiryTime(expiryTime);
		refreshToken.setUsers(users);
		refreshToken = refreshTokenRepository.save(refreshToken);
		
		return refreshToken;
	}
	
	/**
	 * 防止「Refresh Token 重放攻擊 (Replay Attack)」
	 * 
	 * 在 Token 刷新流程中，每次使用 Refresh Token 獲取新的 Access Token 時，都必須同時作廢舊的 Refresh Token，並簽發一個新的 Refresh Token。 
	 * 這種模式被稱為 Refresh Token Rotation (刷新 Token 輪替)。
	 * */
	@Override	// Refresh Token 輪替模式
	public AuthResponseDTO updateRefreshToken(String oldRefreshTokenString) {
		// 1. 查找並驗證 Refresh Token 的有效性
		RefreshToken oldRefreshToken = entityFetcher.getRefreshTokenByToken(oldRefreshTokenString, "Refresh Token 不存在或已被作廢");
		
		// 2. 驗證 Refresh Token 是否過期
		if (oldRefreshToken.getExpiryTime().isBefore(Instant.now())) {
			// 如果過期
			refreshTokenRepository.markTokenAsDeleteByToken(oldRefreshToken.getToken());
			throw new TokenFailureException("Refresh Token 已經過期, 請重新登錄");
		}
		
		// 3. 獲取用戶訊息
		Users users = oldRefreshToken.getUsers();
		
		// 4. 作廢 舊有的 Refresh Token		-> 終止舊 Token A。
		refreshTokenRepository.markTokenAsDeleteByToken(oldRefreshToken.getToken());
		
		// 5. 重新生成新的 Refresh Token	-> 簽發新 Token B
		RefreshToken newRefreshToken = generateToken(users);
		
		// 6. 簽發 新的 Access Token	-> 簽發新 Access Token
		String newAccessToken = jwtUtil.generateToken(
				users.getAccount(), 
				users.getUserId(), 
				users.getRole().getRoleId(),
				users.getRole().getName()
				);
		
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		authResponseDTO.setUsername(users.getAccount());
		authResponseDTO.setRefreshToken(newRefreshToken.getToken());
		authResponseDTO.setAccessToken(newAccessToken);
		authResponseDTO.setRoleName(users.getRole().getName());
		
		return authResponseDTO;
	}

	@Override
	public void deleteRefreshToken(String account) {
		// 1. 軟刪除用戶所有現存的有效 Refresh Token (登出)
		refreshTokenRepository.markAllTokenAsDeleteByAccount(account);
	}

}
