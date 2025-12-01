package com.github.lianick.service;

import java.util.List;

import com.github.lianick.model.dto.cases.CaseLotteryResultDTO;
import com.github.lianick.model.dto.lotteryQueue.LotteryQueueCreateDTO;
import com.github.lianick.model.dto.lotteryQueue.LotteryQueueDTO;
import com.github.lianick.model.eneity.LotteryQueue;

public interface LotteryQueueService {

	/**
	 * 取出 LotteryQueue = QUEUED 的 柱列們
	 * 需要 @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	 * */
	List<LotteryQueueDTO> findQueuedCases();
	
	/**
	 * 執行 抽籤 
	 * 獲取 該機構 全部的 班級 空位
	 * 獲取 該機構 全部的 LotteryQueue = QUEUED 的 柱列
	 * 進行 抽籤 
	 * 將依序 生產結果 回傳 List<CaseLotteryResultDTO>
	 * 需要 @PreAuthorize("hasAuthority('ROLE_MANAGER')")
	 * */
	List<CaseLotteryResultDTO> executeLottery(Long organizationId);
	
	// 供給 CaseService 使用的 子方法
	/**
	 * 建立新的 抽籤柱列
	 * */
	LotteryQueue createNewLotteryQueue(LotteryQueueCreateDTO lotteryQueueCreateDTO);
	
	
}
