package com.github.lianick.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.lianick.exception.UserExistException;
import com.github.lianick.model.dto.user.UserDeleteDTO;
import com.github.lianick.model.dto.user.UserRegisterDTO;
import com.github.lianick.model.dto.userAdmin.UserAdminCreateDTO;
import com.github.lianick.model.dto.userAdmin.UserAdminDTO;
import com.github.lianick.model.dto.userAdmin.UserAdminUpdateDTO;
import com.github.lianick.model.eneity.Organization;
import com.github.lianick.model.eneity.Role;
import com.github.lianick.model.eneity.UserAdmin;
import com.github.lianick.model.eneity.Users;
import com.github.lianick.repository.UserAdminRepository;
import com.github.lianick.repository.UsersRepository;
import com.github.lianick.service.UserAdminService;
import com.github.lianick.service.UserService;
import com.github.lianick.util.PasswordSecurity;
import com.github.lianick.util.SecurityUtil;
import com.github.lianick.util.UserSecurityUtil;
import com.github.lianick.util.validate.UserAdminValidationUtil;

@Service
@Transactional				// 確保 完整性 
public class UserAdminServiceImpl implements UserAdminService{

	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired 
	private UserService userService;
	
	@Autowired
	private UserAdminRepository userAdminRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private UserSecurityUtil userSecurityUtil;
	
	@Autowired
	private UserAdminValidationUtil userAdminValidationUtil;
	
	@Autowired
	private EntityFetcher entityFetcher;
	
	@Autowired
	private PasswordSecurity passwordSecurity;
	
	@Override
	@PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	public UserAdminDTO findUserAdmin() {
		// 1. 從 JWT 取出資料
		UserAdmin userAdmin = userSecurityUtil.getCurrentUserAdminEntity();
		
		// 2. Entity -> DTO
		return modelMapper.map(userAdmin, UserAdminDTO.class);
	}
	
	@Override
	@PreAuthorize("hasAuthority('ROLE_MANAGER')")
	public List<UserAdminDTO> findAllUserAdmin() {
		List<UserAdminDTO> userAdminDTOs = userAdminRepository.findAll()
															.stream()
															.map(userAdmin -> {
																UserAdminDTO userAdminDTO = modelMapper.map(userAdmin, UserAdminDTO.class);
																return userAdminDTO;
															}).toList();
		return userAdminDTOs;
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_MANAGER')")
	public UserAdminDTO findByUsername(UserAdminDTO userAdminDTO) {
		UserAdmin userAdmin = entityFetcher.getUserAdminByUsername(userAdminDTO.getUsername());

		userAdminDTO = modelMapper.map(userAdmin, UserAdminDTO.class);
		return userAdminDTO;
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_MANAGER')")
	public UserAdminDTO createUserAdmin(UserAdminCreateDTO userAdminCreateDTO) {
		// 0. 檢查 是否 自己帳號
		isUserMe(userAdminCreateDTO.getUsername());
		
		// 0. 檢查數值完整性
		userAdminValidationUtil.validateCreateFields(userAdminCreateDTO);
		
		// 目前預設 可以創建 和自己 同權限的 帳號 (3L 可以建立 2L 或 3L)
		userAdminValidationUtil.validateRoleForCreate(userAdminCreateDTO.getRoleNumber());
		
		Organization organization = entityFetcher.getOrganizationById(userAdminCreateDTO.getOrganizationId(), "註冊失敗：機構 設定錯誤");
		
		// 1. 建立 User
		Users users = userService.convertToUser(modelMapper.map(userAdminCreateDTO, UserRegisterDTO.class));
		
		// 2. 密碼 取出加密處理
		String rawPassword = users.getPassword();
		String hashPassword = passwordSecurity.hashPassword(rawPassword);
		users.setPassword(hashPassword);
		
		// 3. users 儲存
		users = usersRepository.save(users);
		
		// 4. 產生驗證碼 同時 寄出 驗證信
		userService.generateUserToken(users, "帳號啟用信件", "verify");
		
		// 5. 建立 UserAdmin
		UserAdmin userAdmin = new UserAdmin();
		userAdmin.setUsers(users);
		userAdmin.setName(userAdminCreateDTO.getName());
		userAdmin.setJobTitle(userAdminCreateDTO.getJobTitle());
		userAdmin.setOrganization(organization);
		
		// 6. UserAdmin 儲存
		userAdmin = userAdminRepository.save(userAdmin);
		
		// 7. Entity 轉 DTO
		UserAdminDTO userAdminDTO = modelMapper.map(userAdmin, UserAdminDTO.class);
		
		return userAdminDTO;
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_MANAGER')")
	public UserAdminDTO updateUserAdmin(UserAdminUpdateDTO userAdminUpdateDTO) {
		// 0. 檢查 是否 自己帳號
		isUserMe(userAdminUpdateDTO.getUsername());
		
		userAdminValidationUtil.validateRoleForUpdate(userAdminUpdateDTO.getNewRoleNumber());
		
		// 0. 檢查數值完整性
		Role role = null;
		if (userAdminUpdateDTO.getNewRoleNumber() != null) {
			role = entityFetcher.getRoleById(userAdminUpdateDTO.getNewRoleNumber(), "更新失敗：角色 設定錯誤");
		}
		
		Organization organization = null;
		if (userAdminUpdateDTO.getNewOrganizationId() != null) {
			organization = entityFetcher.getOrganizationById(userAdminUpdateDTO.getNewOrganizationId(), "更新失敗：機構 設定錯誤");
		}
		
		// 1. 取出帳號
		Users users = entityFetcher.getUsersByUsername(userAdminUpdateDTO.getUsername());
		UserAdmin userAdmin = entityFetcher.getUserAdminByUsername(userAdminUpdateDTO.getUsername());
		
		
		// 2. 依序檢查 數值 並 存入
		if (userAdminUpdateDTO.getNewName() != null && !userAdminUpdateDTO.getNewName().isBlank()) {
			userAdmin.setName(userAdminUpdateDTO.getNewName());
		}
		if (userAdminUpdateDTO.getNewJobTitle() != null && !userAdminUpdateDTO.getNewJobTitle().isBlank()) {
			userAdmin.setJobTitle(userAdminUpdateDTO.getNewJobTitle());
		}
		if (role != null) {
			users.setRole(role);
		}
		if (organization != null) {
			userAdmin.setOrganization(organization);
		}
		
		// 3. 回存
		users = usersRepository.save(users);
		userAdmin = userAdminRepository.save(userAdmin);
		
		// 4. Entity 轉 DTO
		UserAdminDTO userAdminDTO = modelMapper.map(userAdmin, UserAdminDTO.class);
		
		return userAdminDTO;
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_MANAGER')")
	public void deleteUserAdmin(UserDeleteDTO userDeleteDTO) {
		// 0. 檢查 是否 自己帳號
		isUserMe(userDeleteDTO.getUsername());
		
		// 1. 查詢帳號是否存在
		Users users = entityFetcher.getUsersByUsername(userDeleteDTO.getUsername());
		UserAdmin userAdmin = entityFetcher.getUserAdminByUsername(userDeleteDTO.getUsername());
		
		// 2. 進行軟刪除 並且 回存
		LocalDateTime deleteTime = LocalDateTime.now();
	    String deleteSuffix = "_DEL_" + deleteTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
	    
		userAdmin.setDeleteAt(deleteTime);
		
		users.setDeleteAt(deleteTime);
		users.setEmail(users.getEmail() + deleteSuffix);
		users.setAccount(users.getAccount() + deleteSuffix);
		
		userAdminRepository.save(userAdmin);
		usersRepository.save(users);
	}
	
	
	/**
	 * 檢查 是否 自己帳號
	 * */
	private void isUserMe(String username) {
		String currentUsername = SecurityUtil.getCurrentUsername();
		if (currentUsername.equals(username)) {
			throw new UserExistException("失敗：無法通過此管理介面修改自己的帳號資料");
		}
	}
}
