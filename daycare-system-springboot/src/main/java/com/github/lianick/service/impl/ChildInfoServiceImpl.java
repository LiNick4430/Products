package com.github.lianick.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.lianick.exception.ChildNoFoundException;
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
import com.github.lianick.service.ChildInfoService;
import com.github.lianick.service.UserPublicService;
import com.github.lianick.util.DateValidationUtil;

@Service
@Transactional				// 確保 完整性
public class ChildInfoServiceImpl implements ChildInfoService{

	@Autowired
	private UserPublicRepository userPublicRepository;
	
	@Autowired
	private UserPublicService userPublicService;

	@Autowired
	private ChildInfoRepository childInfoRepository;
	
	@Autowired
	private DateValidationUtil dateValidationUtil;
	
	@Autowired
	private ModelMapper modelMapper;

	@Override
	@PreAuthorize("isAuthenticated()") 
	public List<ChildDTO> findAllChildByUserPublic() {
		// 0. 找尋 UserPublic
		UserPublic userPublic = userPublicService.findUserPublic();
		
		// 1. 找尋所有的 幼兒資料
		List<ChildDTO> childDTOs = childInfoRepository.findByUserPublic(userPublic)
									.stream()
									.map(childInfo -> {
										ChildDTO childDTO = modelMapper.map(childInfo, ChildDTO.class);
										return childDTO;
									}) .toList();

		return childDTOs;
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_PUBLIC')") 
	public ChildDTO findChildByUserPublic(ChildDTO childDTO) {
		// 0. 找尋 UserPublic
		UserPublic userPublic = userPublicService.findUserPublic();
		
		// 1. 檢查資料的完整性
		if (childDTO.getId() == null ) {
			throw new ValueMissException("缺少特定資料(幼兒ID)");
		}
		
		// 2. 檢查幼兒ID 是否存在 / 是否 登錄者(民眾) 的幼兒資料
		ChildInfo childInfo = childInfoRepository.findById(childDTO.getId())
				.orElseThrow(() -> new ChildNoFoundException("幼兒資料不存在"));
		if (!childInfo.getUserPublic().getPublicId().equals(userPublic.getPublicId())) {
			throw new ChildNoFoundException("幼兒資料不存在");
		}
		
		// 3. Entity 轉 DTO
		return modelMapper.map(childInfo, ChildDTO.class);
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_PUBLIC')") 
	public ChildCreateDTO createChildInfo(ChildCreateDTO childCreateDTO) {
		// 0. 找尋 UserPublic 
		UserPublic userPublic = userPublicService.findUserPublic();
		
		// 1. 檢查資料的完整性
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
		
		// 3. 存入幼兒基本資料
		ChildInfo childInfo = new ChildInfo();
		childInfo.setUserPublic(userPublic);
		childInfo.setName(childCreateDTO.getName());
		childInfo.setNationalIdNo(childCreateDTO.getNationalIdNo());
		childInfo.setBirthDate(LocalDate.parse(childCreateDTO.getBirthdate()));
		childInfo.setGender(childCreateDTO.getGender());
		
		childInfo = childInfoRepository.save(childInfo);
		
		// 4. Entity 轉 DTO
		childCreateDTO = modelMapper.map(childInfo, ChildCreateDTO.class);
		
		// 5. 返回處理
		
		return childCreateDTO;
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_PUBLIC')") 
	public ChildUpdateDTO updateChildInfo(ChildUpdateDTO childUpdateDTO) {
		// 0. 找尋 UserPublic
		UserPublic userPublic = userPublicService.findUserPublic();
		
		// 1. 檢查資料的完整性
		if (childUpdateDTO.getNewName() == null || childUpdateDTO.getNewName().isBlank() ||
				childUpdateDTO.getId() == null ) {
			throw new ValueMissException("缺少特定資料(幼兒ID 新姓名)");
		}
		
		// 2. 檢查幼兒ID 是否存在 / 是否 登錄者(民眾) 的幼兒資料
		ChildInfo childInfo = childInfoRepository.findById(childUpdateDTO.getId())
				.orElseThrow(() -> new ChildNoFoundException("幼兒資料不存在"));
		if (!childInfo.getUserPublic().getPublicId().equals(userPublic.getPublicId())) {
			throw new ChildNoFoundException("幼兒資料不存在");
		}
		
		// 3. 更新存入 幼兒資料
		childInfo.setName(childUpdateDTO.getNewName());
		childInfo = childInfoRepository.save(childInfo);
		
		// 4. Entity 轉 DTO
		ChildUpdateDTO newChildUpdateDTO = modelMapper.map(childInfo, ChildUpdateDTO.class);
		
		// 5. 返回處理
		
		return newChildUpdateDTO;
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_PUBLIC')") 
	public void deleteChildInfo(ChildDeleteDTO childDeleteDTO) {
		// 0. 找尋 UserPublic
		UserPublic userPublic = userPublicService.findUserPublic();

		// 1. 檢查資料的完整性
		if (childDeleteDTO.getId() == null ) {
			throw new ValueMissException("缺少特定資料(幼兒ID)");
		}
		
		// 2. 檢查幼兒ID 是否存在 / 是否 登錄者(民眾) 的幼兒資料
		ChildInfo childInfo = childInfoRepository.findById(childDeleteDTO.getId())
				.orElseThrow(() -> new ChildNoFoundException("幼兒資料不存在"));
		if (!childInfo.getUserPublic().getPublicId().equals(userPublic.getPublicId())) {
			throw new ChildNoFoundException("幼兒資料不存在");
		}
		
		// TODO 假設在 CASE CLASS 資料有他 可能就不給刪除
		
		// 3. 建立 變數
	    LocalDateTime deleteTime = LocalDateTime.now();
	    String deleteSuffix = "_DEL_" + deleteTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
		
		// 4. 執行 軟刪除
		childInfo.setDeleteAt(deleteTime);
		childInfo.setNationalIdNo(childInfo.getNationalIdNo() + deleteSuffix);
		
		childInfoRepository.save(childInfo);
	}
}
