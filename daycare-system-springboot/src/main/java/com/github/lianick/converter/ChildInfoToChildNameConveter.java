package com.github.lianick.converter;

import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import com.github.lianick.model.eneity.ChildInfo;

@Component
public class ChildInfoToChildNameConveter extends AbstractConverter<ChildInfo, String> {

	@Override
	protected String convert(ChildInfo source) {
		if (source == null) {
			return null;
		}
		
		return source.getName();
	}

}
