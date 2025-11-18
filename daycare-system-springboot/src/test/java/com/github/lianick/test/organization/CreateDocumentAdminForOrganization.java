package com.github.lianick.test.organization;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import com.github.lianick.model.eneity.Announcements;
import com.github.lianick.model.eneity.DocumentAdmin;
import com.github.lianick.model.eneity.Organization;
import com.github.lianick.repository.AnnouncementsRepository;
import com.github.lianick.repository.DocumentAdminRepository;
import com.github.lianick.repository.OrganizationRepository;

import jakarta.transaction.Transactional;

@SpringBootTest		// 載入 Spring Boot 應用程式的完整上下文
@Transactional		// 確保整個測試類別在事務中運行
public class CreateDocumentAdminForOrganization {

	@Autowired
	private DocumentAdminRepository documentAdminRepository;
	
	@Autowired
	private OrganizationRepository organizationRepository;
	
	@Autowired
	private AnnouncementsRepository announcementsRepository;
	
	@Test
	@Rollback(false)	// 強制提交事務，不回滾。	
	public void name() {
		// 變數
		Long organizationId = 2L;
		Long announcementId = 2L;
		Boolean isOrganization = false;		// 機構文件(T) 或者 公告文件(F)
		// 尋找 機構/公告 是否存在 (一般會擇一)
		Optional<Organization> optOrganization = organizationRepository.findById(organizationId);
		if (optOrganization.isEmpty()) {
			System.out.printf("機構 ID = %d, 錯誤", organizationId);
			return;
		}
		Optional<Announcements> optAnnouncement = announcementsRepository.findById(announcementId);
		if (optAnnouncement.isEmpty()) {
			System.out.printf("公告ID = %d 不存在", announcementId);
			return;
		}
		Organization organization = optOrganization.get();
		Announcements announcements = optAnnouncement.get();
		
		// 建立文件
		DocumentAdmin documentAdmin = new DocumentAdmin();
		documentAdmin.setFileName("公告文件");
		documentAdmin.setStoragePath("雲端");
		
		// 根據需求 建立關聯
		if (isOrganization) {
			documentAdmin.setOrganization(organization);
		} else {
			documentAdmin.setAnnouncements(announcements);
		}
		
		documentAdminRepository.save(documentAdmin);
		
		System.out.println("附件 建立成功");
		
	}
}
