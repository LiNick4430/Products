package com.github.lianick.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.github.lianick.model.dto.DownloadDTO;
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
	
	/**
	 * 根據儲存路徑下載附件的 I/O 資訊。
	 *
	 * @param pathString 檔案在儲存系統中的路徑（包含 UUID）。
	 * @return 包含 Resource 和 ContentType 的 DTO。
	 * * @implNote 呼叫此方法後，Service 層必須負責從資料庫獲取**原始檔名**，並設置到回傳的 DownloadDTO 中。
	 */
	DownloadDTO download(String pathString);
	
	// 刪除附件
	void deleteByOrganization(Long organizationId, Long documentAdminId);
	void deleteByAnnouncement(Long announcementId, Long documentAdminId);
}
