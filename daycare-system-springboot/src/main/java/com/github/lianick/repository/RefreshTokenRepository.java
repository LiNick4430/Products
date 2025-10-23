package com.github.lianick.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.github.lianick.model.eneity.RefreshToken;
import com.github.lianick.model.eneity.Users;

import jakarta.transaction.Transactional;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>{

	// 用 users 尋找 RefreshToken
	Optional<RefreshToken> findByUsers(Users users);
	
	// 用 token 尋找 RefreshToken
	Optional<RefreshToken> findByToken(String token);
	
	// 登出的時候 將 所有的 使用中的 登入TOKEN 刪除
	@Modifying
	@Transactional
	@Query(value = 
			"UPDATE refresh_token "
			+ "JOIN users ON users.user_id = refresh_token.user_id "
			+ "SET refresh_token.delete_at = CURRENT_TIMESTAMP "
			+ "WHERE refresh_token.delete_at IS NULL "
			+ "AND user_account = :account "
			, nativeQuery = true)
	Integer markAllTokenAsDeleteByAccount(@Param("account") String account);
	
	// 登入狀態下 重新發行 TOKEN 的時候 所使用
	@Modifying
	@Transactional
	@Query(value = 
			"UPDATE refresh_token "
			+ "SET refresh_token.delete_at = CURRENT_TIMESTAMP "
			+ "WHERE refresh_token.delete_at IS NULL "
			+ "AND refresh_token_token = :token "
			, nativeQuery = true)
	Integer markTokenAsDeleteByToken(@Param("token") String token);
}
