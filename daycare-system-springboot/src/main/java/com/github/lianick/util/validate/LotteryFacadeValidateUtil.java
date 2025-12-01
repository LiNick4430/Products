package com.github.lianick.util.validate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.lianick.exception.LotteryQueueFailureException;
import com.github.lianick.exception.ValueMissException;
import com.github.lianick.model.dto.LotteryExecuteDTO;
import com.github.lianick.model.eneity.Organization;
import com.github.lianick.model.eneity.Users;
import com.github.lianick.service.impl.EntityFetcher;
import com.github.lianick.util.UserSecurityUtil;

@Service
public class LotteryFacadeValidateUtil {

	@Autowired
	private UserSecurityUtil userSecurityUtil;
	
	@Autowired
	private UserValidationUtil userValidationUtil;
	
	@Autowired
	private EntityFetcher entityFetcher;
	
	public Organization validateExecuteAndProcess(LotteryExecuteDTO lotteryExecuteDTO) {
		// 0. 完整性
		if (lotteryExecuteDTO.getOrganizationId() == null) {
			throw new ValueMissException("缺少必要資訊(案件ID)");
		}
		
		// 1. 管理權限
		Users users = userSecurityUtil.getCurrentUserEntity();
		if (!userValidationUtil.validateUserIsManager(users)) {
			throw new LotteryQueueFailureException("權限不足 無法執行抽獎行為");
		}
		
		// 2. 嘗試取出 Organization
		Organization organization = entityFetcher.getOrganizationById(lotteryExecuteDTO.getOrganizationId());
		
		return organization;
	}
	
}
