package com.github.lianick.service;

import com.github.lianick.model.dto.LotteryExecuteDTO;

public interface LotteryFacadeService {

	/**
	 * 負責整合 
	 * LotteryQueueService.executeLottery 	抽籤行為
	 * CaseService.processLotteryResults	永續化行為
	 * 需要 @PreAuthorize("hasAuthority('ROLE_MANAGER')")
 	 * */
	void executeAndProcessLottery(LotteryExecuteDTO lotteryExecuteDTO);
}
