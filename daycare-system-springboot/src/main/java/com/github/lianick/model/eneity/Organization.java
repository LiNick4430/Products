package com.github.lianick.model.eneity;

import java.util.Set;

import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@SQLRestriction("delete_at IS NULL")
public class Organization extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "organization_id")
	private Long organizationId;	// 機構 ID
	
	@Column(name = "organization_name", unique = true, nullable = false)
	private String name;			// 機構 名稱
	
	@Column(name = "organization_description")
	private String description;		// 機構 介紹
	
	@Column(name = "organization_address", nullable = false)
	private String address;			// 機構 地址
	
	@Column(name = "organization_phone", unique = true, nullable = false)
	private String phoneNumber;		// 機構 電話
	
	@Column(name = "organization_email", unique = true, nullable = false)
	private String email;			// 機構 電子信箱
	
	@Column(name = "organization_fax")
	private String fax;				// 機構 傳真
	
	@OneToMany(mappedBy = "organization", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<DocumentAdmin> documents;		// 機構相關 的 文件
	
	@OneToMany(mappedBy = "organization", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<Announcements> announcements;	// 機構相關 的 公告
	
	@OneToMany(mappedBy = "organization", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<Regulations> regulations;		// 機構相關 的 規範
}
