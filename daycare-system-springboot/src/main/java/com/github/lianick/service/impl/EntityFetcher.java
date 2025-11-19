package com.github.lianick.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.lianick.exception.OrganizationFailureException;
import com.github.lianick.exception.RoleFailureException;
import com.github.lianick.exception.TokenFailureException;
import com.github.lianick.exception.UserNoFoundException;
import com.github.lianick.model.eneity.Organization;
import com.github.lianick.model.eneity.Role;
import com.github.lianick.model.eneity.UserAdmin;
import com.github.lianick.model.eneity.UserPublic;
import com.github.lianick.model.eneity.UserVerify;
import com.github.lianick.model.eneity.Users;
import com.github.lianick.repository.OrganizationRepository;
import com.github.lianick.repository.RoleRepository;
import com.github.lianick.repository.UserAdminRepository;
import com.github.lianick.repository.UserPublicRepository;
import com.github.lianick.repository.UsersRepository;
import com.github.lianick.repository.UsersVerifyRepository;

/**
 * 負責處理 各種獲取 Entity 的方法 (不包含 JWT)
 * */
@Service
public class EntityFetcher {

	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private UserPublicRepository userPublicRepository;
	
	@Autowired
	private UserAdminRepository userAdminRepository;
	
	@Autowired
	private UsersVerifyRepository usersVerifyRepository;
	
	@Autowired
	private OrganizationRepository organizationRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	/**
	 * 使用 username 獲取 Users
	 * */
	public Users getUsersByUsername(String username) {
		Users tableUser = usersRepository.findByAccount(username)
		        .orElseThrow(() -> new UserNoFoundException("帳號或密碼錯誤"));
		return tableUser;
	}
	
	/**
	 * 使用 username 獲取 UserPublic
	 * */
	public UserPublic getUsersPublicByUsername(String username) {
		Users tableUser = getUsersByUsername(username);
		
		UserPublic userPublic = userPublicRepository.findByUsers(tableUser)
				.orElseThrow(() -> new UserNoFoundException("帳號錯誤"));
		
		return userPublic;
	}
	
	/**
	 * 使用 Users 獲取 UserPublic
	 * */
	public UserPublic getUsersPublicByUser(Users user) {
		UserPublic userPublic = userPublicRepository.findByUsers(user)
				.orElseThrow(() -> new UserNoFoundException("帳號錯誤"));
		
		return userPublic;
	}
	
	/**
	 * 使用 username 找到 userAdmin
	 * */
	public UserAdmin getUserAdminByUsername(String username) {
		Users tableUser = getUsersByUsername(username);
		
		UserAdmin userAdmin = userAdminRepository.findByUsers(tableUser)
				.orElseThrow(() -> new UserNoFoundException("用戶存在，但非員工帳號"));
		
		return userAdmin;
	}
	
	/**
	 * 使用 token 獲取 UserVerify
	 * */
	public UserVerify getUserVerifyByToken(String token) {
		UserVerify userVerify = usersVerifyRepository.findByToken(token)
				.orElseThrow(() -> new TokenFailureException("驗證碼 無效或不存在"));
		return userVerify;
	}
	
	/**
	 * 使用 OrganizationId 獲取 Organization
	 * */
	public Organization getOrganizationById(Long id) {
		Organization organization = organizationRepository.findById(id)
				.orElseThrow(() -> new OrganizationFailureException("機構找不到"));
		return organization;
	}
	
	/**
	 * 使用 OrganizationId 獲取 Organization 附加 錯誤訊息
	 * */
	public Organization getOrganizationById(Long id, String message) {
		Organization organization = organizationRepository.findById(id)
				.orElseThrow(() -> new OrganizationFailureException(message));
		return organization;
	}
	
	/**
	 * 使用 RoleId 獲取 Role
	 * */
	public Role getRoleById(Long id) {
		Role role = roleRepository.findById(id)
			.orElseThrow(() -> new RoleFailureException("角色找不到"));
		return role;
	}
	
	/**
	 * 使用 RoleId 獲取 Role 附加 錯誤訊息
	 * */
	public Role getRoleById(Long id, String message) {
		Role role = roleRepository.findById(id)
			.orElseThrow(() -> new RoleFailureException(message));
		return role;
	}
}
