package com.github.lianick.service;

import com.github.lianick.model.dto.reviewLog.ReviewLogCreateDTO;
import com.github.lianick.model.eneity.ReviewLogs;
import com.github.lianick.model.enums.BaseEnum;

public interface ReviewLogService {

	/** 建立 新的 審核紀錄 */
	<E extends Enum<E> & BaseEnum> ReviewLogs createNewReviewLog(ReviewLogCreateDTO<E> reviewLogCreateDTO);
	
	/** 建立 新的 審核紀錄 (並且通知 民眾) */
}
