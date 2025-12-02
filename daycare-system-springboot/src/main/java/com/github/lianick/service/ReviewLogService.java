package com.github.lianick.service;

import java.time.LocalDateTime;

import com.github.lianick.model.dto.reviewLog.ReviewLogCreateDTO;
import com.github.lianick.model.eneity.Cases;
import com.github.lianick.model.eneity.ReviewLogs;
import com.github.lianick.model.eneity.UserAdmin;
import com.github.lianick.model.enums.BaseEnum;

public interface ReviewLogService {

	/** 建立 一個 建立 審核紀錄 所需要的 DTO */
	<E extends Enum<E> & BaseEnum> ReviewLogCreateDTO<E> toDTO(Cases cases, UserAdmin userAdmin, 
			E from, E to, Class<E> enumClass, 
			LocalDateTime now, String reviewLogMessage); 
	
	/** 建立 新的 審核紀錄 */
	<E extends Enum<E> & BaseEnum> ReviewLogs createNewReviewLog(ReviewLogCreateDTO<E> reviewLogCreateDTO);
	
	/** 建立 新的 審核紀錄 (並且通知 民眾) */
}
