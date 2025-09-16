package com.github.lianick.service;

import java.util.List;

import com.github.lianick.model.User;

public interface UserService {

	List<User> findAllUser();
	User getUserById(Long id);
	User getUserByAccount(String account, String password);
	
	boolean login(String account, String password);
}
