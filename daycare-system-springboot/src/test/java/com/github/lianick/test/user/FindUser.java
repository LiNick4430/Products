package com.github.lianick.test.user;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.github.lianick.model.eneity.Users;
import com.github.lianick.repository.UsersRepository;

import jakarta.transaction.Transactional;

@SpringBootTest		// 載入 Spring Boot 應用程式的完整上下文
@Transactional		// 確保整個測試類別在事務中運行
public class FindUser {

	@Autowired
	private UsersRepository usersRepository;
	
	@Test
	public void findNoActiveAndUnlinkedUser() {
		
		List<Users> users = usersRepository.findNoActiveAndUnlinkedAllUser();
		
		users.forEach(user -> {
			System.out.println(user.getUserId());
		});
		
	}
}
