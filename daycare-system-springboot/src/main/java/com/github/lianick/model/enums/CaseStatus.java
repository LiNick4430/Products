package com.github.lianick.model.enums;

import com.github.lianick.exception.EnumNotFoundException;

import lombok.Getter;

/**
 * 定義公托系統案件 (Cases) 的所有可能狀態。
 */
@Getter
public enum CaseStatus {

	// 流程起點
    APPLIED("APPLIED", "申請中"), // 1. 申請中
    
    // 成功流程 (核心審核通過)
    PASSED("PASSED", "通過"), // 2. 通過
    
    // 成功流程 (等待分配/抽籤)
    PENDING("PENDING", "待分發"), // 3. 待分發 (或待抽籤)
    
    // 成功流程 (已取得名額)
    ALLOCATED("ALLOCATED", "已分發"), // 4. 已分發
    
    // 成功流程 (報到完成，案件結案)
    COMPLETED("COMPLETED", "已報到/結案"), // 報到結案
    
    // 失敗流程 (人工拒絕)
    REJECTED("REJECTED", "退件"), // 2. 退件
    
    // 撤銷流程 (使用者自願)
    WITHDRAWN("WITHDRAWN", "撤回"); // 2. 撤回
	
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
		throw new EnumNotFoundException("找不到目標 code");
	}
}
