package com.github.lianick.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.github.lianick.model.eneity.UserVerify;

import jakarta.transaction.Transactional;
import java.util.List;


@Repository
public interface UsersVerifyRepository extends JpaRepository<UserVerify, Long> {

	// 尋找 使用者帳號 最新的 未過期 認證
	@Query(value = 
			"SELECT verify_user.* FROM verify_user "
			+ "JOIN users ON users.user_id = verify_user.user_id "
			+ "WHERE verify_user.delete_at IS NULL "
			+ "AND verify_user_is_used = false "
			+ "AND verify_user_expiry_time > now() "
			+ "AND user_account = :account "
			+ "ORDER BY verify_user.create_date DESC "
			+ "LIMIT 1"
			, nativeQuery = true)
	Optional<UserVerify> findNewTokenByUserAccount(@Param("account") String account);
	
	// 將 使用者帳號 所有 未使用的 token 打上 已使用
	@Modifying 		// 標記這個方法用於修改數據 (INSERT, UPDATE, DELETE)
    @Transactional 	// 確保修改操作在事務中進行
 	@Query(value = 
			"UPDATE verify_user "
			+ "JOIN users ON users.user_id = verify_user.user_id "
			+ "SET verify_user.verify_user_is_used = true "
			+ "WHERE verify_user.verify_user_is_used = false "
			+ "AND user_account = :account "
			, nativeQuery = true)
	Integer markAllUnusedTokenAsUsed(@Param("account") String account);
	
	// 使用 Token 反找
	Optional<UserVerify> findByToken(String token);
}
