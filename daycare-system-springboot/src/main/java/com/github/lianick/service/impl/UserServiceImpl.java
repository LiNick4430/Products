package com.github.lianick.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.github.lianick.exception.TokenFailureException;
import com.github.lianick.exception.UserExistException;
import com.github.lianick.exception.UserNoFoundException;
import com.github.lianick.exception.ValueMissException;
import com.github.lianick.model.dto.user.PasswordAwareDTO;
import com.github.lianick.model.dto.user.UserDeleteDTO;
import com.github.lianick.model.dto.user.UserForgetPasswordDTO;
import com.github.lianick.model.dto.user.UserLoginDTO;
import com.github.lianick.model.dto.user.UserMeDTO;
import com.github.lianick.model.dto.user.UserRegisterDTO;
import com.github.lianick.model.dto.user.UserUpdateDTO;
import com.github.lianick.model.dto.user.UserVerifyDTO;
import com.github.lianick.model.eneity.UserVerify;
import com.github.lianick.model.eneity.Users;
import com.github.lianick.repository.UsersRepository;
import com.github.lianick.repository.UsersVerifyRepository;
import com.github.lianick.service.UserService;
import com.github.lianick.util.PasswordSecurity;
import com.github.lianick.util.SecurityUtils;
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
	
	@Override
	public Users convertToUser(UserRegisterDTO userRegisterDTO) {
		return modelMapper.map(userRegisterDTO, Users.class);
	}
	
	@Override
	@PreAuthorize("isAuthenticated()")
	public UserMeDTO getUserDetails() {
		/* 從 JWT 獲取身份：確認操作者身份
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String currentUsername = authentication.getName(); // JWT 中解析出來的帳號
	    */
		String currentUsername = SecurityUtils.getCurrentUsername();
	    
	    // 1. 找尋資料庫 對應的帳號(使用 JWT 提供的 currentUsername 進行查詢)
	    Users tableUser = usersRepository.findByAccount(currentUsername)
	        .orElseThrow(() -> new UserNoFoundException("帳號或密碼錯誤"));
	    
	    // 2. Entity 轉 DTO
	    UserMeDTO userMeDTO = modelMapper.map(tableUser, UserMeDTO.class);
	    
	    // 3. 返回處理
	    userMeDTO.setRoleNumber(tableUser.getRole().getRoleId());
	    userMeDTO.setRoleName(tableUser.getRole().getName());
	    
		return userMeDTO;
	}
	
	@Override
	public UserRegisterDTO registerUser(UserRegisterDTO userRegisterDTO) {
		// 0. 檢查數值完整性
		if(userRegisterDTO.getUsername() == null || userRegisterDTO.getUsername().isBlank() ||
				userRegisterDTO.getPassword() == null || userRegisterDTO.getPassword().isBlank() ||
				userRegisterDTO.getEmail() == null || userRegisterDTO.getEmail().isBlank() ||
				userRegisterDTO.getPhoneNumber() == null || userRegisterDTO.getPhoneNumber().isBlank()) {
			throw new ValueMissException("缺少必要的註冊資料 (帳號、密碼、信箱、電話號碼)");
		}
		
		if (usersRepository.findByAccount(userRegisterDTO.getUsername()).isPresent()) {
			throw new UserExistException("註冊失敗：帳號已有人使用");
		}
		if (usersRepository.findByEmail(userRegisterDTO.getEmail()).isPresent()) {
			throw new UserExistException("註冊失敗：信箱已有人使用");
		}
		
		// 1. DTO 轉 Entity
		userRegisterDTO.setRoleNumber(1L);	// 這裡的 只能建立 民眾帳號
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
		
		// return new ApiResponse<UserRegisterDTO>(true, "帳號建立成功, 請驗證信箱", userRegisterDTO);
		return userRegisterDTO;
	}

	@Override
	public UserVerifyDTO verifyUser(String token){
		// 0. 取出/建立 所需資料
		LocalDateTime now = LocalDateTime.now();
		
		// 1. 使用 Token 紀錄 找尋 UsersVerify 紀錄
		Optional<UserVerify> optUserVerify = usersVerifyRepository.findByToken(token);
		if (optUserVerify.isEmpty()) {
			throw new TokenFailureException("驗證碼 無效或不存在");
		}
		UserVerify userVerify = optUserVerify.get();
		Users users = userVerify.getUsers();
		
		// 2. 判斷 Token 狀態
		if (userVerify.getIsUsed()) {
			throw new TokenFailureException("驗證碼 已經被使用");
		}
		if (userVerify.getExpiryTime().isBefore(now)) {
			throw new TokenFailureException("驗證碼 已經過期");
		}
		
		// 3. 判斷 Users 狀態 以防止重複使用
		if (users.getIsActive()) {
			// Entity 轉 DTO
			UserVerifyDTO userVerifyDTO = modelMapper.map(users, UserVerifyDTO.class);
			
			// 返回處理: 清空 token
			userVerifyDTO.setToken(null);
			
			// return new ApiResponse<UserVerifyDTO>(true, "帳號已經是啟用狀態", null);
			return userVerifyDTO;
		}
		
		// 4. 啟用帳號
		userVerify.setIsUsed(true);
		users.setIsActive(true);
		users.setActiveDate(now);
		
		usersVerifyRepository.save(userVerify);
		users = usersRepository.save(users);
		
		// 5. Entity 轉 DTO
		UserVerifyDTO userVerifyDTO = modelMapper.map(users, UserVerifyDTO.class);
		
		// 6. 返回處理: 清空 token
		userVerifyDTO.setToken(null);
		
		// return new ApiResponse<UserVerifyDTO>(true, "帳號啟用成功", userVerifyDTO);
		return userVerifyDTO;
	}

	@Override
	public Users loginUser(UserLoginDTO userLoginDTO){
		
		// 0. 檢查數值完整性
		if(userLoginDTO.getUsername() == null || userLoginDTO.getUsername().isBlank() ||
				userLoginDTO.getPassword() == null || userLoginDTO.getPassword().isBlank()) {
			throw new ValueMissException("缺少帳號或密碼");
		}
		
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
    	tableUser = usersRepository.save(tableUser);
    	
    	return tableUser;
	}

	@Override
	public UserForgetPasswordDTO forgetPasswordSendEmail(UserForgetPasswordDTO userForgetPasswordDTO){
		// 0. 檢查數值完整性
		if (userForgetPasswordDTO.getUsername() == null || userForgetPasswordDTO.getUsername().isBlank()) {
		    throw new ValueMissException("缺少帳號資訊");
		}
		
		// 1. 找尋資料庫 對應的帳號
	    Users tableUser = usersRepository.findByAccount(userForgetPasswordDTO.getUsername())
	        .orElseThrow(() -> new UserNoFoundException("帳號不存在或已被刪除"));
	    
	    // 2. 產生驗證碼 同時 寄出 驗證信
	    generateUserToken(tableUser, "忘記密碼驗證信件", "reset/password");
	    
	    // 3. Entity 轉 DTO
	    userForgetPasswordDTO = modelMapper.map(tableUser, UserForgetPasswordDTO.class);
	    
	    // 4. 返回處理: 清空 敏感數值
	    userForgetPasswordDTO.setToken(null);
	    userForgetPasswordDTO.setPassword(null);
	    
		// return new ApiResponse<UserForgetPasswordDTO>(true, "驗證信寄出成功", userForgetPasswordDTO);
		return userForgetPasswordDTO;
	}
	
	@Override
	public UserForgetPasswordDTO forgetPasswordVerifty(String token){
		// 0. 取出/建立 所需資料
		LocalDateTime now = LocalDateTime.now();
		
		// 1. 使用 Token 紀錄 找尋 UsersVerify 紀錄
		Optional<UserVerify> optUserVerify = usersVerifyRepository.findByToken(token);
		if (optUserVerify.isEmpty()) {
			throw new TokenFailureException("驗證碼 無效或不存在");
		}
		UserVerify userVerify = optUserVerify.get();
		
		// 2. 判斷 Token 狀態
		if (userVerify.getIsUsed()) {
			throw new TokenFailureException("驗證碼 已經被使用");
		}
		if (userVerify.getExpiryTime().isBefore(now)) {
			throw new TokenFailureException("驗證碼 已經過期");
		}
		
		// 3. 取出 Entity
		Users tableUser = userVerify.getUsers();
		
		// 4. Entity 轉 DTO
	    UserForgetPasswordDTO userForgetPasswordDTO = modelMapper.map(tableUser, UserForgetPasswordDTO.class);
	    
	    // 5. 返回處理: 新增 到期時間 +  清空 敏感數值
	    userForgetPasswordDTO.setExpiryTime(userVerify.getExpiryTime());
	    userForgetPasswordDTO.setToken(null);
	    userForgetPasswordDTO.setPassword(null);
		
		// return new ApiResponse<UserForgetPasswordDTO>(true, "驗證成功, 進入修改密碼網頁", userForgetPasswordDTO);
		return userForgetPasswordDTO;
	}
	
	@Override
	public UserForgetPasswordDTO forgetPasswordUpdatePassword(UserForgetPasswordDTO userForgetPasswordDTO){
		// 0. 檢查數值完整性
		if (userForgetPasswordDTO.getUsername() == null || userForgetPasswordDTO.getUsername().isBlank() ||
				userForgetPasswordDTO.getPassword() == null || userForgetPasswordDTO.getPassword().isBlank() ||
				userForgetPasswordDTO.getToken() == null || userForgetPasswordDTO.getToken().isBlank()) {
			throw new ValueMissException("缺少特定資料 (帳號、新密碼或Token)");
		}
		
		// 1. 取出/建立 所需資料
		Users tableUser = usersRepository.findByAccount(userForgetPasswordDTO.getUsername())
			        .orElseThrow(() -> new UserNoFoundException("帳號不存在或已被刪除"));
		String token = userForgetPasswordDTO.getToken();
		String rawPassword = userForgetPasswordDTO.getPassword();
		LocalDateTime now = LocalDateTime.now();
		
		// 2. 驗證 token, 預設已經膯過第二步 這裡將不再驗證 時間
		Optional<UserVerify> optUserVerify = usersVerifyRepository.findByToken(token);
		if (optUserVerify.isEmpty()) {
			throw new TokenFailureException("驗證碼 無效或不存在");
		}
		UserVerify userVerify = optUserVerify.get();
		
		// 額外的安全性檢查：確保 token 是屬於這個 user 的
        if (!userVerify.getUsers().getAccount().equals(tableUser.getAccount())) {
            throw new TokenFailureException("驗證碼 無效或不存在");
        }
		
        if (userVerify.getIsUsed()) {
        	throw new TokenFailureException("驗證碼 已經被使用");
        }

        if (userVerify.getExpiryTime().isBefore(now)) {
        	throw new TokenFailureException("驗證碼 已過期，請重新申請");
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
		
		// return new ApiResponse<UserForgetPasswordDTO>(true, "密碼更新完成, 請使用新密碼登入", null);
		return userForgetPasswordDTO;
	}

	@Override
	@PreAuthorize("isAuthenticated()")
	public UserUpdateDTO updateUserCheckPassword(UserUpdateDTO userUpdateDTO) {
		/* 從 JWT 獲取身份：確認操作者身份
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String currentUsername = authentication.getName(); // JWT 中解析出來的帳號
	    */
		String currentUsername = SecurityUtils.getCurrentUsername();
		
		// 0. 檢查數值完整性 (這裡只檢查 password 即可，因為 username 已經從 JWT 獲得並驗證)
		if (userUpdateDTO.getPassword() == null || userUpdateDTO.getPassword().isBlank()) {
			throw new ValueMissException("缺少舊密碼");
		}
		
		// 1. 找尋資料庫 對應的帳號(使用 JWT 提供的 currentUsername 進行查詢)
	    Users tableUser = usersRepository.findByAccount(currentUsername)
	        .orElseThrow(() -> new UserNoFoundException("帳號或密碼錯誤"));
	    
	    // 2. 使用 checkPassword 方法 複查 密碼是否相同
	    if (!checkPassword(userUpdateDTO, tableUser)) {
	    	throw new UserNoFoundException("密碼錯誤");
	    }
	    
	    // 3. Entity 轉 DTO
	    userUpdateDTO = modelMapper.map(tableUser, UserUpdateDTO.class);
	    
	    // 4. 返回處理: 清空 password
	    userUpdateDTO.setPassword(null);
	    userUpdateDTO.setNewPassword(null);
	    
		// return new ApiResponse<>(true, "密碼確認成功, 進入修改資料網頁", userUpdateDTO);
		return userUpdateDTO;
	}

	@Override
	@PreAuthorize("isAuthenticated()")
	public UserUpdateDTO updateUser(UserUpdateDTO userUpdateDTO) {
		/* 從 JWT 獲取身份：確認操作者身份
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String currentUsername = authentication.getName(); // JWT 中解析出來的帳號
	    */
		String currentUsername = SecurityUtils.getCurrentUsername();
		
		// 1. 複查一次 
	    Users tableUser = usersRepository.findByAccount(currentUsername)
	        .orElseThrow(() -> new UserNoFoundException("帳號不存在或已被刪除"));
	    
	    // 2. 更新資料, 同時檢查 null、空字串、和空白字元
	    String rawPassword = userUpdateDTO.getNewPassword();
	    String newPhoneNumber = userUpdateDTO.getNewPhoneNumber();
	    
		if (rawPassword != null && !rawPassword.isBlank()) {
			
			String hashPassword = passwordSecurity.hashPassword(rawPassword);
			tableUser.setPassword(hashPassword);
			
			// 密碼改變， 同時將所有 UserVerify Token 作廢 
			usersVerifyRepository.markAllUnusedTokenAsUsed(tableUser.getAccount());
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
	    
	    // 如果密碼被修改，強制使用者登出 (清空 JWT 狀態)
	    if (rawPassword != null && !rawPassword.isBlank()) {
	        // 立即清空 Security Context，強制要求用戶使用新密碼重新登入
	        SecurityContextHolder.clearContext(); 
	    }
	    
	    // return new ApiResponse<>(true, "資料更新完成 請重新登入", userUpdateDTO);
	    return userUpdateDTO;
	}
	
	@Override
	@PreAuthorize("isAuthenticated()")
	public void deleteUser(UserDeleteDTO userDeleteDTO){
		/* 從 JWT 獲取身份：確認操作者身份
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String currentUsername = authentication.getName(); // JWT 中解析出來的帳號
	    */
		String currentUsername = SecurityUtils.getCurrentUsername();
		
		// 1. 找尋資料庫 對應的帳號
	    Users tableUser = usersRepository.findByAccount(currentUsername)
	        .orElseThrow(() -> new UserNoFoundException("帳號或密碼錯誤"));

	    // 2. 使用 checkPassword 方法 複查 密碼是否相同
	    if (!checkPassword(userDeleteDTO, tableUser)) {
	    	throw new UserNoFoundException("帳號或密碼錯誤");
	    }
	    
	    // 3. 建立 變數
	    LocalDateTime deleteTime = LocalDateTime.now();
	    // 使用精確到毫秒的時間格式，確保尾碼在短時間內也是唯一的
	    String deleteSuffix = "_DEL_" + deleteTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
	    
	    // 4. 執行 軟刪除
	    tableUser.setDeleteAt(deleteTime);
	    // 破壞 具有 唯一性 的 欄位
	    tableUser.setEmail(tableUser.getEmail() + deleteSuffix);
	    tableUser.setAccount(tableUser.getAccount() + deleteSuffix);
	    // 5. 關聯欄位
		List<UserVerify> userVerifies = tableUser.getUserVerifies();
		if (userVerifies != null) {
			userVerifies.forEach(userVerify -> {
				userVerify.setDeleteAt(deleteTime);
			});
		}
		
		// 回存
		usersRepository.save(tableUser);
		
		// 安全強化：帳號刪除後，強制登出
		SecurityContextHolder.clearContext();
	    
		// return new ApiResponse<Void>(true, "帳號刪除成功", null);
	}

	@Override
	public void generateUserToken(Users users, String subject, String apiName) {
		// 1. 取出/建立 所需資料
		String account = users.getAccount();
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
		usersVerifyRepository.markAllUnusedTokenAsUsed(account);
		
		// 2. 產生驗證碼 並存回去
		UserVerify userVerify = new UserVerify();
		userVerify.setToken(token);
		userVerify.setExpiryTime(expiryTime);
		userVerify.setUsers(users);
		
		usersVerifyRepository.save(userVerify);
		
		// 3. 寄出驗證信
		// 根據不同 api 導向 不同用途 (1. verify 2. reset/password)
		String verificationLink = "http://localhost:8080/email/" + apiName + "?token=" + userVerify.getToken();			
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
