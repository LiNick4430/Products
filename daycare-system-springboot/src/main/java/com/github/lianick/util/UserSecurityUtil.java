package com.github.lianick.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.lianick.exception.UserNotFoundException;
import com.github.lianick.model.eneity.UserAdmin;
import com.github.lianick.model.eneity.UserPublic;
import com.github.lianick.model.eneity.Users;
import com.github.lianick.repository.UserAdminRepository;
import com.github.lianick.repository.UserPublicRepository;
import com.github.lianick.repository.UsersRepository;

/**
 * 從 JWT 中 取出 User 相關實體
 * */

@Service	// 因為和 資料庫 有交流 -> @Service
public class UserSecurityUtil {
	
	@Autowired
	private  UsersRepository usersRepository;
	
	@Autowired
	private  UserPublicRepository userPublicRepository;
	
	@Autowired
	private  UserAdminRepository userAdminRepository;

	/**
	 * 取出 User 實體
	 * */
	public Users getCurrentUserEntity() {
		String currentUsername = SecurityUtil.getCurrentUsername();
	    
	    // 1. 找尋資料庫 對應的帳號(使用 JWT 提供的 currentUsername 進行查詢)
	    Users tableUser = usersRepository.findByAccount(currentUsername)
	        .orElseThrow(() -> new UserNotFoundException("帳號或密碼錯誤"));
		
	    return tableUser;
	}
	
	/**
	 * 取出 UserPublic 實體
	 * */
	public UserPublic getCurrentUserPublicEntity() {
		UserPublic userPublic = userPublicRepository.findByUsers(getCurrentUserEntity())
				.orElseThrow(() -> new UserNotFoundException("帳號錯誤"));
		
	    return userPublic;
	}
	
	/**
	 * 取出 UserAdmin 實體
	 * */
	public UserAdmin getCurrentUserAdminEntity() {
		UserAdmin userAdmin = userAdminRepository.findByUsers(getCurrentUserEntity())
				.orElseThrow(() -> new UserNotFoundException("帳號錯誤"));
		
	    return userAdmin;
	}
}
