package com.github.lianick.util.validate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.lianick.exception.FormatterFailureException;
import com.github.lianick.exception.UserExistException;
import com.github.lianick.exception.ValueMissException;
import com.github.lianick.model.dto.userPublic.UserPublicCreateDTO;
import com.github.lianick.model.dto.userPublic.UserPublicDTO;
import com.github.lianick.model.dto.userPublic.UserPublicUpdateDTO;
import com.github.lianick.repository.UserPublicRepository;

/**
 * 負責處理 UserPublic 相關的 完整性 檢測
 * */
@Service
public class UserPublicValidationUtil {

	@Autowired
	private UserPublicRepository userPublicRepository;

	@Autowired
	private DateValidationUtil dateValidationUtil;

	/**
	 * 檢查 UserPublicDTO 的 完整性
	 * */
	public void validateUserPublic(UserPublicDTO userPublicDTO) {
		if (userPublicDTO.getUsername() == null || userPublicDTO.getUsername().isBlank()) {
			throw new ValueMissException("缺少帳號");
		}
	}

	/**
	 * 檢查 UserPublicCreateDTO 的 完整性 + 格式 + 唯一性
	 * */
	public void validateCreateFields(UserPublicCreateDTO userPublicCreateDTO) {
		if (userPublicCreateDTO.getName() == null || userPublicCreateDTO.getName().isBlank() || 
				userPublicCreateDTO.getNationalIdNo() == null || userPublicCreateDTO.getNationalIdNo().isBlank() || 
				userPublicCreateDTO.getBirthdate() == null || userPublicCreateDTO.getBirthdate().isBlank() || 
				userPublicCreateDTO.getRegisteredAddress() == null || userPublicCreateDTO.getRegisteredAddress().isBlank() || 
				userPublicCreateDTO.getMailingAddress() == null || userPublicCreateDTO.getMailingAddress().isBlank() ) {
			throw new ValueMissException("缺少特定資料(帳號 角色 民眾姓名 生日 身分證字號 戶籍地址 實際地址)");
		}

		// 是否 "yyyy-MM-dd"
		if (!dateValidationUtil.isValidLocalDate(userPublicCreateDTO.getBirthdate())) {
			throw new FormatterFailureException("生日格式錯誤，必須是 yyyy-MM-dd 格式");
		}
		// 檢查 身分證字號 是否 已經被使用
		if (userPublicRepository.findByNationalIdNo(userPublicCreateDTO.getNationalIdNo()).isPresent()) {
			throw new UserExistException("基本資料填寫：身份證字號已經使用");
		}
	}
	
	/**
	 * 檢查 UserPublicUpdateDTO 密碼 的 完整性
	 * */
	public void validateUpdateCheckPassword(UserPublicUpdateDTO userPublicUpdateDTO) {
		if (userPublicUpdateDTO.getPassword() == null || userPublicUpdateDTO.getPassword().isBlank()) {
			throw new ValueMissException("缺少密碼");
		}
	}
}
