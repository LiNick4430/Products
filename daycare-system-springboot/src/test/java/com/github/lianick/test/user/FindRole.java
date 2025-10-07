package com.github.lianick.test.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.github.lianick.model.eneity.Role;
import com.github.lianick.repository.RoleRepository;

@SpringBootTest
public class FindRole {

	@Autowired
	private RoleRepository roleRepository;
	
	@Test
	public void find() {
		Role role = roleRepository.findById(1L).get();
		
		System.out.println(role.getName());
	}
	
}
