package com.github.lianick.util.validate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.lianick.exception.EnumNotFoundException;
import com.github.lianick.exception.RegulationFailureException;
import com.github.lianick.exception.ValueMissException;
import com.github.lianick.model.dto.regulation.RegulationCreateDTO;
import com.github.lianick.model.dto.regulation.RegulationDeleteDTO;
import com.github.lianick.model.dto.regulation.RegulationFindDTO;
import com.github.lianick.model.dto.regulation.RegulationUpdateDTO;
import com.github.lianick.model.eneity.Organization;
import com.github.lianick.model.enums.RegulationType;
import com.github.lianick.repository.RegulationsRepository;

/**
 * 負責處理 Regulation 相關的 完整性 檢測
 * */
@Service
public class RegulationValidationUtil {

	@Autowired
	private RegulationsRepository regulationsRepository;
	
	/**
	 * RegulationFindDTO 的 規範ID 完整性
	 * */
	public void validateFindById(RegulationFindDTO regulationFindDTO) {
		if (regulationFindDTO.getId() == null) {
			throw new ValueMissException("缺少必要資料(規範ID)");

		}
	}

	/**
	 * RegulationFindDTO 的 機構ID 完整性
	 * */
	public void validateFindByOrganization(RegulationFindDTO regulationFindDTO) {
		if (regulationFindDTO.getOrganizationId() == null) {
			throw new ValueMissException("缺少必要資料(機構ID)");
		}
	}
	
	/**
	 * RegulationCreateDTO 的 完整性 和 RegulationType 的 有效性
	 * */
	public RegulationType validateCreateFields(RegulationCreateDTO regulationCreateDTO) {
		if (regulationCreateDTO.getType() == null || regulationCreateDTO.getType().isBlank() ||
				regulationCreateDTO.getContent() == null || regulationCreateDTO.getContent().isBlank() ||
				regulationCreateDTO.getOrganizationId() == null) {
			throw new ValueMissException("缺少必要資料(規範類型, 規範內文, 機構ID)");
		}
		
		try {
			return RegulationType.formCode(regulationCreateDTO.getType());
		} catch (IllegalArgumentException e) {
			throw new EnumNotFoundException("規範類型 錯誤");
		}
	}
	
	/**
	 * RegulationUpdateDTO 的 完整性
	 * */
	public void validateUpdate(RegulationUpdateDTO regulationUpdateDTO) {
		if (regulationUpdateDTO.getId() == null ||
				regulationUpdateDTO.getNewContent() == null || regulationUpdateDTO.getNewContent().isBlank()
				) {
			throw new ValueMissException("缺少必要資料(規範ID, 新內文)");
		}
	}
	
	/**
	 * RegulationDeleteDTO 的 完整性
	 * */
	public void validateDelete(RegulationDeleteDTO regulationDeleteDTO) {
		if (regulationDeleteDTO.getId() == null) {
			throw new ValueMissException("缺少必要資料(規範ID)");
		}
	}
	
	/**
	 * Organization 和 RegulationType 看 是否 已經存在 規範 
	 * */
	public void validateOrganizationAndRegulationType(Organization organization, RegulationType type) {
		if (regulationsRepository.findByOrganizationAndType(organization.getOrganizationId(), type).isPresent()) {
			throw new RegulationFailureException("同類型規範 已經存在");
		}
	}
}
