package com.github.lianick.test.user.adminUser;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.github.lianick.model.eneity.Role;
import com.github.lianick.model.eneity.Users;
import com.github.lianick.repository.RoleRepository;
import com.github.lianick.repository.UsersRepository;

// 申請民眾帳號
@SpringBootTest
public class CreateAdminUser {

	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Test
	public void create() {
		Role ROLE_STAFF = roleRepository.findByName("ROLE_STAFF").get();
		
		Users user = new Users();
		user.setEmail("test04@xxx.com");
		user.setPhoneNumber("0900111222");
		user.setAccount("test04");
		user.setPassword("123456");
		user.setRole(ROLE_STAFF);
		
		usersRepository.save(user);
		
		System.out.println("申請帳號成功");
	}
	
}
