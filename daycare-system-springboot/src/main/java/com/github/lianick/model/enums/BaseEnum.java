package com.github.lianick.model.enums;

// 基本功用的 ENUM 在 DTO 轉換可以統一處理
public interface BaseEnum {
	
	// name(資料庫儲存用)
	default String getCode() {
		return ((Enum<?>)this).name();
	}; 
	
	// 中文敘述(前台顯示用)
	String getDescription(); 
}
