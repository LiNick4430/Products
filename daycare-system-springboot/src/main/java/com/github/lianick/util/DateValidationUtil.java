package com.github.lianick.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.stereotype.Component;

@Component
public class DateValidationUtil {

	// 定義唯一接受的格式
	private static final String DATE_PATTERN = "yyyy-MM-dd";
	
	/**
     * 檢查給定的字串是否能以單一指定的格式轉換為 LocalDate。
     *
     * @param dateString 待驗證的日期字串 (來自前端)。
     * @return 如果轉換成功返回 true，否則返回 false。
     */
	public boolean isValidLocalDate(String dateString) {		
		
		if (dateString == null || dateString.isBlank()) {
			return false;
		}
		
		try {
			// 建立 格式化器
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
			
			// 嘗試解析字串
			LocalDate.parse(dateString, formatter);
			
			// 若解析成功 返回 true
			return true;			
		} catch (DateTimeParseException e) {
			// 如果解析失敗，會拋出 DateTimeParseException，此時返回 false
			return false;
		}
	}
}
