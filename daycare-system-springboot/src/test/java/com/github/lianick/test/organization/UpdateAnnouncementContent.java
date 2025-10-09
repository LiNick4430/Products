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
public class UpdateAnnouncementContent {

	@Autowired
	private AnnouncementsRepository announcementsRepository;
	
	@Test
	@Rollback(false)	// 強制提交事務，不回滾。
	public void updateContent() {
		// 變數
		Long announcementId = 1L;
		LocalDate now = LocalDate.now();
		String newContent = "123456789 \n"
				+ "987654321 \n";
		// 尋找公告
		Optional<Announcements> optAnnouncement = announcementsRepository.findById(announcementId);
		if (optAnnouncement.isEmpty()) {
			System.out.printf("公告ID = %d 不存在", announcementId);
			return;
		}
		Announcements announcements = optAnnouncement.get();
		// 查看 公告 是否已經發布
		if (announcements.getIsPublished() == false) {
			announcements.setContent(newContent);
		} else {
			announcements.setContent(newContent + "\n" + now + "更新");
		}
		
		announcementsRepository.save(announcements);
		
		System.out.println("公告更新成功");
	}
}
