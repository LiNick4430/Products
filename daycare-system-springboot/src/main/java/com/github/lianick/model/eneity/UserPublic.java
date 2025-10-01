package com.github.lianick.model.eneity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
@Table(name = "public_user")		// 民眾 帳號相關
public class UserPublic extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "public_id")
	private Long publicId;				// 民眾 ID
	
	// 定義一對一關係，並將外鍵設為唯一 (unique = true)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", unique = true, nullable = false)
	private Users users;				// 帳號 ID
	
	@Column(name = "public_name", nullable = false)
	private String name;				// 民眾 姓名
	
	@Column(name = "public_national_id_no", nullable = false, unique = true)
	private String nationalIdNo; 		// 身分證字號
	@Column(nullable = false)
	private LocalDateTime birthdate;	// 生日
	
	@Column(name = "public_registered_address", nullable = false)
	private String registeredAddress;	// 戶籍地址
	
	@Column(name = "public_mailing_address", nullable = false)
	private String mailingAddress;		// 通訊地址
	
}
