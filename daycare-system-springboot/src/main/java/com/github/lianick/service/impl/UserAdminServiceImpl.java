package com.github.lianick.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.lianick.exception.UserExistException;
import com.github.lianick.exception.UserNoFoundException;
import com.github.lianick.exception.ValueMissException;
import com.github.lianick.model.dto.user.UserDeleteDTO;
import com.github.lianick.model.dto.userAdmin.UserAdminCreateDTO;
import com.github.lianick.model.dto.userAdmin.UserAdminDTO;
import com.github.lianick.model.dto.userAdmin.UserAdminUpdateDTO;
import com.github.lianick.model.eneity.UserAdmin;
import com.github.lianick.model.eneity.Users;
import com.github.lianick.repository.OrganizationRepository;
import com.github.lianick.repository.UserAdminRepository;
import com.github.lianick.repository.UsersRepository;
import com.github.lianick.service.UserAdminService;
import com.github.lianick.util.SecurityUtils;

import jakarta.transaction.Transactional;

@Service
@Transactional				// 確保 完整性 
public class UserAdminServiceImpl implements UserAdminService{

	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private UserAdminRepository userAdminRepository;
	
	@Autowired
	private OrganizationRepository organizationRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Override
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
	public UserAdminDTO findByUsername(UserAdminDTO userAdminDTO) {
		Users tableUser = usersRepository.findByAccount(userAdminDTO.getUsername())
				.orElseThrow(() -> new UserNoFoundException("基礎帳號不存在"));
		UserAdmin userAdmin = userAdminRepository.findByUsers(tableUser)
				.orElseThrow(() -> new UserNoFoundException("用戶存在，但非員工帳號"));

		userAdminDTO = modelMapper.map(userAdmin, UserAdminDTO.class);
		return userAdminDTO;
	}

	@Override
	public UserAdminCreateDTO createUserAdmin(UserAdminCreateDTO userAdminCreateDTO) {
		// 0. 檢查數值完整性
		if(userAdminCreateDTO.getUsername() == null || userAdminCreateDTO.getUsername().isBlank() ||
				userAdminCreateDTO.getPassword() == null || userAdminCreateDTO.getPassword().isBlank() ||
				userAdminCreateDTO.getEmail() == null || userAdminCreateDTO.getEmail().isBlank() ||
				userAdminCreateDTO.getPhoneNumber() == null || userAdminCreateDTO.getPhoneNumber().isBlank()) {
			throw new ValueMissException("缺少必要的註冊資料 (帳號、密碼、信箱、電話號碼)");
		}
		if(userAdminCreateDTO.getName() == null || userAdminCreateDTO.getName().isBlank() || 
				userAdminCreateDTO.getJobTitle() == null || userAdminCreateDTO.getJobTitle().isBlank() ||
				userAdminCreateDTO.getOrganizationId() == null) {
			throw new ValueMissException("缺少必要的註冊資料 (員工姓名 員工職稱 機構ID)");
		}
		
		
		return null;
	}

	@Override
	public UserAdminUpdateDTO updateUserAdmin(UserAdminUpdateDTO userAdminUpdateDTO) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteUserAdmin(UserDeleteDTO userDeleteDTO) {
		// 1. 檢查 是否 自己帳號
		String currentUsername = SecurityUtils.getCurrentUsername();
		if (currentUsername.equals(userDeleteDTO.getUsername())) {
			throw new UserExistException("刪除失敗 無法刪除自己");
		}
		
		// 2. 查詢帳號是否存在
		Users tableUser = usersRepository.findByAccount(userDeleteDTO.getUsername())
				.orElseThrow(() -> new UserNoFoundException("基礎帳號不存在"));
		UserAdmin userAdmin = userAdminRepository.findByUsers(tableUser)
				.orElseThrow(() -> new UserNoFoundException("用戶存在，但非員工帳號"));
		
		// 3. 進行軟刪除 並且 回存
		LocalDateTime now = LocalDateTime.now();
		userAdmin.setDeleteAt(now);
		tableUser.setDeleteAt(now);
		
		userAdminRepository.save(userAdmin);
		usersRepository.save(tableUser);
	}

}
