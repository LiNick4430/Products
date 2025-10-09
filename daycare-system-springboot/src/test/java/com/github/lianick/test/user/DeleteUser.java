package com.github.lianick.test.user;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import com.github.lianick.model.eneity.Users;
import com.github.lianick.repository.UsersRepository;

import jakarta.transaction.Transactional;

@SpringBootTest		// 載入 Spring Boot 應用程式的完整上下文
@Transactional		// 確保整個測試類別在事務中運行
public class DeleteUser {
	
	@Autowired
	private UsersRepository usersRepository;

	@Test
	@Rollback(false)	// 強制提交事務，不回滾。
	public void deleteNoActiveUser() {
		// 變數
		Long userId = 5L;
		LocalDateTime deleteTime = LocalDateTime.now();
		
		// 找到 目標 ID
		Optional<Users> optUser = usersRepository.findNoActiveAndUnlinkedUserById(userId);
		if (optUser.isEmpty()) {
			System.out.println("User id = %d 不存在 或 已經被刪除");
			return;
		}
		Users user = optUser.get();
		
		// 執行 軟刪除
		user.setDeleteAt(deleteTime);
		
		// 回存
		usersRepository.save(user);
		
		System.out.println("刪除成功");
	}
}
