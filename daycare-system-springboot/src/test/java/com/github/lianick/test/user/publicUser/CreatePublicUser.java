package com.github.lianick.test.user.publicUser;

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
public class CreatePublicUser {

	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Test
	@Rollback(false)
	public void create() {
		Role ROLE_PUBLIC = roleRepository.findByName("ROLE_PUBLIC").get();
		
		Users user = new Users();
		user.setEmail("test05@xxx.com");
		user.setPhoneNumber("0900111222");
		user.setAccount("test05");
		user.setPassword("123456");
		user.setRole(ROLE_PUBLIC);
		
		usersRepository.save(user);
		
		System.out.println("申請帳號成功");
	}
	
}
