package com.github.lianick.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.github.lianick.exception.TokenFailureException;
import com.github.lianick.exception.UserNoFoundException;
import com.github.lianick.model.dto.UserDeleteDTO;
import com.github.lianick.model.dto.UserForgetPasswordDTO;
import com.github.lianick.model.dto.UserLoginDTO;
import com.github.lianick.model.dto.UserRegisterDTO;
import com.github.lianick.model.dto.UserVerifyDTO;
import com.github.lianick.model.eneity.UserVerify;
import com.github.lianick.model.eneity.Users;
import com.github.lianick.repository.UsersRepository;
import com.github.lianick.repository.UsersVerifyRepository;
import com.github.lianick.response.ApiResponse;
import com.github.lianick.service.UserService;
import com.github.lianick.util.PasswordSecurity;
import com.github.lianick.util.TokenUUID;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor	// Lombok 會自動生成包含所有 final 欄位的建構式, 用於 private final ModelMapper userMapper;
@Transactional				// 確保 完整性 
public class UserServiceImpl implements UserService{

	@Autowired
	private PasswordSecurity passwordSecurity;
	
	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private UsersVerifyRepository usersVerifyRepository;
	
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
		generateUserToken(users, "帳號啟用信件");
		// 返回時 通常把 密碼清空
		userRegisterDTO.setPassword(null);
		return new ApiResponse<UserRegisterDTO>(true, "帳號建立成功, 請驗證信箱", userRegisterDTO);
	}

	@Override
	public ApiResponse<Void> veriftyUser(UserVerifyDTO userVerifyDTO) throws TokenFailureException {
		// 取出/建立 所需資料
		String token = userVerifyDTO.getToekn();
		LocalDateTime now = LocalDateTime.now();
		
		// 1. 使用 Token 紀錄 找尋 UsersVerify 紀錄
		Optional<UserVerify> optUserVerify = usersVerifyRepository.findByToken(token);
		if (optUserVerify.isEmpty()) {
			throw new TokenFailureException("驗證碼無效或不存在");
		}
		UserVerify userVerify = optUserVerify.get();
		Users users = userVerify.getUsers();
		
		// 2. 判斷 Token 狀態
		if (userVerify.getIsUsed()) {
			throw new TokenFailureException("驗證碼已經被使用");
		}
		if (userVerify.getExpiryTime().isBefore(now)) {
			throw new TokenFailureException("驗證碼已經過期");
		}
		
		// 3. 判斷 Users 狀態 以防止重複使用
		if (users.getIsActive()) {
			return new ApiResponse<Void>(true, "帳號已經是啟用狀態", null);
		}
		
		// 4. 啟用帳號
		userVerify.setIsUsed(true);
		users.setIsActive(true);
		users.setActiveDate(now);
		
		usersVerifyRepository.save(userVerify);
		usersRepository.save(users);
		
		return new ApiResponse<Void>(true, "帳號啟用成功", null);
	}

	@Override
	public ApiResponse<UserLoginDTO> loginUser(UserLoginDTO userLoginDTO) throws UserNoFoundException {
		// 1. 找尋資料庫 對應的帳號
	    Users tableUser = usersRepository.findByAccount(userLoginDTO.getUsername())
	        .orElseThrow(() -> new UserNoFoundException("帳號或密碼錯誤"));

	    // 2. 檢查密碼 是否相符
	    String rawPassword = userLoginDTO.getRawPassword(); // 使用者輸入的明文密碼
	    String encodedPassword = tableUser.getPassword(); 	// 資料庫中儲存的雜湊密碼
	    
	    // 直接使用明文密碼和雜湊密碼進行比對
	    if (passwordSecurity.verifyPassword(rawPassword, encodedPassword)) {
	    	userLoginDTO.setRawPassword(rawPassword); // 回傳給前台 會先將密碼清空
	        return new ApiResponse<UserLoginDTO>(true, "登陸成功", userLoginDTO);
	    }
	    
	    // 如果 密碼不相符, 統一由 UserNoFoundException -> GlobalExceptionHandler 處理回傳
	    throw new UserNoFoundException("帳號或密碼錯誤");
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
	public void generateUserToken(Users users, String subject) {
		// 1. 取出/建立 所需資料
		String account = users.getAccount();
		String email = users.getEmail();
		String token = tokenUUID.generateToekn(); 
		LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(15);
		
		// 將該帳號所有未使用的舊 Token 標記為「已使用」(isUsed = true)。
		// 以確保同一時間只存在一個有效的認證 Token，防止使用者誤用或惡意重發。
		usersVerifyRepository.markAllUnusedTokenAsUsed(account);
		
		// 2. 產生驗證碼 並存回去
		UserVerify userVerify = new UserVerify();
		userVerify.setToken(token);
		userVerify.setExpiryTime(expiryTime);
		userVerify.setUsers(users);
		
		usersVerifyRepository.save(userVerify);
		
		// 3. 寄出驗證信
		String verificationLink = "http://localhost:8080/api/users/verify?token=" + userVerify.getToken();	// 預設 認證 網址		
		emailServiceImpl.sendVerificationEmail(email, subject, verificationLink);
	}
}
