package com.github.lianick.test.organization;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import com.github.lianick.model.eneity.Announcements;
import com.github.lianick.repository.AnnouncementsRepository;

import jakarta.transaction.Transactional;

@SpringBootTest		// 載入 Spring Boot 應用程式的完整上下文
@Transactional		// 確保整個測試類別在事務中運行
public class PublishAnnouncement {
	
	@Autowired
	private AnnouncementsRepository announcementsRepository;

	@Test
	@Rollback(false)	// 強制提交事務，不回滾。
	public void publish() {		// 公告 發布
		// 變數
		Long announcementId = 1L;
		Integer plusMonths = 6;				// 6個月後過期
		LocalDate now = LocalDate.now();
		LocalDate expiryDate = now.plusMonths(plusMonths);
		// 尋找公告
		Optional<Announcements> optAnnouncement = announcementsRepository.findById(announcementId);
		if (optAnnouncement.isEmpty()) {
			System.out.printf("公告ID = %d 不存在", announcementId);
			return;
		}
		Announcements announcement = optAnnouncement.get();
		// 條件 檢測
		
		if (announcement.getIsPublished() != false) {			// 是否已經發布
			System.out.printf("公告ID = %d 已經發布", announcementId);
			return;
		}
		
		// 公告發布
		announcement.setIsPublished(true);
		announcement.setPublishDate(now);
		announcement.setExpiryDate(expiryDate);
		
		announcementsRepository.save(announcement);
		
		System.out.println("公告發布成功");
	}
}
