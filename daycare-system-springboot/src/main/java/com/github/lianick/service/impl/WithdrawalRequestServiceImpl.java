package com.github.lianick.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.lianick.model.eneity.Cases;
import com.github.lianick.model.eneity.WithdrawalRequests;
import com.github.lianick.service.WithdrawalRequestService;

@Service
@Transactional
public class WithdrawalRequestServiceImpl implements WithdrawalRequestService{

	@Override
	public WithdrawalRequests createNewWithDrawalRequest(Cases cases, String reason) {
		// TODO Auto-generated method stub
		return null;
	}

}
