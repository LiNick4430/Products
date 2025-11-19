package com.github.lianick.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.lianick.exception.OrganizationFailureException;
import com.github.lianick.exception.RoleFailureException;
import com.github.lianick.exception.UserExistException;
import com.github.lianick.exception.UserNoFoundException;
import com.github.lianick.exception.ValueMissException;
import com.github.lianick.model.dto.user.UserDeleteDTO;
import com.github.lianick.model.dto.user.UserRegisterDTO;
import com.github.lianick.model.dto.userAdmin.UserAdminCreateDTO;
import com.github.lianick.model.dto.userAdmin.UserAdminDTO;
import com.github.lianick.model.dto.userAdmin.UserAdminUpdateDTO;
import com.github.lianick.model.eneity.Organization;
import com.github.lianick.model.eneity.Role;
import com.github.lianick.model.eneity.UserAdmin;
import com.github.lianick.model.eneity.Users;
import com.github.lianick.repository.OrganizationRepository;
import com.github.lianick.repository.RoleRepository;
import com.github.lianick.repository.UserAdminRepository;
import com.github.lianick.repository.UsersRepository;
import com.github.lianick.service.UserAdminService;
import com.github.lianick.service.UserService;
import com.github.lianick.util.PasswordSecurity;
import com.github.lianick.util.SecurityUtil;
import com.github.lianick.util.UserSecurityUtil;

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
	private OrganizationRepository organizationRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private UserSecurityUtil userSecurityUtil;
	
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
		UserAdmin userAdmin = getUserAdminEntity(userAdminDTO.getUsername());

		userAdminDTO = modelMapper.map(userAdmin, UserAdminDTO.class);
		return userAdminDTO;
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_MANAGER')")
	public UserAdminDTO createUserAdmin(UserAdminCreateDTO userAdminCreateDTO) {
		// 0. 檢查 是否 自己帳號
		isUserMe(userAdminCreateDTO.getUsername());
		
		// 0. 檢查數值完整性
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
		// 目前預設 可以創建 和自己 同權限的 帳號 (3L 可以建立 2L 或 3L)
		if (SecurityUtil.getCurrentRoleNumber() < userAdminCreateDTO.getRoleNumber()) {
			throw new RoleFailureException("註冊失敗：角色 設定錯誤");
		}
		Organization organization = organizationRepository.findById(userAdminCreateDTO.getOrganizationId())
				.orElseThrow(() -> new OrganizationFailureException("註冊失敗：機構 設定錯誤"));
		
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
		
		// 0. 檢查數值完整性
		Role role = null;
		Organization organization = null;
		if (userAdminUpdateDTO.getNewRoleNumber() != null) {
			if (userAdminUpdateDTO.getNewRoleNumber().equals(2L) &&
					userAdminUpdateDTO.getNewRoleNumber().equals(3L)) {
				throw new RoleFailureException("更新失敗：角色 設定錯誤");
			}
			// 目前預設 可以創建 和自己 同權限的 帳號 (3L 可以建立 2L 或 3L)
			if (SecurityUtil.getCurrentRoleNumber() < userAdminUpdateDTO.getNewRoleNumber()) {
				throw new RoleFailureException("更新失敗：角色 設定錯誤");
			}
			role = roleRepository.findById(userAdminUpdateDTO.getNewRoleNumber())
					.orElseThrow(() -> new RoleFailureException("更新失敗：角色 設定錯誤"));
		}
		if (userAdminUpdateDTO.getNewOrganizationId() != null) {
			organization = organizationRepository.findById(userAdminUpdateDTO.getNewOrganizationId())
					.orElseThrow(() -> new OrganizationFailureException("更新失敗：機構 設定錯誤"));
		}
		
		// 1. 取出帳號
		Users users = getUserEntity(userAdminUpdateDTO.getUsername());
		UserAdmin userAdmin = getUserAdminEntity(userAdminUpdateDTO.getUsername());
		
		
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
		Users users = getUserEntity(userDeleteDTO.getUsername());
		UserAdmin userAdmin = getUserAdminEntity(userDeleteDTO.getUsername());
		
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
	
	/**
	 * 使用 username 找到 userAdmin
	 * */
	private Users getUserEntity(String username) {
		Users tableUser = usersRepository.findByAccount(username)
				.orElseThrow(() -> new UserNoFoundException("基礎帳號不存在"));
		
		return tableUser;
	}
	
	/**
	 * 使用 username 找到 userAdmin
	 * */
	private UserAdmin getUserAdminEntity(String username) {
		Users tableUser = getUserEntity(username);
		
		UserAdmin userAdmin = userAdminRepository.findByUsers(tableUser)
				.orElseThrow(() -> new UserNoFoundException("用戶存在，但非員工帳號"));
		
		return userAdmin;
	}
}
