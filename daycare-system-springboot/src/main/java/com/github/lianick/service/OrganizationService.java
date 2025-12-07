package com.github.lianick.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.github.lianick.model.dto.DownloadDTO;
import com.github.lianick.model.dto.organization.OrganizationCreateDTO;
import com.github.lianick.model.dto.organization.OrganizationDTO;
import com.github.lianick.model.dto.organization.OrganizationDeleteDTO;
import com.github.lianick.model.dto.organization.OrganizationFindDTO;
import com.github.lianick.model.dto.organization.OrganizationUpdateDTO;
import com.github.lianick.model.dto.organization.OrganizationDocumentDTO;

public interface OrganizationService {

	/** 搜尋 全部 機構 資料 */
	List<OrganizationDTO> findAllOrganization();
	
	/** 關鍵字 搜尋 機構 資料 */
	List<OrganizationDTO> findOrganization(OrganizationFindDTO organizationFindDTO);
	
	/**
	 * 下載 特定 機構中 特定附件 的 檔案<p>
	 * 需要 @PreAuthorize("isAuthenticated()")
	 * */
	DownloadDTO downloadDocument(OrganizationDocumentDTO organizationDocumentDTO);
	
	/** 新增 新 機構<p>
	 * 需要 @PreAuthorize("hasAuthority('ROLE_MANAGER')") 
	 * */
	OrganizationDTO createOrganization(OrganizationCreateDTO organizationCreateDTO);
	
	/** 上傳 機構用 的 附件<p>
	 * 需要 @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')") 
	 * */
	OrganizationDTO uploadOrganization(OrganizationDocumentDTO organizationDocumentDTO, MultipartFile file);
	
	/** 更新 機構 特定資料<p>
	 * 需要 @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	 * */
	OrganizationDTO updateOrganization(OrganizationUpdateDTO organizationUpdateDTO);
	
	/** 刪除 機構用 的 附件<p>
	 * 需要 @PreAuthorize("hasAuthority('ROLE_MANAGER')") 
	 * */
	void deleteOrganizationDocument(OrganizationDocumentDTO organizationDocumentDTO);
	
	/** 刪除 機構 資料<p>
	 * 需要 @PreAuthorize("hasAuthority('ROLE_MANAGER')") 
	 * */
	void deleteOrganization(OrganizationDeleteDTO organizationDeleteDTO);
	
}
