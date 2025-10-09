package com.github.lianick.test.organization;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import com.github.lianick.model.eneity.Announcements;
import com.github.lianick.model.eneity.Organization;
import com.github.lianick.repository.AnnouncementsRepository;
import com.github.lianick.repository.OrganizationRepository;

import jakarta.transaction.Transactional;

@SpringBootTest		// 載入 Spring Boot 應用程式的完整上下文
@Transactional		// 確保整個測試類別在事務中運行
public class CreateAnnouncement {

	@Autowired
	private AnnouncementsRepository announcementsRepository;
	
	@Autowired
	private OrganizationRepository organizationRepository;
	
	@Test
	@Rollback(false)	// 強制提交事務，不回滾。	
	public void create() {
		// 變數
		Long organizationId = 2L;
		String content = "ABCDEFG \t\n"
				+ "12345678 \t\n"
				+ "!QAZ@WSX \t\n"
				+ "#EDCVFR$ \t\n";
		
		// 找尋 目標機構
		Optional<Organization> optOrganization = organizationRepository.findById(organizationId);
		if (optOrganization.isEmpty()) {
			System.out.printf("機構 ID = %d, 錯誤", organizationId);
			return;
		}
		Organization organization = optOrganization.get();
		
		// 建立公告
		Announcements announcements = new Announcements();
		announcements.setTitle("公告B");
		announcements.setContent(content);
		// 建立關聯
		announcements.setOrganization(organization);
		
		// 儲存
		announcementsRepository.save(announcements);
		
		System.out.println("儲存成功");
	}
}
