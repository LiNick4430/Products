package com.github.lianick.model.enums;

import lombok.Getter;

/**
 * 定義公托系統案件 (Cases) 的所有可能狀態。
 */
@Getter
public enum CaseStatus implements BaseEnum{

	// 流程起點
    APPLIED("申請中"), // 1. 申請中
    
    // 成功流程 (核心審核通過)
    PASSED("通過"), // 2. 通過
    
    // 成功流程 (等待分配/抽籤)
    PENDING("待分發"), // 3. 待分發 (或待抽籤)
    
    // 成功流程 (已取得名額)
    ALLOCATED("已分發"), // 4. 已分發
    
    // 成功流程 (報到完成，案件結案)
    COMPLETED("已報到/結案"), // 報到結案
    
    // 失敗流程 (人工拒絕)
    REJECTED("退件"), // 2. 退件
    
    // 撤銷流程 (使用者自願)
    WITHDRAWN("撤回"); // 2. 撤回
	
    private final String description; // 狀態的中文描述

    CaseStatus(String description) {
        this.description = description;
    }
    
    /**
     * 根據代碼查找對應的 Enum 實例。
     */
    public static CaseStatus fromCode(String code) {
		return BaseEnum.formCode(CaseStatus.class, code);
	}
}
