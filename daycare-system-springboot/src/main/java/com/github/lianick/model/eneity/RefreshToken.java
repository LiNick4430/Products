package com.github.lianick.model.eneity;

import java.time.Instant;

import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "refresh_token")
@SQLRestriction("delete_at IS NULL")
public class RefreshToken extends BaseEntity{	// 登陸時 使用的 JWT refresh token

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "refresh_token_id")
	private Long refreshTokenId;				// refresh token ID
	
	@Column(name = "refresh_token_token", nullable = false, unique = true)
	private String token;						// JWT 的 refresh token token
	
	@Column(name = "refresh_token_expiry_time", nullable = false)
	private Instant expiryTime;					// JWT 的 refresh token 過期時間 = 如果用戶在 30 天內沒有使用或刷新這個 Token，它將永久失效。
	
	// 1位 User 只能有一個 多個 token
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private Users users;
	
	/**
     * Helper Method: 檢查 Token 是否過期
     * @return true 如果已過期
     */
    public boolean isExpired() {
        return this.expiryTime.isBefore(Instant.now());
    }
}
