package com.github.lianick.util.validate;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.lianick.exception.TokenFailureException;
import com.github.lianick.exception.UserExistException;
import com.github.lianick.exception.ValueMissException;
import com.github.lianick.model.dto.user.UserForgetPasswordDTO;
import com.github.lianick.model.dto.user.UserLoginDTO;
import com.github.lianick.model.dto.user.UserRegisterDTO;
import com.github.lianick.model.dto.user.UserUpdateDTO;
import com.github.lianick.model.eneity.UserVerify;
import com.github.lianick.model.eneity.Users;
import com.github.lianick.repository.UsersRepository;

/**
 * 負責處理 User 相關的 完整性 檢測
 * */
@Service
public class UserValidationUtil {
	
	@Autowired
	private UsersRepository usersRepository;
	
	/**
     * 檢查 UserRegisterDTO 的 完整性 + 唯一性
     */
	public void validateRegistrationFields(UserRegisterDTO userRegisterDTO) {
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
	}
	
	/**
	 * 檢查 UserLoginDTO 的 完整性
	 * */
	public void validateLoginFields(UserLoginDTO userLoginDTO) {
		if(userLoginDTO.getUsername() == null || userLoginDTO.getUsername().isBlank() ||
				userLoginDTO.getPassword() == null || userLoginDTO.getPassword().isBlank()) {
			throw new ValueMissException("缺少帳號或密碼");
		}
	}
	
	/**
	 * 檢查 UserForgetPasswordDTO 在 寄信時 的 完整性 
	 * */
	public void validateForgetPasswordForSendEmail(UserForgetPasswordDTO userForgetPasswordDTO) {
		if (userForgetPasswordDTO.getUsername() == null || userForgetPasswordDTO.getUsername().isBlank()) {
		    throw new ValueMissException("缺少帳號資訊");
		}
	}
	
	/**
	 * 檢查 UserForgetPasswordDTO 在 更新密碼時 的 完整性 
	 * */
	public void validateForgetPasswordForUpdate(UserForgetPasswordDTO userForgetPasswordDTO) {
		if (userForgetPasswordDTO.getUsername() == null || userForgetPasswordDTO.getUsername().isBlank() ||
				userForgetPasswordDTO.getPassword() == null || userForgetPasswordDTO.getPassword().isBlank() ||
				userForgetPasswordDTO.getToken() == null || userForgetPasswordDTO.getToken().isBlank()) {
			throw new ValueMissException("缺少特定資料 (帳號、新密碼或Token)");
		}
	}
	
	/**
	 * 檢查 UserUpdateDTO 在 密碼確認時 的 完整性 
	 * */
	public void validateUpdateForCheck(UserUpdateDTO userUpdateDTO) {
		if (userUpdateDTO.getPassword() == null || userUpdateDTO.getPassword().isBlank()) {
			throw new ValueMissException("缺少舊密碼");
		}
	}
    
	/**
     * 用來判斷 驗證碼 的 狀態
     * */
    public void validateToken(UserVerify userVerify, LocalDateTime now) {
    	if (userVerify.getIsUsed()) {
			throw new TokenFailureException("驗證碼 已經被使用");
		}
		if (userVerify.getExpiryTime().isBefore(now)) {
			throw new TokenFailureException("驗證碼 已經過期");
		}
    }
    
    /**
     * 檢查 Users 角色的權限 是否是 管理層("ROLE_MANAGER")?
     * */
    public boolean validateUserIsManager(Users users) {
    	if (users == null) {
			throw new ValueMissException("缺少必要資訊(使用者)");
		}
    	
    	boolean isManager = users.getRole().getName().equals("ROLE_MANAGER");
    	
    	return isManager;
    }
}
