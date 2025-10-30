package com.github.lianick.service;

import java.util.List;

import com.github.lianick.model.dto.organization.OrganizationCreateDTO;
import com.github.lianick.model.dto.organization.OrganizationDTO;
import com.github.lianick.model.dto.organization.OrganizationDeleteDTO;
import com.github.lianick.model.dto.organization.OrganizationFindDTO;
import com.github.lianick.model.dto.organization.OrganizationUpdateDTO;

public interface OrganizationService {

	/** 搜尋 機構 資料 */
	List<OrganizationDTO> findOrganization(OrganizationFindDTO organizationFindDTO);
	
	/** 新增 新 機構<p>
	 * 需要 @PreAuthorize("hasAuthority('ROLE_MANAGER')") 
	 * */
	OrganizationDTO createOrganization(OrganizationCreateDTO organizationCreateDTO);
	
	/** 更新 機構 特定資料<p>
	 * 需要 @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	 * */
	OrganizationDTO updateOrganization(OrganizationUpdateDTO organizationUpdateDTO);
	
	/** 刪除 機構 資料<p>
	 * 需要 @PreAuthorize("hasAuthority('ROLE_MANAGER')") 
	 * */
	void deleteOrganization(OrganizationDeleteDTO organizationDeleteDTO);
	
}
