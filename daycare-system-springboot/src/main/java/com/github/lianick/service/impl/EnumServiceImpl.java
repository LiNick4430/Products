package com.github.lianick.service.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.github.lianick.model.dto.EnumDTO;
import com.github.lianick.model.enums.BaseEnum;
import com.github.lianick.service.EnumService;

@Service
public class EnumServiceImpl implements EnumService{

	@Override
	public <E extends Enum<E> & BaseEnum> List<EnumDTO> convertToDTOList(Class<E> enumClass) {
		return Arrays.stream(enumClass.getEnumConstants())
						.map(enumValue -> new EnumDTO(
							enumValue.getCode(),
							enumValue.getDescription()
						)).toList();
	}
}
