package com.github.lianick.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lianick.exception.JsonFailureException;
import com.github.lianick.exception.ValueMissException;

/**
 * 主要用於 Json String -> DTO
 * */
@Component
public class JsonUtil {

	// ObjectMapper 進行手動 JSON 轉換
	@Autowired
	private ObjectMapper objectMapper;
	
	public <T> T jsonStringToDTO(String json, Class<T> targetClass) {
		if (json == null || json.isBlank()) {
			throw new ValueMissException("缺少特定資料");
		}
		try {
            return objectMapper.readValue(json, targetClass);
		} catch (Exception e) {
			throw new JsonFailureException("Json 轉換失敗");
		}
		
	}
}
