package com.github.lianick.converter;

import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import com.github.lianick.model.eneity.ChildInfo;
import com.github.lianick.model.eneity.Classes;

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
