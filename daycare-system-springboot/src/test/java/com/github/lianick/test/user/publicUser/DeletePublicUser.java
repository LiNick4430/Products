package com.github.lianick.test.user.publicUser;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import com.github.lianick.model.eneity.UserPublic;
import com.github.lianick.repository.UserPublicRepository;

import jakarta.transaction.Transactional;

//當 民眾 想要 帳號的時候

@SpringBootTest		// 載入 Spring Boot 應用程式的完整上下文
@Transactional		// 確保整個測試類別在事務中運行
public class DeletePublicUser {			

	@Autowired
	private UserPublicRepository userPublicRepository;
	
	@Test	
	@Rollback(false)	// 強制提交事務，不回滾。
	public void delete() {		// 軟刪除 = delete_at 打上時間
		// 測試用變數
		Long publicId = 1L;
		
		// 檢查 是否 存在 
		Optional<UserPublic> optUserPublic = userPublicRepository.findById(publicId);
		if (optUserPublic.isEmpty()) {
			System.out.printf("Public id = %d 不存在\n", publicId);
			return;
		}
		
		UserPublic userPublic = optUserPublic.get();
		// 檢查 是否 已經被 軟刪除
		if (userPublic.getDeleteAt() != null) {
			System.out.printf("Public id = %d 已經被刪除\n", publicId);
			return;
		}
		
		// 執行 軟刪除
		userPublic.setDeleteAt(LocalDateTime.now());
		userPublic.getUsers().setDeleteAt(LocalDateTime.now());
		
		// 儲存回去資料庫
		userPublicRepository.save(userPublic);
		
		System.out.println("刪除帳號成功");
	}
}
