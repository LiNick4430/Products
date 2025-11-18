package com.github.lianick.service;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.github.lianick.model.eneity.DocumentAdmin;

/**
 * 用於 OrganizationService / AnnouncementService
 * */

public interface DocumentAdminService {

	// 尋找 附件
	List<DocumentAdmin> findByOrganization(Long organizationId);
	List<DocumentAdmin> findByAnnouncement(Long announcementId);
	
	// 上傳附件
	DocumentAdmin uploadByOrganization(Long organizationId, MultipartFile file);
	DocumentAdmin uploadByAnnouncement(Long announcementId, MultipartFile file);
	
	// 下載附件
	Resource downloadFromAnnouncement(String pathString);
	
	// 刪除附件
	void deleteByOrganization(Long organizationId, Long documentAdminId);
	void deleteByAnnouncement(Long announcementId, Long documentAdminId);
	
}
