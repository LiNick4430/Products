package com.github.lianick.model.enums;

import lombok.Getter;

/**
 * 機構規範 的 規範類型
 * */
@Getter
public enum RegulationType {

	// 幼托類 規範
    FEE_SCHEDULE("費用收費表"),
    LUNCH_POLICY("餐點及午睡規定"),
    ATTENDANCE_RULE("出缺勤與接送規定"),
    EMERGENCY_PLAN("緊急應變計畫"); 		// 例如消防、地震等

    private final String description;

    RegulationType(String description) {
        this.description = description;
    }
	
}
