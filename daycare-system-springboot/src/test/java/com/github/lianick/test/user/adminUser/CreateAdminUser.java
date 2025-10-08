package com.github.lianick.test.user.adminUser;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import com.github.lianick.model.eneity.Role;
import com.github.lianick.model.eneity.Users;
import com.github.lianick.repository.RoleRepository;
import com.github.lianick.repository.UsersRepository;

import jakarta.transaction.Transactional;

// 申請民眾帳號
@SpringBootTest
@Transactional
public class CreateAdminUser {

	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Test
	@Rollback(false)
	public void create() {
		Role ROLE_STAFF = roleRepository.findByName("ROLE_STAFF").get();
		Role ROLE_MANAGER = roleRepository.findByName("ROLE_MANAGER").get();
		
		Users user = new Users();
		user.setEmail("test07@xxx.com");
		user.setPhoneNumber("0900111222");
		user.setAccount("test07");
		user.setPassword("123456");
		user.setRole(ROLE_MANAGER);
		
		usersRepository.save(user);
		
		System.out.println("申請帳號成功");
	}
	
}
