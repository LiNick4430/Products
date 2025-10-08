package com.github.lianick.test.user.adminUser;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import com.github.lianick.model.eneity.UserAdmin;
import com.github.lianick.repository.UserAdminRepository;

import jakarta.transaction.Transactional;

//當 民眾 想要 帳號的時候

@SpringBootTest		// 載入 Spring Boot 應用程式的完整上下文
@Transactional		// 確保整個測試類別在事務中運行
public class DeleteAdminUser {			

	@Autowired
	private UserAdminRepository userAdminRepository;
	
	@Test	
	@Rollback(false)	// 強制提交事務，不回滾。
	public void delete() {		// 軟刪除 = delete_at 打上時間
		// 測試用變數
		Long adminId = 3L;
		
		// 檢查 是否 存在
		Optional<UserAdmin> optUserAdmin = userAdminRepository.findById(adminId);
		if (optUserAdmin.isEmpty()) {
			System.out.printf("Admin id = %d 不存在 或者 被刪除\n", adminId);
			return;
		}
		
		UserAdmin userAdmin = optUserAdmin.get();

		// 執行 軟刪除
		userAdmin.setDeleteAt(LocalDateTime.now());
		userAdmin.getUsers().setDeleteAt(LocalDateTime.now());
		
		// 儲存回去資料庫
		userAdminRepository.save(userAdmin);
		
		System.out.println("刪除帳號成功");
	}
}
