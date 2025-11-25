package com.github.lianick.model.enums;

import com.github.lianick.exception.EnumNotFoundException;

// 基本功用的 ENUM 在 DTO 轉換可以統一處理
public interface BaseEnum {
	
	// name(資料庫儲存用)
	default String getCode() {
		return ((Enum<?>)this).name();
	}; 
	
	// 中文敘述(前台顯示用)
	String getDescription(); 
	
	// 用於 前端傳來的 字串 轉換成 對應的 Enum
	// <E extends Enum<E> & BaseEnum> => E 要滿足 是 Enum 並且 實作 BaseEnum
	static <E extends Enum<E> & BaseEnum> E formCode(Class<E> enumClass, String code) {
		// 即早處理 避免浪費
		if (code == null || code.isBlank()) {
			throw new EnumNotFoundException("找不到目標 code");
		}
		
		// 傳入的字串 清除空白 並 轉成全大寫 以供 匹配
		code = code.trim().toUpperCase();
		
		// enumClass.getEnumConstants() => 以反射方式取得 enum 類別的所有 constants，並以陣列回傳。
		for (E e : enumClass.getEnumConstants()) {
			// 當 回傳的 code = enum 類別 中的 constants
			if (e.getCode().equals(code)) {
				return e;
			}
		}
		throw new EnumNotFoundException("找不到目標 code");
	}
}
