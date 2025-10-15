package com.github.lianick.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.github.lianick.exception.TokenFailureException;
import com.github.lianick.exception.UserNoFoundException;
import com.github.lianick.model.dto.PasswordAwareDTO;
import com.github.lianick.model.dto.user.UserDeleteDTO;
import com.github.lianick.model.dto.user.UserForgetPasswordDTO;
import com.github.lianick.model.dto.user.UserLoginDTO;
import com.github.lianick.model.dto.user.UserRegisterDTO;
import com.github.lianick.model.dto.user.UserUpdateDTO;
import com.github.lianick.model.dto.user.UserVerifyDTO;
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
	
	@Autowired
	private ModelMapper modelMapper;
	
	public Users convertToUser(UserRegisterDTO userRegisterDTO) {
		return modelMapper.map(userRegisterDTO, Users.class);
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
		generateUserToken(users, "帳號啟用信件", "verify");
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
		// 找尋資料庫 對應的帳號
	    Users tableUser = usersRepository.findByAccount(userLoginDTO.getUsername())
	        .orElseThrow(() -> new UserNoFoundException("帳號或密碼錯誤"));

	    // 使用 checkPassword 方法
	    if (!checkPassword(userLoginDTO, tableUser)) {
	    	// 如果 密碼不相符, 統一由 UserNoFoundException -> GlobalExceptionHandler 處理回傳
		    throw new UserNoFoundException("帳號或密碼錯誤");
	    }
	    userLoginDTO.setPassword(null);; 				// 回傳給前台 會先將密碼清空
    	tableUser.setLoginDate(LocalDateTime.now());	// 登入時間
    	usersRepository.save(tableUser);
    	
        return new ApiResponse<UserLoginDTO>(true, "登陸成功", userLoginDTO);
	}

	@Override
	public ApiResponse<Void> forgetPasswordSendEmail(UserForgetPasswordDTO userForgetPasswordDTO) throws UserNoFoundException{
		// 找尋資料庫 對應的帳號
	    Users tableUser = usersRepository.findByAccount(userForgetPasswordDTO.getUsername())
	        .orElseThrow(() -> new UserNoFoundException("帳號或密碼錯誤"));
	    
	    // 產生驗證碼 同時 寄出 驗證信
	    generateUserToken(tableUser, "忘記密碼驗證信件", "reset-password");
	    
		return new ApiResponse<Void>(true, "驗證信寄出成功", null);
	}
	
	@Override
	public ApiResponse<Void> forgetPasswordVerifty(UserForgetPasswordDTO userForgetPasswordDTO) throws TokenFailureException {
		// 取出/建立 所需資料
		String token = userForgetPasswordDTO.getToekn();
		LocalDateTime now = LocalDateTime.now();
		
		// 1. 使用 Token 紀錄 找尋 UsersVerify 紀錄
		Optional<UserVerify> optUserVerify = usersVerifyRepository.findByToken(token);
		if (optUserVerify.isEmpty()) {
			throw new TokenFailureException("驗證碼無效或不存在");
		}
		UserVerify userVerify = optUserVerify.get();
		
		// 2. 判斷 Token 狀態
		if (userVerify.getIsUsed()) {
			throw new TokenFailureException("驗證碼已經被使用");
		}
		if (userVerify.getExpiryTime().isBefore(now)) {
			throw new TokenFailureException("驗證碼已經過期");
		}
		
		return new ApiResponse<Void>(true, "驗證成功, 進入修改密碼網頁", null);
	}
	
	@Override
	public ApiResponse<Void> forgetPasswordUpdatePassword(UserForgetPasswordDTO userForgetPasswordDTO) throws TokenFailureException, UserNoFoundException{
		// 取出/建立 所需資料
		Users tableUser = usersRepository.findByAccount(userForgetPasswordDTO.getUsername())
			        .orElseThrow(() -> new UserNoFoundException("帳號或密碼錯誤"));
		String token = userForgetPasswordDTO.getToekn();
		String rawPassword = userForgetPasswordDTO.getPassword();
		LocalDateTime now = LocalDateTime.now(); 
		// 驗證 token, 預設已經膯過第二步 這裡將不再驗證 時間
		Optional<UserVerify> optUserVerify = usersVerifyRepository.findByToken(token);
		if (optUserVerify.isEmpty()) {
			throw new TokenFailureException("驗證碼無效或不存在");
		}
		UserVerify userVerify = optUserVerify.get();
		LocalDateTime newExpiryTime = userVerify.getExpiryTime().plusHours(1);	// 延長修改時間 讓用戶有額外 1個小時 可以修正
		if (userVerify.getIsUsed()) {
			throw new TokenFailureException("驗證碼已經被使用");
		}
		if (newExpiryTime.isBefore(now)) {
			throw new TokenFailureException("閒置時間太久, 將離開網頁");
		}
		// 更新密碼
		String hashPassword = passwordSecurity.hashPassword(rawPassword);
		tableUser.setPassword(hashPassword);
		// 認證 已經使用
		userVerify.setIsUsed(true);
		// 存回去
		usersRepository.save(tableUser);
		usersVerifyRepository.save(userVerify);
		 
		return new ApiResponse<>(true, "密碼更新完成, 請使用新密碼登入", null);
	}

	@Override
	public ApiResponse<Void> updateUserCheckPassword(UserUpdateDTO userUpdateDTO) {
		// 找尋資料庫 對應的帳號
	    Users tableUser = usersRepository.findByAccount(userUpdateDTO.getUsername())
	        .orElseThrow(() -> new UserNoFoundException("帳號或密碼錯誤"));
	    // 使用 checkPassword 方法 複查 密碼是否相同
	    if (!checkPassword(userUpdateDTO, tableUser)) {
	    	throw new UserNoFoundException("帳號或密碼錯誤");
	    }
	    
		return new ApiResponse<>(true, "密碼確認成功, 進入修改資料網頁", null);
	}

	@Override
	public ApiResponse<Void> updateUserVeriftyEmail(UserUpdateDTO userUpdateDTO) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ApiResponse<Void> updateUser(UserUpdateDTO userUpdateDTO) {
		// 複查一次 
	    Users tableUser = usersRepository.findByAccount(userUpdateDTO.getUsername())
	        .orElseThrow(() -> new UserNoFoundException("帳號或密碼錯誤"));
	    
	    // 更新資料, 同時檢查 null、空字串、和空白字元
	    String rawPassword = userUpdateDTO.getNewPassword();
	    String newEmail = userUpdateDTO.getNewEmail();
	    String newPhoneNumber = userUpdateDTO.getNewPhoneNumber();
	    
		if (rawPassword != null && !rawPassword.isBlank()) {
			
			String hashPassword = passwordSecurity.hashPassword(rawPassword);
			tableUser.setPassword(hashPassword);
			
			// 密碼改變， 同時將所有 UserVerify Token 作廢 
			usersVerifyRepository.markAllUnusedTokenAsUsed(tableUser.getAccount());
		}
		
		if (newEmail != null && !newEmail.isBlank()) {
			tableUser.setEmail(userUpdateDTO.getNewEmail());
			
			// 新信箱 進行認證
			generateUserToken(tableUser, "新信箱認證信", "reset-email");
		}
		
		if (newPhoneNumber != null && !newPhoneNumber.isBlank()) {
			tableUser.setPhoneNumber(userUpdateDTO.getNewPhoneNumber());
		}
		
		usersRepository.save(tableUser);
	    
	    return new ApiResponse<>(true, "資料更新完成 請重新登入", null);
	}
	
	@Override
	public ApiResponse<Void> deleteUser(UserDeleteDTO userDeleteDTO) throws UserNoFoundException{
		// 找尋資料庫 對應的帳號
	    Users tableUser = usersRepository.findByAccount(userDeleteDTO.getUsername())
	        .orElseThrow(() -> new UserNoFoundException("帳號或密碼錯誤"));

	    // 使用 checkPassword 方法 複查 密碼是否相同
	    if (!checkPassword(userDeleteDTO, tableUser)) {
	    	throw new UserNoFoundException("帳號或密碼錯誤");
	    }
	    
	    // 建立 變數
	    LocalDateTime deleteTime = LocalDateTime.now();
	    
		// 執行 軟刪除
	    tableUser.setDeleteAt(deleteTime);
		// 關聯欄位
		List<UserVerify> userVerifies = tableUser.getUserVerifies();
		if (userVerifies != null) {
			userVerifies.forEach(userVerify -> {
				userVerify.setDeleteAt(deleteTime);
			});
		}
		
		// 回存
		usersRepository.save(tableUser);
	    
		return new ApiResponse<Void>(true, "帳號刪除成功", null);
	}

	@Override
	public void generateUserToken(Users users, String subject, String apiName) {
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
		// 根據不同 api 導向 不同用途 (1. 驗證帳號 2. 忘記密碼)
		String verificationLink = "http://localhost:8080/api/users/" + apiName + "?token=" + userVerify.getToken();			
		emailServiceImpl.sendVerificationEmail(email, subject, verificationLink);
	}

	@Override
	public <T extends PasswordAwareDTO> Boolean checkPassword(T userDto, Users tableUser) {
		// 取出 使用者輸入的 明文密碼
	    String rawPassword = userDto.getRawPassword(); 		// 使用者輸入的明文密碼
	    
	    // 取出 資料庫的 雜湊密碼
	    String encodedPassword = tableUser.getPassword(); 	// 資料庫中儲存的雜湊密碼
	    
	    // 進行 密碼比對
		return passwordSecurity.verifyPassword(rawPassword, encodedPassword);
	}

	

	
	
}
