package com.github.lianick.test;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import jakarta.transaction.Transactional;

@SpringBootTest		// 載入 Spring Boot 應用程式的完整上下文
@Transactional		// 確保整個測試類別在事務中運行
public class TemplateTest {

	@Test
	// TODO 預設為寫入測試：強制提交事務，不回滾。	如果只是讀取/查詢測試，請移除 @Rollback(false)
	@Rollback(false)	
	public void name() {
		// TODO 變數
		
		// TODO 書寫方法邏輯
	}
}
