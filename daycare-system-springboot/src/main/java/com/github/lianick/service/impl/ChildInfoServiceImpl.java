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

import com.github.lianick.exception.CaseFailureException;
import com.github.lianick.model.dto.child.ChildCreateDTO;
import com.github.lianick.model.dto.child.ChildDTO;
import com.github.lianick.model.dto.child.ChildDeleteDTO;
import com.github.lianick.model.dto.child.ChildUpdateDTO;
import com.github.lianick.model.eneity.ChildInfo;
import com.github.lianick.model.eneity.UserPublic;
import com.github.lianick.repository.ChildInfoRepository;
import com.github.lianick.service.ChildInfoService;
import com.github.lianick.util.UserSecurityUtil;
import com.github.lianick.util.validate.ChildValidationUtil;

@Service
@Transactional				// 確保 完整性
public class ChildInfoServiceImpl implements ChildInfoService{

	@Autowired
	private ChildInfoRepository childInfoRepository;
	
	@Autowired
	private UserSecurityUtil userSecurityUtil;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private EntityFetcher entityFetcher;
	
	@Autowired
	private ChildValidationUtil childValidationUtil;

	@Override
	@PreAuthorize("isAuthenticated()") 
	public List<ChildDTO> findAllChildByUserPublic() {
		// 0. 找尋 UserPublic
		UserPublic userPublic = userSecurityUtil.getCurrentUserPublicEntity();
		
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
		UserPublic userPublic = userSecurityUtil.getCurrentUserPublicEntity();
		
		// 1. 檢查資料的完整性
		childValidationUtil.validateChild(childDTO);
		
		// 2. 檢查幼兒ID 是否存在 / 是否 登錄者(民眾) 的幼兒資料
		ChildInfo childInfo = entityFetcher.getChildInfoById(childDTO.getId());
		childValidationUtil.validateChildAndUserPublic(childInfo, userPublic);
		
		// 3. Entity 轉 DTO
		return modelMapper.map(childInfo, ChildDTO.class);
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_PUBLIC')") 
	public ChildDTO createChildInfo(ChildCreateDTO childCreateDTO) {
		// 0. 找尋 UserPublic 
		UserPublic userPublic = userSecurityUtil.getCurrentUserPublicEntity();
		
		// 1. 檢查資料的完整性 + 檢查 生日 是否符合 格式 + 身分證字號 是否 已經被使用
		childValidationUtil.validateCeateFields(childCreateDTO);
		
		// 2. 存入幼兒基本資料
		ChildInfo childInfo = new ChildInfo();
		childInfo.setUserPublic(userPublic);
		childInfo.setName(childCreateDTO.getName());
		childInfo.setNationalIdNo(childCreateDTO.getNationalIdNo());
		childInfo.setBirthDate(LocalDate.parse(childCreateDTO.getBirthdate()));
		childInfo.setGender(childCreateDTO.getGender());
		
		childInfo = childInfoRepository.save(childInfo);
		
		// 3. Entity 轉 DTO
		ChildDTO childDTO = modelMapper.map(childInfo, ChildDTO.class);
		
		return childDTO;
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_PUBLIC')") 
	public ChildDTO updateChildInfo(ChildUpdateDTO childUpdateDTO) {
		// 0. 找尋 UserPublic
		UserPublic userPublic = userSecurityUtil.getCurrentUserPublicEntity();
		
		// 1. 檢查資料的完整性
		childValidationUtil.validateUpdate(childUpdateDTO);
		
		// 2. 檢查幼兒ID 是否存在 / 是否 登錄者(民眾) 的幼兒資料
		ChildInfo childInfo = entityFetcher.getChildInfoById(childUpdateDTO.getId());
		childValidationUtil.validateChildAndUserPublic(childInfo, userPublic);
		
		// 3. 更新存入 幼兒資料
		childInfo.setName(childUpdateDTO.getNewName());
		childInfo = childInfoRepository.save(childInfo);
		
		// 4. Entity 轉 DTO
		ChildDTO childDTO = modelMapper.map(childInfo, ChildDTO.class);
		
		return childDTO;
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_PUBLIC')") 
	public void deleteChildInfo(ChildDeleteDTO childDeleteDTO) {
		// 0. 找尋 UserPublic
		UserPublic userPublic = userSecurityUtil.getCurrentUserPublicEntity();

		// 1. 檢查資料的完整性
		childValidationUtil.validateDelete(childDeleteDTO);
		
		// 2. 檢查幼兒ID 是否存在 / 是否 登錄者(民眾) 的幼兒資料
		ChildInfo childInfo = entityFetcher.getChildInfoById(childDeleteDTO.getId());
		childValidationUtil.validateChildAndUserPublic(childInfo, userPublic);
		
		// TODO 假設在 CASE CLASS 資料有他 可能就不給刪除
		if (!childInfo.getCases().isEmpty()) {
			throw new CaseFailureException("關聯案件存在中 無法刪除");
		}
		
		// 3. 建立 變數
	    LocalDateTime deleteTime = LocalDateTime.now();
	    String deleteSuffix = "_DEL_" + deleteTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
		
		// 4. 執行 軟刪除
		childInfo.setDeleteAt(deleteTime);
		childInfo.setNationalIdNo(childInfo.getNationalIdNo() + deleteSuffix);
		
		childInfoRepository.save(childInfo);
	}
}
