package com.github.lianick.test.user.childInfo;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import com.github.lianick.model.eneity.ChildInfo;
import com.github.lianick.model.eneity.UserPublic;
import com.github.lianick.repository.ChildInfoRepository;
import com.github.lianick.repository.UserPublicRepository;

import jakarta.transaction.Transactional;

@SpringBootTest		// 載入 Spring Boot 應用程式的完整上下文
@Transactional		// 確保整個測試類別在事務中運行
public class CreateChild {

	@Autowired
	private UserPublicRepository userPublicRepository;
	
	@Autowired
	private ChildInfoRepository childInfoRepository;
	
	@Test
	@Rollback(false)	// 強制提交事務，不回滾。
	public void caeate() {
		// 變數
		Long publicId = 2L;
		
		// 找尋 民眾帳號 是否存在
		Optional<UserPublic> optUserPublic = userPublicRepository.findById(publicId);
		if (optUserPublic.isEmpty()) {
			System.out.printf("Public id = %d 不存在 或者 被刪除\n", publicId);
			return;
		}
		UserPublic userPublic = optUserPublic.get();
		
		// 創建 幼兒資訊
		ChildInfo childInfo = new ChildInfo();
		childInfo.setName("幼兒B");
		childInfo.setNationalIdNo("幼兒B");	// 測試用
		childInfo.setBirthDate(LocalDate.parse("2025-01-01"));
		childInfo.setGender("女");
		
		// 建立 關聯
		childInfo.setUserPublic(userPublic);
		childInfoRepository.save(childInfo);
		
		System.out.println("幼兒資料 儲存成功");
	}
}
