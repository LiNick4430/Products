package com.github.lianick.util.validate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.lianick.exception.ChildNotFoundException;
import com.github.lianick.exception.FormatterFailureException;
import com.github.lianick.exception.UserExistException;
import com.github.lianick.exception.ValueMissException;
import com.github.lianick.model.dto.child.ChildCreateDTO;
import com.github.lianick.model.dto.child.ChildDTO;
import com.github.lianick.model.dto.child.ChildDeleteDTO;
import com.github.lianick.model.dto.child.ChildUpdateDTO;
import com.github.lianick.model.eneity.ChildInfo;
import com.github.lianick.model.eneity.UserPublic;
import com.github.lianick.repository.ChildInfoRepository;
import com.github.lianick.repository.UserPublicRepository;

/**
 * 負責處理 Child 相關的 完整性 檢測
 * */
@Service
public class ChildValidationUtil {

	@Autowired
	private UserPublicRepository userPublicRepository;
	
	@Autowired
	private ChildInfoRepository childInfoRepository;
	
	@Autowired
	private DateValidationUtil dateValidationUtil;
	
	/**
	 * 檢查 ChildDTO 的 完整性 
	 * */
	public void validateChild(ChildDTO childDTO) {
		if (childDTO.getId() == null ) {
			throw new ValueMissException("缺少特定資料(幼兒ID)");
		}
	}
	
	/**
	 * 檢查 ChildInfo 和 UserPublic 的關係
	 * */
	public void validateChildAndUserPublic(ChildInfo childInfo, UserPublic userPublic) {
		if (!childInfo.getUserPublic().getPublicId().equals(userPublic.getPublicId())) {
			throw new ChildNotFoundException("查無幼兒資料");
		}
	}
	
	/**
	 * 檢查 ChildCreateDTO 的 完整性 + 格式 + 唯一
	 * */
	public void validateCeateFields(ChildCreateDTO childCreateDTO) {
		if (childCreateDTO.getName() == null || childCreateDTO.getName().isBlank() || 
				childCreateDTO.getNationalIdNo() == null || childCreateDTO.getNationalIdNo().isBlank() || 
				childCreateDTO.getBirthdate() == null || childCreateDTO.getBirthdate().isBlank() || 
				childCreateDTO.getGender() == null || childCreateDTO.getGender().isBlank()) {
			throw new ValueMissException("缺少特定資料(幼兒姓名 生日 身分證字號 性別)");
		}
		
		// 2. 檢查 生日 是否符合 格式 / 身分證字號 是否 已經被使用
		// 是否 "yyyy-MM-dd"
		if (!dateValidationUtil.isValidLocalDate(childCreateDTO.getBirthdate())) {
			throw new FormatterFailureException("生日格式錯誤，必須是 yyyy-MM-dd 格式");
		}
		// 檢查 身分證字號 是否 已被註冊為主要用戶 (家長或其他)
		if (userPublicRepository.findByNationalIdNo(childCreateDTO.getNationalIdNo()).isPresent()) {
		    throw new UserExistException("身份證字號已經被主要用戶使用");
		}
		// 檢查 身分證字號 是否 已經被註冊為另一個幼兒
		if (childInfoRepository.findByNationalIdNo(childCreateDTO.getNationalIdNo()).isPresent()) {
		    throw new UserExistException("該身分證字號已存在於幼兒資料中");
		}
	}
	
	/**
	 * 檢查 ChildUpdateDTO 的 完整性 
	 * */
	public void validateUpdate(ChildUpdateDTO childUpdateDTO) {
		if (childUpdateDTO.getNewName() == null || childUpdateDTO.getNewName().isBlank() ||
				childUpdateDTO.getId() == null ) {
			throw new ValueMissException("缺少特定資料(幼兒ID 新姓名)");
		}
	}
	
	/**
	 * 檢查 ChildDeleteDTO 的 完整性 
	 * */
	public void validateDelete(ChildDeleteDTO childDeleteDTO) {
		if (childDeleteDTO.getId() == null ) {
			throw new ValueMissException("缺少特定資料(幼兒ID)");
		}
	}
}
