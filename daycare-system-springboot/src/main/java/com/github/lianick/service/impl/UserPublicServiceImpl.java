package com.github.lianick.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

@Service
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
	private UserService userService;
	
	@Override
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
	public UserPublicDTO findByUsername(UserPublicDTO userPublicDTO) {
		// 0. 檢查數值完整性
		if (userPublicDTO.getUsername().isBlank() || userPublicDTO.getUsername() == null) {
			throw new ValueMissException("缺少帳號");
		}
		
		// 1. 找尋資料庫 對應的帳號
		Users tableUser = usersRepository.findByAccount(userPublicDTO.getUsername())
		        .orElseThrow(() -> new UserNoFoundException("帳號錯誤"));
		
		UserPublic userPublic = userPublicRepository.findByUsers(tableUser)
				.orElseThrow(() -> new UserNoFoundException("帳號錯誤"));
		
		// 2. Entity 轉 DTO
		userPublicDTO = modelMapper.map(userPublic, UserPublicDTO.class);
		
		// 3. 返回處理
		
		return userPublicDTO;
	}

	@Override
	public UserPublicCreateDTO createUserPublic(UserPublicCreateDTO userPublicCreateDTO) {
		// 0. 檢查數值完整性
		if (userPublicCreateDTO.getUsername() == null || userPublicCreateDTO.getUsername().isBlank() ||
			userPublicCreateDTO.getRoleNumber() == null ||
			userPublicCreateDTO.getName() == null || userPublicCreateDTO.getName().isBlank() || 
			userPublicCreateDTO.getNationalIdNo() == null || userPublicCreateDTO.getNationalIdNo().isBlank() || 
			userPublicCreateDTO.getBirthdate() == null || userPublicCreateDTO.getBirthdate().isBlank() || 
			userPublicCreateDTO.getRegisteredAddress() == null || userPublicCreateDTO.getRegisteredAddress().isBlank() || 
			userPublicCreateDTO.getMailingAddress() == null || userPublicCreateDTO.getMailingAddress().isBlank() ) {
			throw new ValueMissException("缺少特定資料(帳號 角色 民眾姓名 生日 身分證字號 戶籍地址 實際地址)");
		}
		
		// 1. 檢查 角色 是否 為 民眾 和 生日 是否符合 格式
		if (userPublicCreateDTO.getRoleNumber() != 1L) {
			throw new RoleFailureException("角色錯誤");
		}
		// 是否 "yyyy-MM-dd"
		if (!dateValidationUtil.isValidLocalDate(userPublicCreateDTO.getBirthdate())) {
			throw new FormatterFailureException("生日格式錯誤，必須是 yyyy-MM-dd 格式");
		}
		
		// 2. 找尋資料庫 對應的帳號
		Users tableUser = usersRepository.findByAccount(userPublicCreateDTO.getUsername())
		        .orElseThrow(() -> new UserNoFoundException("帳號錯誤"));
		
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
		userPublicCreateDTO = modelMapper.map(userPublic, UserPublicCreateDTO.class);
		
		// 6. 返回處理
		
		return userPublicCreateDTO;
	}

	@Override
	public UserPublicUpdateDTO updateUserPublic(UserPublicUpdateDTO userPublicUpdateDTO) {
		// 0. 檢查數值完整性
		if (userPublicUpdateDTO.getUsername() == null || userPublicUpdateDTO.getUsername().isBlank() ) {
			throw new ValueMissException("缺少帳號資訊");
		}
		
		// 2. 找尋資料庫 對應的帳號
		Users tableUser = usersRepository.findByAccount(userPublicUpdateDTO.getUsername())
		        .orElseThrow(() -> new UserNoFoundException("帳號錯誤"));
		
		// 3. 找到對應的 userPublic
	    UserPublic userPublic = userPublicRepository.findByUsers(tableUser)
	    		.orElseThrow(() -> new UserNoFoundException("帳號錯誤"));
		
		// 4. 存入民眾基本資料
		
		
		// 5. Entity 轉 DTO
		
		
		// 6. 返回處理
		
		return userPublicUpdateDTO;
	}

	@Override
	public void deleteUserPublic(UserDeleteDTO userDeleteDTO) {
		
		final LocalDateTime deleteTime = LocalDateTime.now();
		final String ERROR_MESSAGE = "帳號或密碼錯誤";
		
		// 0. 檢查數值完整性
		if (userDeleteDTO.getUsername() == null || userDeleteDTO.getUsername().isBlank()) {
		    throw new ValueMissException("缺少帳號資訊");
		}
		
		// 1. 找尋資料庫 對應的帳號
	    Users tableUser = usersRepository.findByAccount(userDeleteDTO.getUsername())
	        .orElseThrow(() -> new UserNoFoundException(ERROR_MESSAGE));

	    // 2. 使用 checkPassword 方法 複查 密碼是否相同
	    if (!userService.checkPassword(userDeleteDTO, tableUser)) {
	    	throw new UserNoFoundException(ERROR_MESSAGE);
	    }
	    
	    // 3. 找到對應的 userPublic
	    UserPublic userPublic = userPublicRepository.findByUsers(tableUser)
	    		.orElseThrow(() -> new UserNoFoundException(ERROR_MESSAGE));
	    
		// 4. 執行 軟刪除
	    userPublic.setDeleteAt(deleteTime);
	    tableUser.setDeleteAt(deleteTime);
	    
		// 5. 關聯欄位
		Set<ChildInfo> children = userPublic.getChildren();
		if (children != null) {
			children.forEach(child -> {
				child.setDeleteAt(deleteTime);
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
		
		// 回存
		userPublicRepository.save(userPublic);
	    
		// return new ApiResponse<Void>(true, "民眾帳號刪除成功", null);
	}

}
