package com.github.lianick.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.lianick.exception.ValueMissException;
import com.github.lianick.exception.WithdrawalRequestFailureException;
import com.github.lianick.model.eneity.Cases;
import com.github.lianick.model.eneity.WithdrawalRequests;
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
		
		// 1. 定義所有需要阻止的狀態
		List<WithdrawalRequestStatus> blockingStatuses = List.of(
				WithdrawalRequestStatus.APPLIED,
				WithdrawalRequestStatus.PASSED,
				WithdrawalRequestStatus.IN_REVIEW
				);
		
		// 2. 執行單次查詢
	    List<WithdrawalRequests> existingRequests = withdrawalRequestsRepository.findByCaseIdAndStatusIn(
	        cases.getCaseId(), 
	        blockingStatuses
	    );
	    
	    // 3. 檢查是否存在記錄
	    if (!existingRequests.isEmpty()) {
	        // 由於無法精確判斷是哪個狀態，這裡使用通用的錯誤訊息
	        throw new WithdrawalRequestFailureException("撤銷申請正在處理中、審核中，或已通過，請勿重複提交。");
	    }
		
		// 4. 建立 新的 撤銷申請
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
