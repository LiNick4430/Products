package com.github.lianick.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.github.lianick.exception.AnnouncementFailureException;
import com.github.lianick.exception.FileStorageException;
import com.github.lianick.exception.OrganizationFailureException;
import com.github.lianick.exception.ValueMissException;
import com.github.lianick.model.dto.DocumentDTO;
import com.github.lianick.model.dto.DownloadDTO;
import com.github.lianick.model.eneity.Announcements;
import com.github.lianick.model.eneity.DocumentAdmin;
import com.github.lianick.model.eneity.Organization;
import com.github.lianick.model.enums.DocumentType;
import com.github.lianick.model.enums.EntityType;
import com.github.lianick.repository.AnnouncementsRepository;
import com.github.lianick.repository.DocumentAdminRepository;
import com.github.lianick.repository.OrganizationRepository;
import com.github.lianick.service.DocumentAdminService;
import com.github.lianick.util.DocumentInfo;
import com.github.lianick.util.DocumentUtil;

/**
 * 用於 OrganizationService / AnnouncementService
 * */
@Service
@Transactional				// 確保 完整性 
public class DocumentAdminServiceImpl implements DocumentAdminService{

	@Autowired
	private DocumentUtil documentUtil;
	
	@Autowired
	private DocumentAdminRepository documentAdminRepository;
	
	@Autowired
	private OrganizationRepository organizationRepository;
	
	@Autowired
	private AnnouncementsRepository announcementsRepository;
	
	@Autowired
	private EntityFetcher entityFetcher;
	
	@Override
	public List<DocumentAdmin> findByOrganization(Long organizationId) {
		// 1. 檢查 是否存在
		if (organizationId == null) {
			throw new ValueMissException("缺少特定資料(機構ID)");
		}
		Organization organization = organizationRepository.findById(organizationId)
				.orElseThrow(() -> new OrganizationFailureException("機構找不到"));
		
		// 2. 找尋文件
		return documentAdminRepository.findByOrganization(organization);
	}

	@Override
	public List<DocumentAdmin> findByAnnouncement(Long announcementId) {
		// 1. 檢查 是否存在
		if (announcementId == null) {
			throw new ValueMissException("缺少特定資料(公告ID)");
		}
		Announcements announcements = announcementsRepository.findById(announcementId)
				.orElseThrow(() -> new AnnouncementFailureException("公告找不到"));
				
		// 2. 找尋文件
		return documentAdminRepository.findByAnnouncements(announcements);
	}
	
	@Override
	public DocumentAdmin uploadByOrganization(Long organizationId, MultipartFile file) {
		// 1. 檢查 是否存在
		if (file.isEmpty()) {
			throw new FileStorageException("檔案錯誤：上傳檔案不存在");
		}
		if (organizationId == null) {
			throw new ValueMissException("缺少特定資料(機構ID)");
		}
		Organization organization = organizationRepository.findById(organizationId)
				.orElseThrow(() -> new OrganizationFailureException("機構找不到"));
		
		// 2. I/O 處理
		DocumentDTO documentDTO = documentUtil.upload(organization.getOrganizationId(), EntityType.ORGANIZATION, file, true);
		
		// 3. 資料庫處理
		DocumentAdmin documentAdmin = new DocumentAdmin();
		documentAdmin.setFileName(documentDTO.getOrignalFileName());
		documentAdmin.setStoragePath(documentDTO.getTargetLocation());
		documentAdmin.setOrganization(organization);
		documentAdmin.setDocType(DocumentType.ORGANIZATION);
		
		// documentAdmin = documentAdminRepository.save(documentAdmin);
		
		return documentAdmin;
	}

	@Override
	public DocumentAdmin uploadByAnnouncement(Long announcementId, MultipartFile file) {
		// 1. 檢查 是否存在
		if (file.isEmpty()) {
			throw new FileStorageException("檔案錯誤：上傳檔案不存在");
		}
		if (announcementId == null) {
			throw new ValueMissException("缺少特定資料(公告ID)");
		}
		Announcements announcements = entityFetcher.getAnnouncementsById(announcementId);
		
		// 2. I/O 處理
		DocumentDTO documentDTO = documentUtil.upload(announcements.getAnnouncementId(), EntityType.ANNOUNCEMENT, file, true);
		
		// 3. 資料庫處理
		DocumentAdmin documentAdmin = new DocumentAdmin();
		documentAdmin.setFileName(documentDTO.getOrignalFileName());
		documentAdmin.setStoragePath(documentDTO.getTargetLocation());
		documentAdmin.setAnnouncements(announcements);
		documentAdmin.setDocType(DocumentType.ANNOUNCEMENT);

		// documentAdmin = documentAdminRepository.save(documentAdmin);

		return documentAdmin;
	}

	@Override
	public DownloadDTO download(String pathString) {
		// 1. 檢查 是否存在
		if (pathString == null || pathString.isBlank()) {
			throw new FileStorageException("檔案路徑無效");
		}
		
		// 2. I/O 處理
		DocumentInfo documentInfo = documentUtil.download(pathString);
		
		// 3. 轉換成 DTO
		DownloadDTO downloadDTO = new DownloadDTO();
		downloadDTO.setResource(documentInfo.getResource());
		downloadDTO.setContentType(documentInfo.getContentType());
		downloadDTO.setContentLength(documentInfo.getContentLength());
		
		return downloadDTO;
	}

	@Override
	public void deleteByOrganization(Long organizationId, Long documentAdminId) {
		// 1. 檢查 是否存在
		if (organizationId == null || documentAdminId == null) {
			throw new ValueMissException("缺少特定資料(機構ID, 附件ID)");
		}
		
		entityFetcher.getOrganizationById(documentAdminId);
		DocumentAdmin documentAdmin = entityFetcher.getDocumentAdminByIdAndOrganizationId(documentAdminId, organizationId);
		
		// 2. I/O
		documentUtil.delete(documentAdmin.getStoragePath());
		
		// 3. 軟刪除
		LocalDateTime now = LocalDateTime.now();
		documentAdmin.setDeleteAt(now);
		documentAdmin = documentAdminRepository.save(documentAdmin);
	}

	@Override
	public void deleteByAnnouncement(Long announcementId, Long documentAdminId) {
		// 1. 檢查 是否存在
		if (announcementId == null || documentAdminId == null) {
			throw new ValueMissException("缺少特定資料(公告ID, 附件ID)");
		}
		
		entityFetcher.getAnnouncementsById(documentAdminId);
		DocumentAdmin documentAdmin = entityFetcher.getDocumentAdminByIdAndAnnouncementId(documentAdminId, announcementId);
		
		// 2. I/O
		documentUtil.delete(documentAdmin.getStoragePath());
		
		// 3. 軟刪除
		LocalDateTime now = LocalDateTime.now();
		documentAdmin.setDeleteAt(now);
		documentAdmin = documentAdminRepository.save(documentAdmin);
	}
	
}
