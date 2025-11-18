package com.github.lianick.test.user.publicUser;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import com.github.lianick.model.eneity.DocumentPublic;
import com.github.lianick.model.eneity.UserPublic;
import com.github.lianick.repository.DocumentPublicRepository;
import com.github.lianick.repository.UserPublicRepository;

import jakarta.transaction.Transactional;

@SpringBootTest		// 載入 Spring Boot 應用程式的完整上下文
@Transactional		// 確保整個測試類別在事務中運行
public class CreateDocument {

	@Autowired
	private UserPublicRepository userPublicRepository;
	
	@Autowired
	private DocumentPublicRepository documentPublicRepository;
	
	@Test
	@Rollback(false)	// 強制提交事務，不回滾。
	public void name() {
		// 變數
		Long publicId = 8L;
		
		// 找尋 民眾帳號 是否存在
		Optional<UserPublic> optUserPublic = userPublicRepository.findById(publicId);
		if (optUserPublic.isEmpty()) {
			System.out.printf("Public id = %d 不存在 或者 被刪除\n", publicId);
			return;
		}
		UserPublic userPublic = optUserPublic.get();
		
		// 建立 document
		DocumentPublic documentPublic = new DocumentPublic();
		documentPublic.setFileName("資料A");
		documentPublic.setStoragePath("地端");
		// 建立關係
		documentPublic.setUserPublic(userPublic);
		
		// 回存
		documentPublicRepository.save(documentPublic);
		
		System.out.println("檔案儲存成功");
	}
}
