package com.github.lianick.model.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WithdrawalRequestDTO {

	private Long id;					// 撤銷申請 ID

	private String caseNumber;			// 案件 編號
	
	private LocalDateTime requestDate;	// 撤銷 日期
	private String reason;				// 撤銷 原因
	
	private String adminName;			// 審核通過 的 員工
	private LocalDateTime auditDate;	// 審核 日期
	
	private String status;				// 目前 審核狀態
	
}
