package com.github.lianick.test.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.github.lianick.model.eneity.Role;
import com.github.lianick.repository.RoleRepository;

import jakarta.transaction.Transactional;

// 尋找 Role 並 找到 所擁有的 permission
@SpringBootTest
@Transactional
public class FindRole {

	@Autowired
	private RoleRepository roleRepository;
	
	@Test
	@Transactional
	public void find() {
		// 設定變數
		Long userId = 2L;
		
		Role role = roleRepository.findById(userId).get();
		
		System.out.println(role.getName());
		
		role.getPermissions().forEach(permission -> {
			System.out.println(permission.getName());
		});
	}
}
