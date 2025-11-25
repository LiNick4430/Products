package com.github.lianick.service;

import java.util.List;

import com.github.lianick.model.dto.EnumDTO;
import com.github.lianick.model.enums.BaseEnum;

/**
 * 負責 Enum 的 給前端的 方法
 * */
public interface EnumService {

	/**
	 * 將任何實作 BaseEnum 介面的 Enum 陣列轉換為 EnumDTO 列表
	 * @param enumClass 任何實作 BaseEnum 的 Enum 類別
	 * @return List<EnumDTO>
	 */
	<E extends Enum<E> & BaseEnum> List<EnumDTO> convertToDTOList(Class<E> enumClass);
}
