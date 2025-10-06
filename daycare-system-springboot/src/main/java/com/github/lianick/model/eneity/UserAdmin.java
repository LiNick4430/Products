package com.github.lianick.model.eneity;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "admin_user")			// 員工 帳號相關
public class UserAdmin extends BaseEntity{

	// ID 設置	共享主鍵（Shared Primary Key, SPK）模式
	@Id
	// @GeneratedValue(strategy = GenerationType.IDENTITY)	因為 @MapsId 這個將被 忽略
	@Column(name = "admin_id")
	private Long adminId;				// 員工 ID
	// 定義一對一關係
	@MapsId								// @MapsId 告訴 JPA 這個 adminId 的值來自 Users 的主鍵
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "admin_id")
	private Users users;				// 帳號 ID
	
	// 基本資料
	@Column(name = "admin_name", nullable = false)
	private String name;				// 員工 姓名
	
	@Column(name = "admin_job_title", nullable = false)
	private String jobTitle;				// 職稱
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "organization_id", nullable = false)
	private Organization organization;		// 所屬機構
	
	// 處理業務	
	@OneToMany(mappedBy = "userAdmin", fetch = FetchType.LAZY)
	private Set<ReviewLogs> reviewHistorys;		// 所審核過 案件
}
