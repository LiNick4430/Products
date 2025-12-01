package com.github.lianick.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.lianick.model.dto.LotteryExecuteDTO;
import com.github.lianick.model.dto.cases.CaseLotteryResultDTO;
import com.github.lianick.model.eneity.Organization;
import com.github.lianick.service.CaseService;
import com.github.lianick.service.LotteryFacadeService;
import com.github.lianick.service.LotteryQueueService;
import com.github.lianick.util.validate.LotteryFacadeValidateUtil;

@Service
@Transactional
public class LotteryFacadeServiceImpl implements LotteryFacadeService {

	@Autowired
	private CaseService caseService;
	
	@Autowired
	private LotteryQueueService lotteryQueueService;
	
	@Autowired
	private LotteryFacadeValidateUtil lotteryFacadeValidateUtil;
	
	@Override
	@PreAuthorize("hasAuthority('ROLE_MANAGER')")
	public void executeAndProcessLottery(LotteryExecuteDTO lotteryExecuteDTO) {
		// 1. 檢驗完整性 + 權限 並排出 Organization
		Organization organization = lotteryFacadeValidateUtil.validateExecuteAndProcess(lotteryExecuteDTO);
		
		// 2. 執行方法
		// 抽籤行為
		List<CaseLotteryResultDTO> caseLotteryResultDTOs = lotteryQueueService.executeLottery(organization);
		// 常駐行為
		caseService.processLotteryResults(caseLotteryResultDTOs);
	}

}
