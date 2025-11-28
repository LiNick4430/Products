package com.github.lianick.util.validate;

import org.springframework.stereotype.Service;

import com.github.lianick.exception.CaseFailureException;
import com.github.lianick.exception.ValueMissException;
import com.github.lianick.model.dto.cases.CaseCreateDTO;
import com.github.lianick.model.dto.cases.CaseFindPublicDTO;
import com.github.lianick.model.dto.cases.CaseWithdrawnDTO;
import com.github.lianick.model.eneity.Cases;
import com.github.lianick.model.eneity.ChildInfo;
import com.github.lianick.model.eneity.UserPublic;
import com.github.lianick.model.enums.ApplicationMethod;

/**
 * 負責處理 Case 相關的 完整性 檢測
 * */
@Service
public class CaseValidationUtil {

	/**
	 * CaseFindPublicDTO 的完整性
	 * */
	public void validateFindPublic(CaseFindPublicDTO caseFindPublicDTO) {
		if (caseFindPublicDTO.getId() == null) {
			throw new ValueMissException("缺少特定資料(案件ID)");
		}
	}
	
	/**
	 * 判斷 UserPublic 是否有權限 觀看這個案件
	 * */
	public void validatePublicAndCase(UserPublic userPublic, Cases cases) {
		if (userPublic == null || cases == null) {
			throw new ValueMissException("缺少必要資訊(使用者, 案件)");
		}
		if (!cases.getChildInfo().getUserPublic().equals(userPublic)) {
			throw new CaseFailureException("您沒有權限查找這個案件");
		}
	}
	
	/**
	 * CaseCreateDTO 的 完整性 + 並取出 AapplicationMethod
	 * */
	public ApplicationMethod validateCreateFields(CaseCreateDTO caseCreateDTO) {
		if (caseCreateDTO.getApplicationMethod() == null || caseCreateDTO.getApplicationMethod().isBlank() ||
				caseCreateDTO.getChildId() == null || caseCreateDTO.getOrganizationIdFirst() == null
				) {
			throw new ValueMissException("缺少必要資訊(申請方式, 幼兒ID, 第一志願機構ID)");
		}
		
		try {
			return ApplicationMethod.formCode(caseCreateDTO.getApplicationMethod());
		} catch (IllegalArgumentException e) {
			throw new CaseFailureException("申請方式 類型錯誤");
		}
	}
	
	/**
	 * 判斷 UserPublic ChildInfo 是否相關聯
	 * */
	public void validatePublicAndChildInfo(UserPublic userPublic, ChildInfo childInfo) {
		if (userPublic == null || childInfo == null) {
			throw new ValueMissException("缺少必要資訊(使用者, 幼兒)");
		}
		if (!childInfo.getUserPublic().equals(userPublic)) {
			throw new CaseFailureException("您沒有權限建立此案件");
		}
	}
	
	/**
	 * CaseWithdrawnDTO 的完整性
	 * */
	public void validateWithdrawnCase(CaseWithdrawnDTO caseWithdrawnDTO) {
		if (caseWithdrawnDTO.getId() == null || 
				caseWithdrawnDTO.getReason() == null || caseWithdrawnDTO.getReason().isBlank()
				) {
			
			throw new ValueMissException("缺少特定資料(案件ID, 撤銷原因)");
		}
	}
}
