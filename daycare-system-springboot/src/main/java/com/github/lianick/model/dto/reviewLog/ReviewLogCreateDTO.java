package com.github.lianick.model.dto.reviewLog;

import java.time.LocalDateTime;

import com.github.lianick.model.eneity.Cases;
import com.github.lianick.model.eneity.UserAdmin;
import com.github.lianick.model.enums.BaseEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewLogCreateDTO<E extends Enum<E> & BaseEnum> {

	private Cases cases;				// 目標 案件
	private UserAdmin userAdmin;		// 建立 員工
	private E from;						// 原本 狀態
	private E to;						// 目標 狀態
	private Class<E> enumClass;			// 狀態 類型
	private LocalDateTime now;			// 現在 時間
	private String reviewLogMessage;	// 員工 訊息
	
}
