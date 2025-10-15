package com.github.lianick.model.eneity;

import java.time.LocalDateTime;

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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name = "verify_user")			// 帳號 認證碼
@SQLRestriction("delete_at IS NULL")
public class UserVerify extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "verify_user_id")
	private Long verifyId;				// 驗證碼 ID
	
	// 由 UUID 產生的 = 128位元(長度36), 經由處裡減去"-" 變成 length = 32
	@Column(name = "verify_user_toekn", nullable = false, unique = true, length = 32)
	private String token;				// 認證碼 toekn
	
	@Column(name = "verify_user_expiry_time", nullable = false)
	private LocalDateTime expiryTime;	// 認證碼 過期時間(預設 +15分)
	
	@Column(name = "verify_user_is_used")
	private Boolean isUsed = false;		// 認證碼 是否使用過
	
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private Users users;				// 認證 帳號
	
	@Column(name = "verify_user_new_email_target")
	private String newEmailTarget;		// 認證 新信箱 使用的 
	
}
