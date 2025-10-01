package com.github.lianick.model.eneity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "organization")		// 機構
public class Organization extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "organization_id")
	private Long organizationId;	// 機構 ID
	
	@Column(name = "name", unique = true, nullable = false)
	private String name;			// 機構 名稱
	
	@Column(name = "description")
	private String description;		// 機構 介紹
	
	@Column(name = "address", nullable = false)
	private String address;			// 機構 地址
	
	@Column(name = "phone", nullable = false)
	private String phone;			// 機構 電話
	
	@Column(name = "email", nullable = false)
	private String email;			// 機構 電子信箱
	
	@Column(name = "fax")
	private String fax;				// 機構 傳真
	
}
