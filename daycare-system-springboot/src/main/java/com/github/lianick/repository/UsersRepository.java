package com.github.lianick.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.github.lianick.model.eneity.Users;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

	// 尋找 未通過認證(user_is_active = false) 且 尚未連結 public/admin user_id 帳號 
	@Query(value = 
			"SELECT users.* FROM users "
			+ "LEFT JOIN public_user ON users.user_id = public_user.public_id "
			+ "LEFT JOIN admin_user ON users.user_id = admin_user.admin_id "
			+ "WHERE user_id = :id AND user_is_active = false "
			+ "AND public_user.public_id IS NULL AND admin_user.admin_id IS NULL"
			, nativeQuery = true)
	Optional<Users> findNoActiveAndUnlinkedUserById(@Param("id") Long id);
	
	// 尋找 通過認證(user_is_active = true) 的 public user_id 帳號 
	@Query(value = 
			"SELECT * FROM users WHERE user_id = :id AND user_is_active = true AND role_id = 1"
			, nativeQuery = true)
	Optional<Users> findActivePublicUserById(@Param("id") Long id);
	
	// 尋找 通過認證(user_is_active = true) 的 admin user_id 帳號 
	@Query(value = 
			"SELECT * FROM users WHERE user_id = :id AND user_is_active = true AND role_id IN (2, 3)"
			, nativeQuery = true)
	Optional<Users> findActiveAdminUserById(@Param("id") Long id);
	
}
