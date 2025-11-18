package com.github.lianick.model.enums;

import com.github.lianick.exception.EnumNoFoundException;

import lombok.Getter;

/**
 * 定義公托系統案件 在 機構 的 狀態 (CaseOrganization) 的所有可能狀態。
 */
@Getter
public enum CaseOrganizationStatus {

	// --- 進行中 / 已申請 ---

	// 申請中 (Pending Review)
	APPLIED("APPLIED", "申請中"), // 剛提交，等待審核/抽籤

	// --- 最終狀態 (成功/失敗) ---

	// 審核通過 (Passed Review / Allocation)
	PASSED("PASSED", "已通過"),  // 本機構已確認錄取

	// 未通過 (Rejected)
	REJECTED("REJECTED", "未通過"), // 經審核/抽籤後，本機構確定不錄取

	// --- 暫時狀態 / 被動狀態 ---

	// 備選中 (Waitlisted)
	WAITLISTED("WAITLISTED", "備取中"), // 經抽籤後，本機構列為備取名單

	// 其他機構錄取 (Passed by Other Organization)
	// 這是針對「志願序」而言，如果第一志願通過，第二志願會轉為這個狀態。
	ALLOCATED_TO_OTHER("ALLOCATED_TO_OTHER", "其他機構錄取"), 

	// 撤回申請 (Withdrawn by Public User)
	WITHDRAWN("WITHDRAWN", "撤回"); // 民眾主動撤回申請

	private final String code; // 資料庫中儲存的狀態代碼
	private final String description; // 狀態的中文描述

	CaseOrganizationStatus(String code, String description) {
		this.code = code;
		this.description = description;
	}

	/**
	 * 根據代碼查找對應的 Enum 實例。
	 */
	public static CaseOrganizationStatus fromCode(String code) {
		for (CaseOrganizationStatus status : CaseOrganizationStatus.values()) {
			if (status.getCode().equalsIgnoreCase(code)) {
				return status;
			}
		}
		throw new EnumNoFoundException("找不到目標 code");
	}
}
