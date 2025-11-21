package com.github.lianick.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.github.lianick.exception.AnnouncementFailureException;
import com.github.lianick.model.dto.DownloadDTO;
import com.github.lianick.model.dto.announcement.AnnouncementCreateDTO;
import com.github.lianick.model.dto.announcement.AnnouncementDTO;
import com.github.lianick.model.dto.announcement.AnnouncementDeleteDTO;
import com.github.lianick.model.dto.announcement.AnnouncementDocumnetDTO;
import com.github.lianick.model.dto.announcement.AnnouncementFindDTO;
import com.github.lianick.model.dto.announcement.AnnouncementPublishDTO;
import com.github.lianick.model.dto.announcement.AnnouncementUpdateDTO;
import com.github.lianick.model.eneity.Announcements;
import com.github.lianick.model.eneity.DocumentAdmin;
import com.github.lianick.model.eneity.Organization;
import com.github.lianick.model.eneity.Users;
import com.github.lianick.repository.AnnouncementsRepository;
import com.github.lianick.repository.OrganizationRepository;
import com.github.lianick.service.AnnouncementService;
import com.github.lianick.service.DocumentAdminService;
import com.github.lianick.util.UserSecurityUtil;
import com.github.lianick.util.validate.AnnouncementValidationUtil;
import com.github.lianick.util.validate.OrganizationValidationUtil;

@Service
@Transactional				// 確保 完整性 
public class AnnouncementServiceImpl implements AnnouncementService{

	@Autowired
	private AnnouncementsRepository announcementsRepository;
	
	@Autowired
	private OrganizationRepository organizationRepository;
	
	@Autowired
	private DocumentAdminService documentAdminService;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private EntityFetcher entityFetcher;
	
	@Autowired
	private UserSecurityUtil userSecurityUtil;
	
	@Autowired
	private AnnouncementValidationUtil announcementValidationUtil;
	
	@Autowired
	private OrganizationValidationUtil organizationValidationUtil;
	
	@Override
	public List<AnnouncementDTO> findAllAnnouncement() {
		// 1. 建立 現在變數
		LocalDateTime now = LocalDateTime.now();
		// 2. 找尋 符合的公告
		List<Announcements> announcements = announcementsRepository.findAll(now);
		// 3. 轉成 DTO
		return announcements.stream()
					.map(announcement -> {
						AnnouncementDTO announcementDTO = modelMapper.map(announcement, AnnouncementDTO.class);
						return announcementDTO;
					}).toList();
	}

	@Override
	public AnnouncementDTO findAnnouncementById(AnnouncementFindDTO announcementFindDTO) {
		// 0. 檢查完整性
		announcementValidationUtil.validateFind(announcementFindDTO);
		// 1. 建立 現在變數
		LocalDateTime now = LocalDateTime.now();
		// 2. 找尋 符合的公告
		Announcements announcements = entityFetcher.getAnnouncementsByIdAndNow(announcementFindDTO.getId(), now);
		// 3. 轉成 DTO
		return modelMapper.map(announcements, AnnouncementDTO.class);
	}

	@Override
	public DownloadDTO downloadDoc(AnnouncementDocumnetDTO announcementDocumnetDTO) {
		// 0. 檢查 完整性
		announcementValidationUtil.validateDocument(announcementDocumnetDTO, null, false);
		Announcements announcements = entityFetcher.getAnnouncementsById(announcementDocumnetDTO.getId());
		DocumentAdmin documentAdmin = entityFetcher.getDocumentAdminByIdAndAnnouncementId(announcementDocumnetDTO.getDocumentId(), announcements.getAnnouncementId());
		
		// 1. I/O
		DownloadDTO downloadDTO = documentAdminService.download(documentAdmin.getStoragePath());
		downloadDTO.setName(documentAdmin.getFileName());
		
		return downloadDTO;
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	public AnnouncementDTO createAnnouncement(AnnouncementCreateDTO announcementCreateDTO) {
		// 0. 檢查 完整性
		announcementValidationUtil.validateCreateFields(announcementCreateDTO);
		
		// 1. 檢查是否權限
		Users tableUser = userSecurityUtil.getCurrentUserEntity();
		Organization organization = entityFetcher.getOrganizationById(announcementCreateDTO.getOrganizationId());
		organizationValidationUtil.validateOrganizationPermission(tableUser, organization.getOrganizationId());
		
		// 2. 建立 公告 並 儲存
		Announcements announcements = new Announcements();
		announcements.setTitle(announcementCreateDTO.getTitle());
		announcements.setContent(announcementCreateDTO.getContent());
		announcements.setOrganization(organization);
		announcements = announcementsRepository.save(announcements);
		
		organization.getAnnouncements().add(announcements);
		organizationRepository.save(organization);
		
		// 3. 轉成 DTO
		return modelMapper.map(announcements, AnnouncementDTO.class);
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	public AnnouncementDTO updateAnnouncement(AnnouncementUpdateDTO announcementUpdateDTO) {
		// 0. 檢查 完整性
		announcementValidationUtil.validateUpdate(announcementUpdateDTO);
		Announcements announcements = entityFetcher.getAnnouncementsById(announcementUpdateDTO.getId());
		
		// 1. 檢查是否權限
		Users tableUser = userSecurityUtil.getCurrentUserEntity();
		organizationValidationUtil.validateOrganizationPermission(tableUser, announcements.getOrganization().getOrganizationId());
				
		// 2. 更新 並回存
		if (announcementUpdateDTO.getNewTitle() != null) {
			announcements.setTitle(announcementUpdateDTO.getNewTitle());
		}
		if (announcementUpdateDTO.getNewContent() != null) {
			announcements.setContent(announcementUpdateDTO.getNewContent());
		}
		announcements = announcementsRepository.save(announcements);
		
		// 3. 轉成 DTO
		return modelMapper.map(announcements, AnnouncementDTO.class);
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	public AnnouncementDTO uploadAnnouncementDocument(AnnouncementDocumnetDTO announcementDocumnetDTO, MultipartFile file) {
		// 0. 檢查 完整性
		announcementValidationUtil.validateDocument(announcementDocumnetDTO, file, true);
		Announcements announcements = entityFetcher.getAnnouncementsById(announcementDocumnetDTO.getId());
		
		// 1. 檢查是否權限
		Users tableUser = userSecurityUtil.getCurrentUserEntity();
		organizationValidationUtil.validateOrganizationPermission(tableUser, announcements.getOrganization().getOrganizationId());
		
		// 2. I/O
		DocumentAdmin documentAdmin = documentAdminService.uploadByAnnouncement(announcements.getAnnouncementId(), file);
		
		// 3. 儲存
		announcements.getDocuments().add(documentAdmin);
		announcements = announcementsRepository.save(announcements);
		
		// 4. 轉成 DTO
		return modelMapper.map(announcements, AnnouncementDTO.class);
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	public void deleteAnnouncementDocument(AnnouncementDocumnetDTO announcementDocumnetDTO) {
		// 0. 檢查 完整性
		announcementValidationUtil.validateDocument(announcementDocumnetDTO, null, false);
		Announcements announcements = entityFetcher.getAnnouncementsById(announcementDocumnetDTO.getId());
		DocumentAdmin documentAdmin = entityFetcher.getDocumentAdminByIdAndAnnouncementId(announcementDocumnetDTO.getDocumentId(), announcements.getAnnouncementId());
		
		// 1. 檢查是否權限
		Users tableUser = userSecurityUtil.getCurrentUserEntity();
		organizationValidationUtil.validateOrganizationPermission(tableUser, announcements.getOrganization().getOrganizationId());
		
		// 2. I/O
		documentAdminService.deleteByAnnouncement(announcements.getAnnouncementId(), documentAdmin.getAdminDocId());
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	public void deleteAnnouncement(AnnouncementDeleteDTO announcementDeleteDTO) {
		// 0. 檢查 完整性
		announcementValidationUtil.validateDelete(announcementDeleteDTO);
		Announcements announcements = entityFetcher.getAnnouncementsById(announcementDeleteDTO.getId());
		
		// 1. 檢查是否權限
		Users tableUser = userSecurityUtil.getCurrentUserEntity();
		organizationValidationUtil.validateOrganizationPermission(tableUser, announcements.getOrganization().getOrganizationId());
		
		// 2. 判斷過期時間
		LocalDate now = LocalDate.now();
		if (announcements.getIsPublished() == true && announcements.getExpiryDate().isAfter(now)) {
			throw new AnnouncementFailureException("公告尚未過期 無法刪除");
		}
		
		// 3. 執行刪除
		announcements.setDeleteAt(LocalDateTime.now());
		announcementsRepository.save(announcements);
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	public List<AnnouncementDTO> findAllAnnouncementNotPublish() {
		// 1. 是否有權限
		Users tableUser = userSecurityUtil.getCurrentUserEntity();
		
		// 2. 根據權限 抓取不同 的 公告
		List<Announcements> announcements = null;
		
		if (tableUser.getRole().getName().equals("ROLE_MANAGER")) {
			announcements = announcementsRepository.findAllByNoPublish();
		} else {
			// ROLE_STAFF
			announcements = announcementsRepository.findAllByNoPublishAndOrganizationId(tableUser.getAdminInfo().getOrganization().getOrganizationId());
		}
		
		return announcements.stream()
				.map(announcement -> {
					AnnouncementDTO announcementDTO = modelMapper.map(announcement, AnnouncementDTO.class);
					return announcementDTO;
				}).toList();
	}
	
	@Override
	@PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	public AnnouncementDTO publishAnnouncement(AnnouncementPublishDTO announcementPublishDTO) {
		// 0. 檢查 完整性
		announcementValidationUtil.validatePublish(announcementPublishDTO);
		Announcements announcements = entityFetcher.getAnnouncementsById(announcementPublishDTO.getId());
		
		// 1. 檢查是否權限
		Users tableUser = userSecurityUtil.getCurrentUserEntity();
		organizationValidationUtil.validateOrganizationPermission(tableUser, announcements.getOrganization().getOrganizationId());
		
		// 2. 判斷是否 已經 公告了
		if (announcements.getIsPublished() == true || announcements.getExpiryDate() != null) {
			throw new AnnouncementFailureException("此公告 已經公告過了");
		}
		
		// 3. 開始發布公告
		LocalDate expiryDate = LocalDate.now().plusDays(announcementPublishDTO.getDaysUntilExpiry());
		announcements.setIsPublished(true);
		announcements.setExpiryDate(expiryDate);
		
		announcements = announcementsRepository.save(announcements);
		
		// 4. DTO 回傳
		return modelMapper.map(announcements, AnnouncementDTO.class);
	}
}
