package com.github.lianick.service;

import java.util.List;

import com.github.lianick.model.dto.regulation.RegulationCreateDTO;
import com.github.lianick.model.dto.regulation.RegulationDTO;
import com.github.lianick.model.dto.regulation.RegulationDeleteDTO;
import com.github.lianick.model.dto.regulation.RegulationFindDTO;
import com.github.lianick.model.dto.regulation.RegulationUpdateDTO;

// 規範
public interface RegulationService {

	/** 搜尋 機構內 全部規範 */
	List<RegulationDTO> findAll(RegulationFindDTO regulationFindDTO);
	
	/** 搜尋 機構內 特定規範 */
	RegulationDTO findByRegulationId(RegulationFindDTO regulationFindDTO);
	
	/** 建立 規範
	 * @PreAuthorize("hasAuthority('ROLE_MANAGER')")
	 * */
	RegulationDTO createRegulation(RegulationCreateDTO regulationCreateDTO);
	
	/** 更新 規範 
	 * @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	 * */
	RegulationDTO updateRegulation(RegulationUpdateDTO regulationUpdateDTO);
	
	/** 刪除 規範 
	 * @PreAuthorize("hasAuthority('ROLE_MANAGER')")
	 * */
	void deleteRegulation(RegulationDeleteDTO regulationDeleteDTO);
}
