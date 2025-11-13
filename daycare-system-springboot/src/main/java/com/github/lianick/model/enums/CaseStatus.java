package com.github.lianick.model.enums;

import com.github.lianick.exception.EnumNoFoundException;

import lombok.Getter;

/**
 * 定義公托系統案件 (Cases) 的所有可能狀態。
 */
@Getter
public enum CaseStatus {

	// 申請中 (Pending Review)
    APPLIED("APPLIED", "申請中"), 
    
    // 審核通過 (Passed Review / Allocation)
    PASSED("PASSED", "通過"),
    
    // 已退件/拒絕 (Rejected)
    REJECTED("REJECTED", "退件"),
    
    // 撤回申請 (Withdrawn by Public User)
    WITHDRAWN("WITHDRAWN", "撤回"),
    
    // 待分發/待抽籤 (Pending Lottery/Allocation) - 根據實際流程可選
    PENDING_ALLOCATION("PENDING_ALLOCATION", "待分發");
	
	private final String code; // 資料庫中儲存的狀態代碼
    private final String description; // 狀態的中文描述

    CaseStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    /**
     * 根據代碼查找對應的 Enum 實例。
     */
    public static CaseStatus fromCode(String code) {
		for (CaseStatus status : CaseStatus.values()) {
			if (status.getCode().equalsIgnoreCase(code)) {
				return status;
			}
		}
		throw new EnumNoFoundException("找不到目標 code");
	}
}
