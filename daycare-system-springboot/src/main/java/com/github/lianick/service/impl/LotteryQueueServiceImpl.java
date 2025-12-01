package com.github.lianick.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.lianick.exception.LotteryQueueFailureException;
import com.github.lianick.exception.ValueMissException;
import com.github.lianick.model.dto.lotteryQueue.LotteryQueueCreateDTO;
import com.github.lianick.model.dto.lotteryQueue.LotteryQueueDTO;
import com.github.lianick.model.eneity.LotteryQueue;
import com.github.lianick.model.eneity.Organization;
import com.github.lianick.model.eneity.Users;
import com.github.lianick.model.enums.LotteryQueueStatus;
import com.github.lianick.repository.LotteryQueueRepository;
import com.github.lianick.service.LotteryQueueService;
import com.github.lianick.util.UserSecurityUtil;
import com.github.lianick.util.validate.UserValidationUtil;

@Service
@Transactional
public class LotteryQueueServiceImpl implements LotteryQueueService{

	@Autowired
	private LotteryQueueRepository lotteryQueueRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private UserSecurityUtil userSecurityUtil;
	
	@Autowired
	private UserValidationUtil userValidationUtil;
	
	@Override
	@PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	public List<LotteryQueueDTO> findQueuedCases() {
		// 1. 判斷權限
		Users users = userSecurityUtil.getCurrentUserEntity();
		boolean isManager = userValidationUtil.validateUserIsManager(users);
		
		// 2. 根據權限 執行 搜尋方法(status = QUEUED)
		List<LotteryQueue> lotteryQueues = new ArrayList<>();
		LotteryQueueStatus status = LotteryQueueStatus.QUEUED;
		
		if (isManager) {
			// 管理層
			lotteryQueues = lotteryQueueRepository.findByStatus(status);
		} else {
			// 基層員工
			Organization organization = userSecurityUtil.getOrganizationEntity();
			lotteryQueues = lotteryQueueRepository.findByOrganizationAndStatus(organization, status);
		}
		
		// 3. 轉成 DTO 回傳
		return lotteryQueues.stream()
					.map(lotteryQueue -> modelMapper.map(lotteryQueue, LotteryQueueDTO.class))
					.toList();
	}
	
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
