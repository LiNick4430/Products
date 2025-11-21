package com.github.lianick.util.validate;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.github.lianick.exception.FileStorageException;
import com.github.lianick.exception.ValueMissException;
import com.github.lianick.model.dto.announcement.AnnouncementCreateDTO;
import com.github.lianick.model.dto.announcement.AnnouncementDeleteDTO;
import com.github.lianick.model.dto.announcement.AnnouncementDocumnetDTO;
import com.github.lianick.model.dto.announcement.AnnouncementFindDTO;
import com.github.lianick.model.dto.announcement.AnnouncementPublishDTO;
import com.github.lianick.model.dto.announcement.AnnouncementUpdateDTO;
import com.github.lianick.model.dto.organization.OrganizationDocumentDTO;

/**
 * 負責處理 Announcement 相關的 完整性 檢測
 * */
@Service
public class AnnouncementValidationUtil {

	/**
	 * AnnouncementFindDTO 的完整性
	 * */
	public void validateFind(AnnouncementFindDTO announcementFindDTO) {
		if (announcementFindDTO.getId() == null) {
			throw new ValueMissException("缺少必要資訊(公告ID)");
		}
	}
	
	/**
	 * AnnouncementCreateDTO 的完整性
	 * */
	public void validateCreateFields(AnnouncementCreateDTO announcementCreateDTO) {
		if (announcementCreateDTO.getTitle() == null || announcementCreateDTO.getTitle().isBlank() ||
				announcementCreateDTO.getContent() == null || announcementCreateDTO.getContent().isBlank() ||
				announcementCreateDTO.getOrganizationId() == null) {
			throw new ValueMissException("缺少必要資訊(公告標題, 公告內文, 機構ID)");
		}
	}
	
	/**
	 * AnnouncementUpdateDTO 的完整性
	 * */
	public void validateUpdate(AnnouncementUpdateDTO announcementUpdateDTO) {
		if (announcementUpdateDTO.getId() == null || announcementUpdateDTO.getOrganizationId() == null) {
			throw new ValueMissException("缺少必要資訊(公告ID, 機構ID)");
		}
	}
	
	/**
	 * AnnouncementDeleteDTO 的完整性
	 * */
	public void validateDelete(AnnouncementDeleteDTO announcementDeleteDTO) {
		if (announcementDeleteDTO.getId() == null) {
			throw new ValueMissException("缺少必要資訊(公告ID)");
		}
	}
	
	/**
	 * AnnouncementPublishDTO 的完整性
	 * */
	public void validatePublish(AnnouncementPublishDTO announcementPublishDTO) {
		if (announcementPublishDTO.getId() == null || announcementPublishDTO.getDaysUntilExpiry() == null) {
			throw new ValueMissException("缺少必要資訊(公告ID, 幾天後過期)");
		}
	}
	
	/**
	 * AnnouncementDocumnetDTO 的 完整性
	 * @param isUpload = true 需要 AnnouncementId(id) + file, false 需要 AnnouncementId(id) + DoucmnetId
	 * */
	public void validateDocument(AnnouncementDocumnetDTO announcementDocumnetDTO, MultipartFile file, Boolean isUpload) {
		if (isUpload) {
			if (announcementDocumnetDTO.getId() == null) {
				throw new ValueMissException("缺少必要的公告上傳資料 (公告ID)");
			}
			if (file.isEmpty()) {
				throw new FileStorageException("檔案錯誤：上傳檔案不存在");
			}
		} else {
			if (announcementDocumnetDTO.getId() == null || announcementDocumnetDTO.getDocumentId() == null) {
				throw new ValueMissException("缺少必要的公告上傳資料 (公告ID, 附件ID)");
			}
		}
	}
}
