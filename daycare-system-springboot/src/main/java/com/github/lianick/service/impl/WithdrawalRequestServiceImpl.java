package com.github.lianick.service.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.lianick.exception.ValueMissException;
import com.github.lianick.exception.WithdrawalRequestFailureException;
import com.github.lianick.model.eneity.Cases;
import com.github.lianick.model.eneity.WithdrawalRequests;
import com.github.lianick.model.enums.CaseStatus;
import com.github.lianick.model.enums.WithdrawalRequestStatus;
import com.github.lianick.repository.WithdrawalRequestsRepository;
import com.github.lianick.service.WithdrawalRequestService;

@Service
@Transactional
public class WithdrawalRequestServiceImpl implements WithdrawalRequestService{

	@Autowired
	private WithdrawalRequestsRepository withdrawalRequestsRepository;
	
	@Override
	public WithdrawalRequests createNewWithDrawalRequest(Cases cases, String reason) {
		// 0. 完整性
		if (cases == null || reason == null || reason.isBlank()) {
			throw new ValueMissException("缺少特定資料(案件, 撤銷原因)");
		}
		
		// 1. 檢查和 案件 相關的 是否存在
		if (withdrawalRequestsRepository.findByCaseAndStatus(cases.getCaseId(), WithdrawalRequestStatus.APPLIED).isPresent()) {
			throw new WithdrawalRequestFailureException("撤銷申請 正在處理");
		}
		if (withdrawalRequestsRepository.findByCaseAndStatus(cases.getCaseId(), WithdrawalRequestStatus.PASSED).isPresent()) {
			throw new WithdrawalRequestFailureException("撤銷申請 已經通過");
		}
		if (withdrawalRequestsRepository.findByCaseAndStatus(cases.getCaseId(), WithdrawalRequestStatus.IN_REVIEW).isPresent()) {
			throw new WithdrawalRequestFailureException("撤銷申請 正在審核中");
		}
		
		// 2. 建立 新的 撤銷申請
		LocalDateTime now = LocalDateTime.now();
		
		WithdrawalRequests withdrawalRequests = new WithdrawalRequests();
		withdrawalRequests.setCases(cases);
		withdrawalRequests.setReason(reason);
		withdrawalRequests.setRequestDate(now);
		withdrawalRequests.setStatus(WithdrawalRequestStatus.APPLIED);
		
		withdrawalRequests = withdrawalRequestsRepository.save(withdrawalRequests);
		
		return withdrawalRequests;
	}

}
