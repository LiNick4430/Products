package com.github.lianick.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
		// 1. DTO 轉 Entity
		Users users = convertToUser(userRegisterDTO);
		
		// 2. 密碼 取出加密處理
		String rawPassword = users.getPassword();
		String hashPassword = passwordSecurity.hashPassword(rawPassword);
		users.setPassword(hashPassword);
		
		// 3. users 儲存
		users = usersRepository.save(users);
		
		// 4. 產生驗證碼 同時 寄出 驗證信
		generateUserToken(users, "帳號啟用信件", "verify");
		
		// 5. Entity 轉 DTO
		userRegisterDTO = modelMapper.map(users, UserRegisterDTO.class);
		
		// 6. 返回處理: 清空 password, 給予角色ID
		userRegisterDTO.setRoleNumber(users.getRole().getRoleId());
		userRegisterDTO.setPassword(null);
		
		return new ApiResponse<UserRegisterDTO>(true, "帳號建立成功, 請驗證信箱", userRegisterDTO);
	}

	@Override
	public ApiResponse<UserVerifyDTO> veriftyUser(UserVerifyDTO userVerifyDTO) throws TokenFailureException {
		// 0. 取出/建立 所需資料
		String token = userVerifyDTO.getToken();
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
			return new ApiResponse<UserVerifyDTO>(true, "帳號已經是啟用狀態", null);
		}
		
		// 4. 啟用帳號
		userVerify.setIsUsed(true);
		users.setIsActive(true);
		users.setActiveDate(now);
		
		usersVerifyRepository.save(userVerify);
		users = usersRepository.save(users);
		
		// 5. Entity 轉 DTO
		userVerifyDTO = modelMapper.map(users, UserVerifyDTO.class);
		
		// 6. 返回處理: 清空 token
		userVerifyDTO.setToken(null);
		
		return new ApiResponse<UserVerifyDTO>(true, "帳號啟用成功", userVerifyDTO);
	}

	@Override
	public ApiResponse<UserLoginDTO> loginUser(UserLoginDTO userLoginDTO) throws UserNoFoundException {
		// 1. 找尋資料庫 對應的帳號
	    Users tableUser = usersRepository.findByAccount(userLoginDTO.getUsername())
	        .orElseThrow(() -> new UserNoFoundException("帳號或密碼錯誤"));

	    // 2. checkPassword 方法 驗證密碼
	    if (!checkPassword(userLoginDTO, tableUser)) {
	    	// 如果 密碼不相符, 統一由 UserNoFoundException -> GlobalExceptionHandler 處理回傳
		    throw new UserNoFoundException("帳號或密碼錯誤");
	    }
	    // 3. 登入成功 打上時間
	    tableUser.setLoginDate(LocalDateTime.now());	// 登入時間
    	usersRepository.save(tableUser);
	    
	    // 4. Entity 轉 DTO
	    userLoginDTO = modelMapper.map(tableUser, UserLoginDTO.class);
	    
	    // 5. 返回處理: 清空 password
    	userLoginDTO.setPassword(null);
    	
        return new ApiResponse<UserLoginDTO>(true, "登陸成功", userLoginDTO);
	}

	@Override
	public ApiResponse<UserForgetPasswordDTO> forgetPasswordSendEmail(UserForgetPasswordDTO userForgetPasswordDTO) throws UserNoFoundException{
		// 1. 找尋資料庫 對應的帳號
	    Users tableUser = usersRepository.findByAccount(userForgetPasswordDTO.getUsername())
	        .orElseThrow(() -> new UserNoFoundException("帳號或密碼錯誤"));
	    
	    // 2. 產生驗證碼 同時 寄出 驗證信
	    generateUserToken(tableUser, "忘記密碼驗證信件", "reset-password");
	    
	    // 3. Entity 轉 DTO
	    userForgetPasswordDTO = modelMapper.map(tableUser, UserForgetPasswordDTO.class);
	    
	    // 4. 返回處理: 清空 敏感數值
	    userForgetPasswordDTO.setToken(null);
	    userForgetPasswordDTO.setPassword(null);
	    
		return new ApiResponse<UserForgetPasswordDTO>(true, "驗證信寄出成功", userForgetPasswordDTO);
	}
	
	@Override
	public ApiResponse<UserForgetPasswordDTO> forgetPasswordVerifty(UserForgetPasswordDTO userForgetPasswordDTO) throws TokenFailureException {
		// 0. 取出/建立 所需資料
		String token = userForgetPasswordDTO.getToken();
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
		
		// 3. 取出 Entity
		Users tableUser = userVerify.getUsers();
		
		// 4. Entity 轉 DTO
	    userForgetPasswordDTO = modelMapper.map(tableUser, UserForgetPasswordDTO.class);
	    
	    // 5. 返回處理: 清空 敏感數值
	    userForgetPasswordDTO.setToken(null);
	    userForgetPasswordDTO.setPassword(null);
		
		return new ApiResponse<UserForgetPasswordDTO>(true, "驗證成功, 進入修改密碼網頁", userForgetPasswordDTO);
	}
	
	@Override
	public ApiResponse<UserForgetPasswordDTO> forgetPasswordUpdatePassword(UserForgetPasswordDTO userForgetPasswordDTO) throws TokenFailureException, UserNoFoundException{
		// 1. 取出/建立 所需資料
		Users tableUser = usersRepository.findByAccount(userForgetPasswordDTO.getUsername())
			        .orElseThrow(() -> new UserNoFoundException("帳號或密碼錯誤"));
		String token = userForgetPasswordDTO.getToken();
		String rawPassword = userForgetPasswordDTO.getPassword();
		LocalDateTime now = LocalDateTime.now();
		
		// 2. 驗證 token, 預設已經膯過第二步 這裡將不再驗證 時間
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
		// 3. 更新密碼
		String hashPassword = passwordSecurity.hashPassword(rawPassword);
		tableUser.setPassword(hashPassword);
		
		// 4. 設定 已經 認證 
		userVerify.setIsUsed(true);
		
		// 5. 存回去
		tableUser = usersRepository.save(tableUser);
		usersVerifyRepository.save(userVerify);
		
		// 6. Entity 轉 DTO
	    userForgetPasswordDTO = modelMapper.map(tableUser, UserForgetPasswordDTO.class);
	    
	    // 7. 返回處理: 清空 敏感數值
	    userForgetPasswordDTO.setToken(null);
	    userForgetPasswordDTO.setPassword(null);
		
		return new ApiResponse<UserForgetPasswordDTO>(true, "密碼更新完成, 請使用新密碼登入", null);
	}

	@Override
	public ApiResponse<UserUpdateDTO> updateUserCheckPassword(UserUpdateDTO userUpdateDTO) {
		// 1. 找尋資料庫 對應的帳號
	    Users tableUser = usersRepository.findByAccount(userUpdateDTO.getUsername())
	        .orElseThrow(() -> new UserNoFoundException("帳號或密碼錯誤"));
	    
	    // 2. 使用 checkPassword 方法 複查 密碼是否相同
	    if (!checkPassword(userUpdateDTO, tableUser)) {
	    	throw new UserNoFoundException("帳號或密碼錯誤");
	    }
	    
	    // 3. Entity 轉 DTO
	    userUpdateDTO = modelMapper.map(tableUser, UserUpdateDTO.class);
	    
	    // 4. 返回處理: 清空 password
	    userUpdateDTO.setPassword(null);
	    userUpdateDTO.setNewPassword(null);
	    userUpdateDTO.setMailIsActive(false);		// 給予 預設值 
	    userUpdateDTO.setToken(null);
	    
		return new ApiResponse<>(true, "密碼確認成功, 進入修改資料網頁", userUpdateDTO);
	}

	@Override
	public ApiResponse<UserUpdateDTO> updateUserSendEmail(UserUpdateDTO userUpdateDTO) {
		// 1. 取出/建立 所需資料
		String newEmail = userUpdateDTO.getNewEmail();
	    Users tableUser = usersRepository.findByAccount(userUpdateDTO.getUsername())
	        .orElseThrow(() -> new UserNoFoundException("帳號或密碼錯誤"));
	    
	    // 1. 假設 新信箱 和 舊信箱一樣
	    if (tableUser.getEmail().equalsIgnoreCase(newEmail)) {
	    	// Entity 轉 DTO(獲取舊 Email/PhoneNumber 資訊)
		    userUpdateDTO = modelMapper.map(tableUser, UserUpdateDTO.class);
		    
		    // 返回處理
		    userUpdateDTO.setPassword(null);
		    userUpdateDTO.setNewPassword(null);
		    userUpdateDTO.setNewEmail(newEmail);
		    userUpdateDTO.setToken(null);
		    userUpdateDTO.setMailIsActive(true); 	// 因為 信箱 一樣 預設通過驗證
		    
		    return new ApiResponse<>(true, "新信箱與舊信箱相同，無需重新驗證", userUpdateDTO);
		}
	    
	    // 3. 檢查 新信箱 是否 已被 其他用戶使用
	    Optional<Users> existingOptUser = usersRepository.findByEmail(newEmail);
	    if (existingOptUser.isPresent() && !existingOptUser.get().getUserId().equals(tableUser.getUserId())) {
			
	    	// 模糊錯誤, 防止惡意使用者偵測哪些 Email 已被註冊
	    	throw new UserNoFoundException("新信箱或帳號資訊錯誤");
		}
	    
	    // 4. 寄出驗證信
	    generateUserTokenByNewEmail(tableUser, "新信箱驗證信", "reset-email", newEmail);
	    
	    // 5. Entity 轉 DTO
	    userUpdateDTO = modelMapper.map(tableUser, UserUpdateDTO.class);
	    
	    // 6. 返回處理
	    userUpdateDTO.setPassword(null);
	    userUpdateDTO.setNewPassword(null);
	    userUpdateDTO.setNewEmail(newEmail);
	    userUpdateDTO.setToken(null);
	    
		return new ApiResponse<>(true, "驗證信已經寄出, 請收信", userUpdateDTO);
	}
	
	@Override
	public ApiResponse<UserUpdateDTO> updateUserVeriftyEmail(UserUpdateDTO userUpdateDTO) {
		// 1. 取出/建立 所需資料
		String token = userUpdateDTO.getToken();
		String newEmail = userUpdateDTO.getNewEmail();
		LocalDateTime now = LocalDateTime.now();
	    Users tableUser = usersRepository.findByAccount(userUpdateDTO.getUsername())
	        .orElseThrow(() -> new UserNoFoundException("帳號或密碼錯誤"));
	    
	    // 2. 驗證 token
 		Optional<UserVerify> optUserVerify = usersVerifyRepository.findByToken(token);
 		if (optUserVerify.isEmpty()) {
 			throw new TokenFailureException("驗證碼 無效或不存在");
 		}
 		UserVerify userVerify = optUserVerify.get();
 		if (userVerify.getIsUsed()) {
 			throw new TokenFailureException("驗證碼 已經被使用");
 		}
 		if (userVerify.getExpiryTime().isBefore(now)) {
			throw new TokenFailureException("驗證碼 已經過期");
		}
 		if (!userVerify.getNewEmailTarget().equals(newEmail)) {
			throw new TokenFailureException("驗證碼 信箱錯誤或與待驗證信箱不符");
		}
 		
 		// 3. 驗證成功 後 確認已經使用 並回傳
 		userVerify.setIsUsed(true);
 		usersVerifyRepository.save(userVerify);
 		
	    // 4. 返回處理
	    userUpdateDTO.setPassword(null);
	    userUpdateDTO.setNewPassword(null);
	    userUpdateDTO.setNewEmail(newEmail);
	    userUpdateDTO.setToken(null);
	    userUpdateDTO.setMailIsActive(true);
	    
		return new ApiResponse<>(true, "信箱: " + newEmail + "\n 已經通過驗證", userUpdateDTO);
	}
	
	@Override
	public ApiResponse<UserUpdateDTO> updateUser(UserUpdateDTO userUpdateDTO) {
		// 1. 複查一次 
	    Users tableUser = usersRepository.findByAccount(userUpdateDTO.getUsername())
	        .orElseThrow(() -> new UserNoFoundException("帳號或密碼錯誤"));
	    
	    // 2. 更新資料, 同時檢查 null、空字串、和空白字元
	    String rawPassword = userUpdateDTO.getNewPassword();
	    String newEmail = userUpdateDTO.getNewEmail();
	    String newPhoneNumber = userUpdateDTO.getNewPhoneNumber();
	    
		if (rawPassword != null && !rawPassword.isBlank()) {
			
			String hashPassword = passwordSecurity.hashPassword(rawPassword);
			tableUser.setPassword(hashPassword);
			
			// 密碼改變， 同時將所有 UserVerify Token 作廢 
			usersVerifyRepository.markAllUnusedTokenAsUsed(tableUser.getAccount());
		}
		
		if (newEmail != null && !newEmail.isBlank() && userUpdateDTO.getMailIsActive() == true) {
			tableUser.setEmail(userUpdateDTO.getNewEmail());
		}
		
		if (newPhoneNumber != null && !newPhoneNumber.isBlank()) {
			tableUser.setPhoneNumber(userUpdateDTO.getNewPhoneNumber());
		}
		
		tableUser = usersRepository.save(tableUser);
		
		// 4. Entity 轉 DTO
	    userUpdateDTO = modelMapper.map(tableUser, UserUpdateDTO.class);
	    
	    // 5. 返回處理
	    userUpdateDTO.setPassword(null);
	    userUpdateDTO.setNewPassword(null);
	    userUpdateDTO.setToken(null);
	    
	    return new ApiResponse<>(true, "資料更新完成 請重新登入", userUpdateDTO);
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
		// 根據不同 api 導向 不同用途 (1. verify 2. reset-password)
		String verificationLink = "http://localhost:8080/api/users/" + apiName + "?token=" + userVerify.getToken();			
		emailServiceImpl.sendVerificationEmail(email, subject, verificationLink);
	}
	
	@Override
	public void generateUserTokenByNewEmail(Users users, String subject, String apiName, String newEmail) {
		// 1. 取出/建立 所需資料
		String account = users.getAccount();
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
		userVerify.setNewEmailTarget(newEmail);
		
		usersVerifyRepository.save(userVerify);
		
		// 3. 寄出驗證信
		// 根據不同 api 導向 不同用途 (3. reset-email)
		String verificationLink = "http://localhost:8080/api/users/" + apiName + "?token=" + userVerify.getToken();			
		emailServiceImpl.sendVerificationEmail(newEmail, subject, verificationLink);
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
