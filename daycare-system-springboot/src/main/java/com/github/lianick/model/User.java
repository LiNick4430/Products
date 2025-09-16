package com.github.lianick.model;

import java.time.LocalDateTime;

import jakarta.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "users")					// 指定資料表名稱為 "users"
@EqualsAndHashCode(callSuper = true)	// 將 callSuper 設為 true，告訴 Lombok 在生成 equals() 和 hashCode() 時，也要包含父類別的欄位。
public class User extends BaseModel{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;					// 系統產生的 流水編號
	@Column(nullable = false)
	private String name;				// 姓名
	@Column(name = "national_id_no", nullable = false, unique = true)
	private String nationalIdNo; 		// 身分證字號
	@Column(nullable = false)
	private LocalDateTime birthdate;	// 生日
	
	@Column(name = "registered_address", nullable = false)
	private String registeredAddress;	// 戶籍地址
	@Column(name = "mailing_address", nullable = false)
	private String mailingAddress;		// 通訊地址
	@Column(nullable = false, unique = true)
	private String email;				// e-mail
	@Column(name = "phone_number", nullable = false)
	private String phoneNumber;			// 手機號碼
	
	@Column(nullable = false, unique = true)
	private String account;				// 帳號名稱
	
	@JsonIgnore // 防止回傳 JSON 時洩漏密碼
    @Column(nullable = false)
	private String password;			// 密碼(hash)		使用 PasswordSecurity, BCrypt 演算法 的 hash 內部 包含了 salt
	// private String hash;				// 密碼(hash)		使用 PasswordHash
	// private String salt;				// 密碼(salt)		使用 PasswordHash
	
	@Column(nullable = false)
	private String role;				// 帳號權限等級
	
	@Column(name = "is_active", nullable = false)
	private Boolean isActive;			// 帳號是否已啟用 (Email驗證後設為true)
	
}
