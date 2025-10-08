package com.github.lianick.model.eneity;

import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "case_organization",		// case 中 organization 的關聯
		uniqueConstraints = {
			// 1. 案件不能重複申請 同一機構
			@UniqueConstraint(
					name = "UK_case_organization_unique", 			// 確保 案件 不會重複申請同一個 機構
					columnNames = {"case_id", "organization_id"}
					),
			// 2. 案件不能有重複的 志願序
			@UniqueConstraint(
					name = "UK_case_preference_unique",		// 確保 案件 不會出現重複的 志願序
					columnNames = {"case_id", "case_organization_preference_order"}
					)
		})
@SQLRestriction("delete_at IS NULL")
public class CaseOrganization extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "case_organization_id")
	private Long caseOrganizationId;				// 主鍵
	
	@ManyToOne
	@JoinColumn(name = "case_id", nullable = false)
	private Cases cases;							// 案件 ID
	
	@ManyToOne
	@JoinColumn(name = "organization_id", nullable = false)
	private Organization organization;				// 機構 ID
	
	@Column(name = "case_organization_preference_order", nullable = false)
	private Integer preferenceOrder;				// 志願序

	@Column(name = "case_organization_status", nullable = false)
	private String status;							// 案件 在 機構的 狀態
	
}
