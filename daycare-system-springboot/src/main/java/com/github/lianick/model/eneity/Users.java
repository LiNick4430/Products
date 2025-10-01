package com.github.lianick.model.eneity;

import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
@Table(name = "users")			// 指定資料表名稱為 "users" 帳號 相關
public class Users extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long userId;				// 帳號 ID

	@Column(nullable = false, unique = true)
	private String email;				// e-mail
	
	@Column(name = "phone_number", nullable = false)
	private String phoneNumber;			// 手機號碼
	
	@Column(nullable = false, unique = true)
	private String account;				// 帳號名稱
	
	@JsonIgnore // 防止回傳 JSON 時洩漏密碼
    @Column(nullable = false)
	private String password;			// 密碼(hash)		使用 PasswordSecurity, BCrypt 演算法 的 hash 內部 包含了 salt
	
	@Column(name = "is_active", nullable = false)
	private Boolean isActive;			// 帳號是否已啟用 (Email驗證後設為true)
	
	@Column(name = "active_date")
	private LocalDateTime activeDate;	// 啟用日期
	
	@Column(name = "login_date")
	private LocalDateTime loginDate;	// 最後登入日期
	
	// 多對一關係，定義 Role 的外鍵
	@ManyToOne(fetch = FetchType.EAGER)				// 建議 EAGER，因為登入時通常需要知道角色
	@JoinColumn(name = "role_id", nullable = false)	// <--- 外鍵欄位名稱，不可為空
	private Role role;
	
	// 反向關聯：一個 Users 對應一個 User_Public
	@OneToOne(mappedBy = "users", fetch = FetchType.LAZY)	// mappedBy 意思是 告訴 JPA 去 User_Public 找 users, 他會 會定義外鍵 (user_id)
	private UserPublic publicInfo;		
	
	// 反向關聯：一個 Users 對應一個 User_Admin
	@OneToOne(mappedBy = "users", fetch = FetchType.LAZY)
	private UserAdmin adminInfo;		
	
}
