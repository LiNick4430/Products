package com.github.lianick.test.user.publicUser;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.github.lianick.model.eneity.UserPublic;
import com.github.lianick.repository.UserPublicRepository;

import jakarta.transaction.Transactional;


@SpringBootTest		// 載入 Spring Boot 應用程式的完整上下文
@Transactional		// 確保整個測試類別在事務中運行
public class FindPublicUser {

	@Autowired
	private UserPublicRepository userPublicRepository;
	
	@Test
	public void find() {
		
		List<UserPublic> userPublics =  userPublicRepository.findAll();
		
		userPublics.forEach(userpublic -> {
			System.out.println(userpublic.getPublicId());
		});
	}
}
