package com.github.lianick.service.impl;

import java.time.LocalDateTime;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.github.lianick.exception.UserNoFoundException;
import com.github.lianick.model.dto.UserDeleteDTO;
import com.github.lianick.model.dto.UserForgetPasswordDTO;
import com.github.lianick.model.dto.UserLoginDTO;
import com.github.lianick.model.dto.UserRegisterDTO;
import com.github.lianick.model.eneity.UserVerify;
import com.github.lianick.model.eneity.Users;
import com.github.lianick.repository.UsersRepository;
import com.github.lianick.repository.UsersVerifyRepository;
import com.github.lianick.response.ApiResponse;
import com.github.lianick.service.UserService;
import com.github.lianick.util.PasswordSecurity;
import com.github.lianick.util.TokenUUID;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor	// Lombok 會自動生成包含所有 final 欄位的建構式
public class UserServiceImpl implements UserService{

	@Autowired
	private PasswordSecurity passwordSecurity;
	
	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private UsersVerifyRepository usersVeriftyRepository;
	
	@Autowired
	private EmailServiceImpl emailServiceImpl;
	
	@Autowired
	private TokenUUID tokenUUID;
	
	// 注入時不需要 @Autowired，因為 Lombok 會處理它
	@Qualifier("userModelMapper")
	private final ModelMapper userMapper;
	
	public Users convertToUser(UserRegisterDTO userRegisterDTO) {
		return userMapper.map(userRegisterDTO, Users.class);
	}

	@Override
	public ApiResponse<UserRegisterDTO> registerUser(UserRegisterDTO userRegisterDTO) {
		// DTO 轉 Entity
		Users users = convertToUser(userRegisterDTO);
		// 密碼加密
		String rawPassword = users.getPassword();
		String hashPassword = passwordSecurity.hashPassword(rawPassword);
		users.setPassword(hashPassword);
		// 儲存
		usersRepository.save(users);
		// 產生驗證碼 同時 寄出 驗證信
		generateUserToken(users);
		// 返回時 通常把 密碼清空
		userRegisterDTO.setPassword(null);
		return new ApiResponse<UserRegisterDTO>(true, "帳號建立成功, 請驗證信箱", userRegisterDTO);
	}

	@Override
	public ApiResponse<Void> veriftyUser(String token) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApiResponse<UserLoginDTO> loginUser(UserLoginDTO userLoginDTO) throws UserNoFoundException {
		// 1. 找尋資料庫 對應的帳號
	    Users tableUser = usersRepository.findByAccount(userLoginDTO.getUsername())
	        .orElseThrow(() -> new UserNoFoundException("帳號不存在或錯誤"));

	    // 2. 檢查密碼 是否相符
	    String rawPassword = userLoginDTO.getRawPassword(); 	// 使用者輸入的明文密碼
	    String encodedPassword = tableUser.getPassword(); 	// 資料庫中儲存的雜湊密碼
	    
	    // 直接使用明文密碼和雜湊密碼進行比對
	    if (passwordSecurity.verifyPassword(rawPassword, encodedPassword)) {
	    	userLoginDTO.setRawPassword(rawPassword); // 回傳給前台 會先將密碼清空
	        return new ApiResponse<UserLoginDTO>(true, "登陸成功", userLoginDTO);
	    }
	    
	    // 如果不相符
	    return new ApiResponse<UserLoginDTO>(false, "密碼錯誤", null);
	}

	@Override
	public ApiResponse<UserForgetPasswordDTO> forgetPassword(UserForgetPasswordDTO userForgetPasswordDTO) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApiResponse<Void> deleteUser(UserDeleteDTO userDeleteDTO) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void generateUserToken(Users users) {
		// 1. 取出 所需資料
		String account = users.getAccount();
		String email = users.getEmail();
		usersVeriftyRepository.markAllUnusedTokenAsUsed(account);
		// 2. 產生驗證碼 並存回去
		String token = tokenUUID.generateToekn(); 
		LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(15);
		
		UserVerify userVerify = new UserVerify();
		userVerify.setToken(token);
		userVerify.setExpiryTime(expiryTime);
		userVerify.setUsers(users);
		
		usersVeriftyRepository.save(userVerify);
		// 3. 寄出驗證信
		String verificationLink = "http://localhost:8080/api/users/verify?token=" + userVerify.getToken();	// 預設 認證 網址		
		emailServiceImpl.sendVerificationEmail(email, "帳號啟用信件", verificationLink);
	}
}
