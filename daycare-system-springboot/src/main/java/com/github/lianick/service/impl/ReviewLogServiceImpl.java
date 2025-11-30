package com.github.lianick.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.lianick.exception.EnumNotFoundException;
import com.github.lianick.exception.ReviewLogFailureException;
import com.github.lianick.exception.ValueMissException;
import com.github.lianick.model.dto.reviewLog.ReviewLogCreateDTO;
import com.github.lianick.model.eneity.ReviewLogs;
import com.github.lianick.model.enums.BaseEnum;
import com.github.lianick.repository.ReviewLogsRepository;
import com.github.lianick.service.ReviewLogService;

@Service
@Transactional
public class ReviewLogServiceImpl implements ReviewLogService{

	@Autowired
	private ReviewLogsRepository reviewLogsRepository;
	
	@Override
	public <E extends Enum<E> & BaseEnum> ReviewLogs createNewReviewLog(ReviewLogCreateDTO<E> reviewLogCreateDTO) {
		// 檢查完整性
		if (reviewLogCreateDTO.getCases() == null || reviewLogCreateDTO.getCases().getCaseId() == null ||
				reviewLogCreateDTO.getUserAdmin() == null || reviewLogCreateDTO.getUserAdmin().getAdminId() == null ||
				reviewLogCreateDTO.getFrom() == null || reviewLogCreateDTO.getTo() == null ||
				reviewLogCreateDTO.getNow() == null || reviewLogCreateDTO.getEnumClass() == null) {
			throw new ValueMissException("審核紀錄 缺少必要資料");
		}
		
		// 檢查 from 和 to 是不是 EnumClass 的類型
		if (reviewLogCreateDTO.getFrom().getClass() != reviewLogCreateDTO.getEnumClass() ||
				reviewLogCreateDTO.getTo().getClass() != reviewLogCreateDTO.getEnumClass()) {
			throw new EnumNotFoundException("目標類型找不到");
		}
		
		// 檢查 紀錄 是否 已經存在
		if (reviewLogsRepository.countByCaseIdAndFromAndType(
				reviewLogCreateDTO.getCases().getCaseId(),
				reviewLogCreateDTO.getFrom().getCode(),
				reviewLogCreateDTO.getEnumClass().getSimpleName()) > 0) {
			throw new ReviewLogFailureException("審核紀錄 已經存在");
		}
		
		// 建立新的 審核紀錄
		ReviewLogs reviewLogs = new ReviewLogs();
		reviewLogs.setCases(reviewLogCreateDTO.getCases());
		reviewLogs.setUserAdmin(reviewLogCreateDTO.getUserAdmin());
		reviewLogs.setFromStatus(reviewLogCreateDTO.getFrom().getCode());
		reviewLogs.setToStatus(reviewLogCreateDTO.getTo().getCode());
		reviewLogs.setReviewType(reviewLogCreateDTO.getEnumClass().getSimpleName());
		reviewLogs.setReviewDate(reviewLogCreateDTO.getNow());
		reviewLogs.setReviewMessage(reviewLogCreateDTO.getReviewLogMessage());
		
		return reviewLogs;
	}

}
