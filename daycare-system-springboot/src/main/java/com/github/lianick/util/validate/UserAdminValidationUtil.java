package com.github.lianick.util.validate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.lianick.exception.RoleFailureException;
import com.github.lianick.exception.UserExistException;
import com.github.lianick.exception.ValueMissException;
import com.github.lianick.model.dto.userAdmin.UserAdminCreateDTO;
import com.github.lianick.repository.UsersRepository;
import com.github.lianick.util.SecurityUtil;

/**
 * 負責處理 UserAdmin 相關的 完整性 檢測
 * */
@Service
public class UserAdminValidationUtil {

	@Autowired
	private UsersRepository usersRepository;
	
	/**
	 * 檢查 UserAdminCreateDTO 的 完整性 + 唯一性
	 * */
	public void validateCreateFields(UserAdminCreateDTO userAdminCreateDTO) {
		if(userAdminCreateDTO.getUsername() == null || userAdminCreateDTO.getUsername().isBlank() ||
				userAdminCreateDTO.getPassword() == null || userAdminCreateDTO.getPassword().isBlank() ||
				userAdminCreateDTO.getEmail() == null || userAdminCreateDTO.getEmail().isBlank() ||
				userAdminCreateDTO.getPhoneNumber() == null || userAdminCreateDTO.getPhoneNumber().isBlank() ||
				userAdminCreateDTO.getRoleNumber() == null) {
			throw new ValueMissException("缺少必要的註冊資料 (帳號、密碼、信箱、電話號碼、角色ID)");
		}
		if(userAdminCreateDTO.getName() == null || userAdminCreateDTO.getName().isBlank() || 
				userAdminCreateDTO.getJobTitle() == null || userAdminCreateDTO.getJobTitle().isBlank() ||
				userAdminCreateDTO.getOrganizationId() == null) {
			throw new ValueMissException("缺少必要的基本資料 (員工姓名、員工職稱、機構ID)");
		}
		
		if (usersRepository.findByAccount(userAdminCreateDTO.getUsername()).isPresent()) {
			throw new UserExistException("註冊失敗：帳號已有人使用");
		}
		if (usersRepository.findByEmail(userAdminCreateDTO.getEmail()).isPresent()) {
			throw new UserExistException("註冊失敗：信箱已有人使用");
		}
		if (userAdminCreateDTO.getRoleNumber() != 2L &&
				userAdminCreateDTO.getRoleNumber() != 3L) {
			throw new RoleFailureException("註冊失敗：角色 設定錯誤");
		}
	}
	
	/**
	 * 檢查 角色ID 權限
	 * */
	public void validateRoleForCreate(Long roleNumber) {
		// 目前預設 可以創建 和自己 同權限的 帳號 (3L 可以建立 2L 或 3L)
		if (SecurityUtil.getCurrentRoleNumber() < roleNumber) {
			throw new RoleFailureException("角色 設定錯誤");
		}
	}
	
	/**
	 * 檢查 角色ID 的合法 和 權限
	 * */
	public void validateRoleForUpdate(Long newRoleNumber) {
		if (newRoleNumber == null) {
			return;
		}
		
		if (newRoleNumber != 2L && newRoleNumber != 3L) {
			throw new RoleFailureException("更新失敗：角色 設定錯誤");
		}
		
		// 目前預設 可以創建 和自己 同權限的 帳號 (3L 可以建立 2L 或 3L)
		if (SecurityUtil.getCurrentRoleNumber() < newRoleNumber) {
			throw new RoleFailureException("更新失敗：角色 設定錯誤");
		}
	}
}
