package com.github.lianick.service;

import com.github.lianick.model.eneity.Cases;
import com.github.lianick.model.eneity.WithdrawalRequests;

public interface WithdrawalRequestService {

	/**
	 * 建立 新的撤銷申請
	 * */
	public WithdrawalRequests createNewWithDrawalRequest(Cases cases, String reason);
}
