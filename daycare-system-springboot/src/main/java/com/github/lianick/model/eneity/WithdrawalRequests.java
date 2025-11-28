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
@Table(name = "withdrawal_requests")			// 撤銷申請
@SQLRestriction("delete_at IS NULL")
public class WithdrawalRequests extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "withdrawal_request_id")
	private Long withdrawalRequestId;			//  申請ID
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "case_id", nullable = false)
	private Cases cases;						// 申請撤銷 案件
	
	@Column(name = "withdrawal_request_date", nullable = false)
	private LocalDateTime requestDate;			// 申請撤銷 日期
	
	@Column(name = "withdrawal_request_reason")
	private String reason;						// 申請撤銷 原因
	
	@ManyToOne
	@JoinColumn(name = "admin_id", nullable = false)
	private UserAdmin userAdmin;				// 審核 員工
	
	@Column(name = "withdrawal_request_audit_date")
	private LocalDateTime auditDate;			// 審核 日期
	
	@Enumerated(EnumType.STRING)
	@Column(name = "withdrawal_request_audit_status", nullable = false)
	private WithdrawalRequestStatus status;						// 審核 狀態
}

/*
	在你的 WithdrawalRequestsService.createWithdrawalRequest(caseId) 方法中，必須執行以下三項檢查：
	檢查項目			邏輯判斷																	行為
	A. 案件主狀態		查詢 Cases 實體。如果 Cases.status 已是 已撤銷 (WITHDRAWN) 狀態。					拋出異常： 案件已處於撤銷狀態，無法再次操作。
	B. 存在已通過請求	查詢 WithdrawalRequests 表：WHERE case_id = [X] AND status = 'APPROVED'。	拋出異常： 案件已有通過的撤銷紀錄，無法重複申請。
	C. 存在待審核請求	查詢 WithdrawalRequests 表：WHERE case_id = [X] AND status = 'APPLYING'。	拋出異常： 案件已有待審核的請求，請勿重複提交。
*/