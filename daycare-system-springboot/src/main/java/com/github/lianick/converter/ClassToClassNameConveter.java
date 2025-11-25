package com.github.lianick.converter;

import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import com.github.lianick.model.eneity.Classes;

@Component
public class ClassToClassNameConveter extends AbstractConverter<Classes, String> {

	@Override
	protected String convert(Classes source) {
		if (source == null) {
			return null;
		}
		
		return source.getName();
	}

}
