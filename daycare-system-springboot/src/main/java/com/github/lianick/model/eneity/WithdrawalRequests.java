package com.github.lianick.model.eneity;

import java.time.LocalDateTime;

import org.hibernate.annotations.SQLRestriction;

import com.github.lianick.model.enums.WithdrawalRequestStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "withdrawal_requests")			// 撤銷申請
@SQLRestriction("delete_at IS NULL")
public class WithdrawalRequests extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "withdrawal_request_id")
	private Long withdrawalRequestId;			//  申請ID
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "case_id", nullable = false)
	private Cases cases;						// 申請撤銷 案件
	
	@Column(name = "withdrawal_request_date", nullable = false)
	private LocalDateTime requestDate;			// 申請撤銷 日期
	
	@Column(name = "withdrawal_request_reason")
	private String reason;						// 申請撤銷 原因
	
	@ManyToOne
	@JoinColumn(name = "admin_id")
	private UserAdmin userAdmin;				// 審核 員工
	
	@Column(name = "withdrawal_request_audit_date")
	private LocalDateTime auditDate;			// 審核 日期
	
	@Enumerated(EnumType.STRING)
	@Column(name = "withdrawal_request_audit_status", nullable = false)
	private WithdrawalRequestStatus status;		// 審核 狀態

	@Version
	@Column(name = "version", nullable = false)
	private Long version;	// 樂觀鎖
}