package com.github.lianick.util.validate;

import org.springframework.stereotype.Service;

import com.github.lianick.exception.ValueMissException;
import com.github.lianick.model.dto.cases.CaseFindPublicDTO;

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
	
}
