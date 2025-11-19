package com.github.lianick.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.lianick.exception.FormatterFailureException;
import com.github.lianick.exception.RoleFailureException;
import com.github.lianick.exception.UserExistException;
import com.github.lianick.exception.UserNoFoundException;
import com.github.lianick.exception.ValueMissException;
import com.github.lianick.model.dto.user.UserDeleteDTO;
import com.github.lianick.model.dto.userPublic.UserPublicDTO;
import com.github.lianick.model.dto.userPublic.UserPublicCreateDTO;
import com.github.lianick.model.dto.userPublic.UserPublicUpdateDTO;
import com.github.lianick.model.eneity.ChildInfo;
import com.github.lianick.model.eneity.DocumentPublic;
import com.github.lianick.model.eneity.UserPublic;
import com.github.lianick.model.eneity.UserVerify;
import com.github.lianick.model.eneity.Users;
import com.github.lianick.repository.UserPublicRepository;
import com.github.lianick.repository.UsersRepository;
import com.github.lianick.service.UserPublicService;
import com.github.lianick.service.UserService;
import com.github.lianick.util.DateValidationUtil;
import com.github.lianick.util.UserSecurityUtil;

@Service
@Transactional				// 確保 完整性 
public class UserPublicServiceImpl implements UserPublicService{

	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private UserPublicRepository userPublicRepository;
	
	@Autowired
	private DateValidationUtil dateValidationUtil;
	
	@Autowired
	private UserSecurityUtil userSecurityUtil;
	
	@Autowired 
	private UserService userService;
	
	@Override
	@PreAuthorize("hasAuthority('ROLE_PUBLIC')") 
	public UserPublicDTO findUserPublicDTO() {
		UserPublic userPublic = userSecurityUtil.getCurrentUserPublicEntity();
		return modelMapper.map(userPublic, UserPublicDTO.class);
	}
	
	@Override
	@PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	public List<UserPublicDTO> findAllUserPublic() {
		List<UserPublicDTO> userPublicDTOs = userPublicRepository.findAll()
																.stream()
																.map(userPublic -> {
																	UserPublicDTO userPublicDTO = modelMapper.map(userPublic, UserPublicDTO.class);
																	userPublicDTO.setUsername(userPublic.getUsers().getAccount());
																	
																	return userPublicDTO;
																}).toList();
		return userPublicDTOs;
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	public UserPublicDTO findByUsername(UserPublicDTO userPublicDTO) {
		// 0. 檢查數值完整性
		if (userPublicDTO.getUsername() == null || userPublicDTO.getUsername().isBlank()) {
			throw new ValueMissException("缺少帳號");
		}
		
		// 1. 找尋資料庫 對應的帳號
		Users tableUser = usersRepository.findByAccount(userPublicDTO.getUsername())
		        .orElseThrow(() -> new UserNoFoundException("帳號錯誤"));
		
		UserPublic userPublic = userPublicRepository.findByUsers(tableUser)
				.orElseThrow(() -> new UserNoFoundException("帳號錯誤"));
		
		// 2. Entity 轉 DTO
		return modelMapper.map(userPublic, UserPublicDTO.class);
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_PUBLIC')")
	public UserPublicCreateDTO createUserPublic(UserPublicCreateDTO userPublicCreateDTO) {
		// 0. 從 JWT 找尋資料庫 對應的帳號 同時檢查角色ID
		Users tableUser = userSecurityUtil.getCurrentUserEntity();
		
		// 1. 檢查數值完整性
		if (userPublicCreateDTO.getName() == null || userPublicCreateDTO.getName().isBlank() || 
			userPublicCreateDTO.getNationalIdNo() == null || userPublicCreateDTO.getNationalIdNo().isBlank() || 
			userPublicCreateDTO.getBirthdate() == null || userPublicCreateDTO.getBirthdate().isBlank() || 
			userPublicCreateDTO.getRegisteredAddress() == null || userPublicCreateDTO.getRegisteredAddress().isBlank() || 
			userPublicCreateDTO.getMailingAddress() == null || userPublicCreateDTO.getMailingAddress().isBlank() ) {
			throw new ValueMissException("缺少特定資料(帳號 角色 民眾姓名 生日 身分證字號 戶籍地址 實際地址)");
		}
		
		// 2. 檢查 生日 是否符合 格式 / 身分證字號 是否 已經被使用
		
		// 是否 "yyyy-MM-dd"
		if (!dateValidationUtil.isValidLocalDate(userPublicCreateDTO.getBirthdate())) {
			throw new FormatterFailureException("生日格式錯誤，必須是 yyyy-MM-dd 格式");
		}
		// 檢查 身分證字號 是否 已經被使用
		if (userPublicRepository.findByNationalIdNo(userPublicCreateDTO.getNationalIdNo()).isPresent()) {
			throw new UserExistException("基本資料填寫：身份證字號已經使用");
		}
		
		if (tableUser.getRole().getRoleId() != 1L) {
			throw new RoleFailureException("角色錯誤");
		}
		
		// 3. 檢查是否 重複創建
		if (userPublicRepository.findByUsers(tableUser).isPresent()) {
			throw new UserExistException("帳號已經存在");
		}
		
		// 4. 存入民眾基本資料
		UserPublic userPublic = new UserPublic();
		userPublic.setUsers(tableUser);
		userPublic.setName(userPublicCreateDTO.getName());
		userPublic.setNationalIdNo(userPublicCreateDTO.getNationalIdNo());
		userPublic.setBirthdate(LocalDate.parse(userPublicCreateDTO.getBirthdate()));
		userPublic.setRegisteredAddress(userPublicCreateDTO.getRegisteredAddress());
		userPublic.setMailingAddress(userPublicCreateDTO.getMailingAddress());
		
		userPublic = userPublicRepository.save(userPublic);
		
		// 5. Entity 轉 DTO
		return modelMapper.map(userPublic, UserPublicCreateDTO.class);
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_PUBLIC')")
	public UserPublicUpdateDTO updateUserPublicCheckPassword(UserPublicUpdateDTO userPublicUpdateDTO) {
		// 0. 從 JWT 找尋資料庫 對應的帳號
	    Users tableUser = userSecurityUtil.getCurrentUserEntity();
		
		// 1. 檢查數值完整性
		if (userPublicUpdateDTO.getPassword() == null || userPublicUpdateDTO.getPassword().isBlank()) {
			throw new ValueMissException("缺少密碼");
		}
		
	    // 2. 使用 checkPassword 方法 複查 密碼是否相同
	    if (!userService.checkPassword(userPublicUpdateDTO, tableUser)) {
	    	throw new UserNoFoundException("帳號或密碼錯誤");
	    }
	    
	    // 3. 找到對應的 userPublic
	    UserPublic userPublic = userPublicRepository.findByUsers(tableUser)
	    		.orElseThrow(() -> new UserNoFoundException("帳號或密碼錯誤"));
	    
	    // 4. Entity 轉 DTO
	    userPublicUpdateDTO = modelMapper.map(userPublic, UserPublicUpdateDTO.class);
	    
	    // 5. 返回處理: 清空 password
	    userPublicUpdateDTO.setPassword(null);
	    
		return userPublicUpdateDTO;
	}
	
	@Override
	@PreAuthorize("hasAuthority('ROLE_PUBLIC')")
	public UserPublicUpdateDTO updateUserPublic(UserPublicUpdateDTO userPublicUpdateDTO) {
		// 1. 從 JWT 找到對應的 userPublic
	    UserPublic userPublic = userSecurityUtil.getCurrentUserPublicEntity();
		
		// 2. 更新 民眾基本資料 (僅更新允許修改的欄位：姓名、地址)
		if (userPublicUpdateDTO.getNewName() != null && !userPublicUpdateDTO.getNewName().isBlank() ) {
			userPublic.setName(userPublicUpdateDTO.getNewName());
		}
		if (userPublicUpdateDTO.getNewRegisteredAddress() != null && !userPublicUpdateDTO.getNewRegisteredAddress().isBlank() ) {
			userPublic.setRegisteredAddress(userPublicUpdateDTO.getNewRegisteredAddress());
		}
		if (userPublicUpdateDTO.getNewMailingAddress() != null && !userPublicUpdateDTO.getNewMailingAddress().isBlank() ) {
			userPublic.setMailingAddress(userPublicUpdateDTO.getNewMailingAddress());
		}
		
		// 3. 儲存 修改
		userPublic = userPublicRepository.save(userPublic);
		
		// 4. Entity 轉 DTO 並返回 (回傳新的 DTO 實例)
		userPublicUpdateDTO = modelMapper.map(userPublic, UserPublicUpdateDTO.class);
		
		// 5. 返回處理:
	    userPublicUpdateDTO.setPassword(null);
		
		return userPublicUpdateDTO;
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_PUBLIC')")
	public void deleteUserPublic(UserDeleteDTO userDeleteDTO) {
		// 1. 從 JWT 找尋資料庫 對應的帳號
	    Users tableUser = userSecurityUtil.getCurrentUserEntity();
		
		final LocalDateTime deleteTime = LocalDateTime.now();
		final String deleteSuffix = "_DEL_" + deleteTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
		final String ERROR_MESSAGE = "帳號或密碼錯誤";
		
	    // 2. 使用 checkPassword 方法 複查 密碼是否相同
	    if (!userService.checkPassword(userDeleteDTO, tableUser)) {
	    	throw new UserNoFoundException(ERROR_MESSAGE);
	    }
	    
	    // 3. 找到對應的 userPublic
	    UserPublic userPublic = userPublicRepository.findByUsers(tableUser)
	    		.orElseThrow(() -> new UserNoFoundException(ERROR_MESSAGE));
	    
		// 4. 執行 軟刪除 (核心 Entity)
	    userPublic.setDeleteAt(deleteTime);
	    userPublic.setNationalIdNo(userPublic.getNationalIdNo() + deleteSuffix);
	    
	    tableUser.setDeleteAt(deleteTime);
	    tableUser.setAccount(tableUser.getAccount() + deleteSuffix);
	    tableUser.setEmail(tableUser.getEmail() + deleteSuffix);
	    
		// 5. 關聯欄位
		Set<ChildInfo> children = userPublic.getChildren();
		if (children != null) {
			children.forEach(child -> {
				child.setDeleteAt(deleteTime);
				child.setNationalIdNo(child.getNationalIdNo() + deleteSuffix);
			});
		}
		Set<DocumentPublic> documents = userPublic.getDocuments();
		if (documents != null) {
			documents.forEach(document -> {
				document.setDeleteAt(deleteTime);
			});
		}
		List<UserVerify> userVerifies = tableUser.getUserVerifies();
		if (userVerifies != null) {
			userVerifies.forEach(userVerify -> {
				userVerify.setDeleteAt(deleteTime);
			});
		}
		
		// 6. 回存
		userPublicRepository.save(userPublic);
	}
}
