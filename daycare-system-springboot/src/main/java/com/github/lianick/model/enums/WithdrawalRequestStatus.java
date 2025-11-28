package com.github.lianick.model.enums;

import lombok.Getter;

@Getter
public enum WithdrawalRequestStatus implements BaseEnum{

	APPLIED("申請中"),  		// 使用者 申請 撤銷 
	
    WITHDRAWN("申請取消"), 	// 使用者 申請 取消撤銷
	
    IN_REVIEW("申請審核中"),	// 員工 取出 尚未審核的資料
    
    PASSED("申請通過"), 		// 員工 通過 撤銷申請
	
	REJECTED("申請未通過");    // 員工 不通過 撤銷申請
	
	private final String description; // 狀態的中文描述

	WithdrawalRequestStatus(String description) {
        this.description = description;
    }
}
