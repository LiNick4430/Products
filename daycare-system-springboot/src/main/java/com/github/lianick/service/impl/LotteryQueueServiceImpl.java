package com.github.lianick.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.lianick.exception.LotteryQueueFailureException;
import com.github.lianick.exception.ValueMissException;
import com.github.lianick.model.dto.lotteryQueue.LotteryQueueCreateDTO;
import com.github.lianick.model.eneity.LotteryQueue;
import com.github.lianick.model.enums.LotteryQueueStatus;
import com.github.lianick.repository.LotteryQueueRepository;
import com.github.lianick.service.LotteryQueueService;

@Service
@Transactional
public class LotteryQueueServiceImpl implements LotteryQueueService{

	@Autowired
	private LotteryQueueRepository lotteryQueueRepository;
	
	@Override
	public LotteryQueue createNewLotteryQueue(LotteryQueueCreateDTO lotteryQueueCreateDTO) {
		// 0. 檢查完整性
		if (lotteryQueueCreateDTO.getCases() == null ||
				lotteryQueueCreateDTO.getChildInfo() == null ||
				lotteryQueueCreateDTO.getOrganization() == null) {
			throw new ValueMissException("缺少必要資訊(案件, 機構, 兒童)");
		}
		
		// 1. 檢查是否存在
		if (lotteryQueueRepository.existsByCaseAndOrg(
				lotteryQueueCreateDTO.getCases().getCaseId(), 
				lotteryQueueCreateDTO.getOrganization().getOrganizationId())) {
			throw new LotteryQueueFailureException("抽籤柱列 已經存在");
		}
		
		// 2. 建立 新的抽籤柱列
		LotteryQueue lotteryQueue = new LotteryQueue();
		lotteryQueue.setCases(lotteryQueueCreateDTO.getCases());
		lotteryQueue.setChildInfo(lotteryQueueCreateDTO.getChildInfo());
		lotteryQueue.setOrganization(lotteryQueueCreateDTO.getOrganization());
		lotteryQueue.setLotteryOrder(-1);	// 只有放入柱列 所以是 -1
		lotteryQueue.setStatus(LotteryQueueStatus.QUEUED);
		
		return lotteryQueue;
	}

}
