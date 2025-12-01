package com.github.lianick.model.dto.lotteryQueue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LotteryQueueDTO {
	
	private Long id;				// 案列 ID
	
	private String caseNumber;		// 案件 號碼
	private String childName;		// 小孩 名字
	
	private Long organizationId;	// 機構 ID
	
	private Integer lotteryOrder;	// 抽籤 序位
	private Integer alternateNumber; // 候補 排序
	
	private String status;	// 目前狀態
}
