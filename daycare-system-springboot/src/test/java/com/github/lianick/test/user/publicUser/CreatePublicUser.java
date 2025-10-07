package com.github.lianick.test.user.publicUser;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.github.lianick.model.eneity.Role;
import com.github.lianick.model.eneity.Users;
import com.github.lianick.repository.RoleRepository;
import com.github.lianick.repository.UsersRepository;

// 申請民眾帳號
@SpringBootTest
public class CreatePublicUser {

	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Test
	public void create() {
		Role ROLE_PUBLIC = roleRepository.findByName("ROLE_PUBLIC").get();
		
		Users user = new Users();
		user.setEmail("test02@xxx.com");
		user.setPhoneNumber("0900111222");
		user.setAccount("test02");
		user.setPassword("123456");
		user.setRole(ROLE_PUBLIC);
		
		usersRepository.save(user);
		
		System.out.println("申請帳號成功");
	}
	
}
