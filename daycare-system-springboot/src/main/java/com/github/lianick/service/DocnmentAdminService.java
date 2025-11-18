package com.github.lianick.service;

import org.springframework.web.multipart.MultipartFile;

import com.github.lianick.model.eneity.DocumentAdmin;

/**
 * 用於 OrganizationService / AnnouncementService
 * */

public interface DocnmentAdminService {

	// 上傳附件
	DocumentAdmin uploadByOrganization(Long organizationId, MultipartFile file);
	DocumentAdmin uploadByAnnouncement(Long announcementId, MultipartFile file);
	
	// 刪除附件
	
}
