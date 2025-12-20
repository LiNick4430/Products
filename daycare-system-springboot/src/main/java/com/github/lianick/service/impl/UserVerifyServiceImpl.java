package com.github.lianick.service.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.lianick.config.FrontendProperties;
import com.github.lianick.model.eneity.UserVerify;
import com.github.lianick.model.eneity.Users;
import com.github.lianick.repository.UsersVerifyRepository;
import com.github.lianick.service.EmailService;
import com.github.lianick.service.UserVerifyService;
import com.github.lianick.util.TokenUUID;

@Service
public class UserVerifyServiceImpl implements UserVerifyService{

	@Autowired
	private EmailService emailService;
	
	@Autowired
	private TokenUUID tokenUUID;
	
	@Autowired
	private UsersVerifyRepository usersVerifyRepository;
	
	@Autowired
	private FrontendProperties frontendProperties;
	
	/** 生成驗證碼 + 寄信 私用方法 */
	private void generateUserTokenAndSendEmail(Users users, String subject, String apiName) {
		// 1. 取出/建立 所需資料
		Long userId = users.getUserId();
		String email = users.getEmail();
		String token = tokenUUID.generateToken();
		LocalDateTime expiryTime;
		// 假設是 重設密碼 
		if (apiName.equals("reset/password")) {
			expiryTime = LocalDateTime.now().plusHours(1);	
		} else {	// 其他
			expiryTime = LocalDateTime.now().plusMinutes(15);
		}

		// 將該帳號所有未使用的舊 Token 標記為「已使用」(isUsed = true)。
		// 以確保同一時間只存在一個有效的認證 Token，防止使用者誤用或惡意重發。
		usersVerifyRepository.markAllUnusedTokenAsUsed(userId);

		// 2. 產生驗證碼 並存回去
		UserVerify userVerify = new UserVerify();
		userVerify.setToken(token);
		userVerify.setExpiryTime(expiryTime);
		userVerify.setUsers(users);

		usersVerifyRepository.save(userVerify);

		// 3. 寄出驗證信
		// 根據不同 api 導向 不同用途 (1. verify 2. reset/password)
		String verificationLink = frontendProperties.getUrl() + "/email/" + apiName + "?token=" + userVerify.getToken();			
		emailService.sendVerificationEmail(email, subject, verificationLink);
	}
	
	@Override
	@Transactional
	public void generateUserToken(Users users, String subject, String apiName) {
		generateUserTokenAndSendEmail(users, subject, apiName);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void generateUserTokenWithNewTransactional(Users users, String subject, String apiName) {
		generateUserTokenAndSendEmail(users, subject, apiName);
	}
	
}
