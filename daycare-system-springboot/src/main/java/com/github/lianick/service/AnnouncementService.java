package com.github.lianick.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.github.lianick.model.dto.DownloadDTO;
import com.github.lianick.model.dto.announcement.AnnouncementCreateDTO;
import com.github.lianick.model.dto.announcement.AnnouncementDTO;
import com.github.lianick.model.dto.announcement.AnnouncementDeleteDTO;
import com.github.lianick.model.dto.announcement.AnnouncementDocumnetDTO;
import com.github.lianick.model.dto.announcement.AnnouncementFindDTO;
import com.github.lianick.model.dto.announcement.AnnouncementPublishDTO;
import com.github.lianick.model.dto.announcement.AnnouncementUpdateDTO;

// 公告用 服務
public interface AnnouncementService {

	/** 尋找公告(活動) */
	List<AnnouncementDTO> findAllActiveAnnouncement();
	
	/** 尋找公告(未過期包含未發布)
	 * 需要 @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')") 
	 * */
	List<AnnouncementDTO> findAllNoExpiryAnnouncement();
	
	/** 尋找 公告(活動) */
	AnnouncementDTO findActiveAnnouncementById(AnnouncementFindDTO announcementFindDTO);
	
	/** 尋找 公告(未過期包含未發布) 
	 * 需要 @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')") 
	 * */
	AnnouncementDTO findNoExpiryAnnouncementById(AnnouncementFindDTO announcementFindDTO);
	
	/** 下載 公告附件 */
	DownloadDTO downloadDoc(AnnouncementDocumnetDTO announcementDocumnetDTO);
	
	/** 建立 公告<p>
	 * 需要 @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')") 
	 * */
	AnnouncementDTO createAnnouncement(AnnouncementCreateDTO announcementCreateDTO);
	
	/** 修改 公告<p>
	 * 需要 @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')") 
	 * */
	AnnouncementDTO updateAnnouncement(AnnouncementUpdateDTO announcementUpdateDTO);
	
	/** 添加 公告附件<p>
	 * 需要 @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")  
	 * */
	AnnouncementDTO uploadAnnouncementDocument(AnnouncementDocumnetDTO announcementDocumnetDTO, MultipartFile file);
	
	/** 刪除 公告附件<p>
	 * 需要 @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')") 
	 */
	void deleteAnnouncementDocument(AnnouncementDocumnetDTO announcementDocumnetDTO);
	
	/** 刪除 公告<p>
	 * 需要 @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")  
	 * */
	void deleteAnnouncement(AnnouncementDeleteDTO announcementDeleteDTO);
	
	/** 尋找公告<p>
	 * 需要 @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')") 
	 *  */
	List<AnnouncementDTO> findAllAnnouncementNotPublish();
	
	/** 發布 公告<p>
	 * 需要 @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')") 
	 * */
	AnnouncementDTO publishAnnouncement(AnnouncementPublishDTO announcementPublishDTO);
}
