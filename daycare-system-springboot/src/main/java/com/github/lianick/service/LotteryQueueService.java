package com.github.lianick.service;

import com.github.lianick.model.dto.lotteryQueue.LotteryQueueCreateDTO;
import com.github.lianick.model.eneity.LotteryQueue;

public interface LotteryQueueService {

	/**
	 * 建立新的 抽籤柱列
	 * */
	LotteryQueue createNewLotteryQueue(LotteryQueueCreateDTO lotteryQueueCreateDTO); 
}
