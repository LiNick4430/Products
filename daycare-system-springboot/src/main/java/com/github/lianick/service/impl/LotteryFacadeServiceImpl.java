package com.github.lianick.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.lianick.model.dto.LotteryExecuteDTO;
import com.github.lianick.model.dto.cases.CaseLotteryResultDTO;
import com.github.lianick.service.CaseService;
import com.github.lianick.service.LotteryFacadeService;
import com.github.lianick.service.LotteryQueueService;

@Service
@Transactional
public class LotteryFacadeServiceImpl implements LotteryFacadeService {

	@Autowired
	private CaseService caseService;
	
	@Autowired
	private LotteryQueueService lotteryQueueService;
	
	@Override
	@PreAuthorize("hasAuthority('ROLE_MANAGER')")
	public void executeAndProcessLottery(LotteryExecuteDTO lotteryExecuteDTO) {
		// 0. 完整性
		
		// 1. 管理權限
		
		// 2. 執行方法
		// 抽籤行為
		List<CaseLotteryResultDTO> caseLotteryResultDTOs = lotteryQueueService.executeLottery(lotteryExecuteDTO.getOrganizationId());
		// 常駐行為
		caseService.processLotteryResults(caseLotteryResultDTOs);
	}

}
