package com.github.lianick.model.enums;

import lombok.Getter;

@Getter
public enum LotteryQueueStatus implements BaseEnum{

	QUEUED("候選中"),		// 候選中
	
	WITHDRAWN("已撤銷"),	// 已撤銷
	
	FAILED("未抽到"),		// 未抽到
	
	SELECTED("被抽到"),	// 被抽到
	
	ALLOCATED_ELSEWHERE("被其他分配");	// 被 其他的機構 分配了	
	
	private final String description;

	LotteryQueueStatus(String description) {
        this.description = description;
    }
}
