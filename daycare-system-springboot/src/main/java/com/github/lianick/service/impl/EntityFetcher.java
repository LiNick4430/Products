package com.github.lianick.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.lianick.exception.TokenFailureException;
import com.github.lianick.exception.UserNoFoundException;
import com.github.lianick.model.eneity.UserVerify;
import com.github.lianick.model.eneity.Users;
import com.github.lianick.repository.UsersRepository;
import com.github.lianick.repository.UsersVerifyRepository;

/**
 * 負責處理 各種獲取 Entity 的方法
 * */
@Service
public class EntityFetcher {

	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private UsersVerifyRepository usersVerifyRepository;
	
	/**
	 * 使用 username 獲取 Users
	 * */
	public Users getUsersByUsername(String username) {
		Users tableUser = usersRepository.findByAccount(username)
		        .orElseThrow(() -> new UserNoFoundException("帳號或密碼錯誤"));
		return tableUser;
	}
	
	/**
	 * 使用 token 獲取 UserVerify
	 * */
	public UserVerify getUserVerifyByToken(String token) {
		UserVerify userVerify = usersVerifyRepository.findByToken(token)
				.orElseThrow(() -> new TokenFailureException("驗證碼 無效或不存在"));
		return userVerify;
	}
	
}
