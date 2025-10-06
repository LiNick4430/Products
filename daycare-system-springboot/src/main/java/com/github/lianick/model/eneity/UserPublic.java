package com.github.lianick.model.eneity;

import java.time.LocalDate;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
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

	// ID 設置	共享主鍵（Shared Primary Key, SPK）模式
	@Id
	// @GeneratedValue(strategy = GenerationType.IDENTITY)	因為 @MapsId 這個將被 忽略
	@Column(name = "public_id")
	private Long publicId;				// 民眾 ID
	// 定義一對一關係
	@MapsId								// @MapsId 告訴 JPA 這個 publicId 的值來自 Users 的主鍵
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "public_id")
	private Users users;				// 帳號 ID
	
	// 基本資料
	@Column(name = "public_name", nullable = false)
	private String name;				// 民眾 姓名
	
	// 反向關聯 一個 User_Public 對應多個 ChildInfo
	// cascade = CascadeType.ALL => 對父 Entity (UserPublic) 執行的任何操作，都應該自動應用於所有相關的子 Entity (ChildInfo)。
	@OneToMany(mappedBy = "userPublic", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<ChildInfo> children;		// 民眾的所有幼兒清單
	
	@OneToMany(mappedBy = "userPublic", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<DocumentPublic> documents;	// 民眾的所有附件清單
	
	@Column(name = "public_national_id_no", nullable = false, unique = true)
	private String nationalIdNo; 		// 身分證字號
	
	@Column(name = "public_birthdate", nullable = false)
	private LocalDate birthdate;		// 生日
	
	@Column(name = "public_registered_address", nullable = false)
	private String registeredAddress;	// 戶籍地址
	
	@Column(name = "public_mailing_address", nullable = false)
	private String mailingAddress;		// 通訊地址
}
