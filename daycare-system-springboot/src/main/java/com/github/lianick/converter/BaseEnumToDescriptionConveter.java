package com.github.lianick.converter;

import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import com.github.lianick.model.enums.BaseEnum;

@Component
public class BaseEnumToDescriptionConveter extends AbstractConverter<BaseEnum, String> {

	@Override
	protected String convert(BaseEnum source) {
		if (source == null) {
			return null;
		}
		
		return source.getDescription();
	}

}
