package com.github.lianick.model.eneity;

import java.time.LocalDateTime;
import java.util.Set;

import org.hibernate.annotations.SQLRestriction;

import com.github.lianick.model.enums.ApplicationMethod;
import com.github.lianick.model.enums.CaseStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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
@Table(name = "cases")						// 案件
@SQLRestriction("delete_at IS NULL")
public class Cases extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "case_id")
	private Long caseId;					// 案件 ID
	
	// 前台顯示用
	@Column(name = "case_number", unique = true, nullable = false)
	private String caseNumber;				// 案件編號
	
	@Column(name = "case_application_date", nullable = false)
	private LocalDateTime applicationDate;	// 申請時間
	
	@Enumerated(EnumType.STRING)
	@Column(name = "case_application_method", nullable = false)
	private ApplicationMethod applicationMethod;		// 申請方式
	
	@OneToMany(mappedBy = "cases", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<CaseOrganization> organizations;	// 機構(第一 和 第二志願)
	
	// 對應前台(申請 退件 通過)
	@Enumerated(EnumType.STRING)	// 確保資料庫中儲存的是 Enum 的名稱字符串 (e.g., "APPLIED")
	@Column(name = "case_status", nullable = false)
	private CaseStatus status;						// 申請當前狀態(使用 Enum)
	
	/*	可以藉由 幼兒ID -> 民眾ID -> 帳戶ID 反查到
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private Users users;						// 帳戶ID
	*/
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "child_id", nullable = false)
	private ChildInfo childInfo;				// 幼兒ID
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "class_id")
	private Classes classes = null;				// 成功之後 對應的 班級ID
	
	// 以下 三個 的生命週期 都和 cases 綁定 因此使用 cascade = CascadeType.ALL
	
	@OneToMany(mappedBy = "cases", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<CasePriority> priorities;		// 案件 所選擇的 優先條件
	
	// @OneToMany(mappedBy = "cases", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@ManyToMany
	@JoinTable(
			name = "case_link_document_public",							// 關連表名稱
			joinColumns = @JoinColumn(name = "case_id"),				// 案件 ID
			inverseJoinColumns = @JoinColumn(name = "public_doc_id")	// 民眾附件 ID
			)
	private Set<DocumentPublic> documents;		// 案件 所使用 附件
	
	
	@OneToMany(mappedBy = "cases", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<ReviewLogs> reviewHistorys;		// 案件 的 審核紀錄
}
