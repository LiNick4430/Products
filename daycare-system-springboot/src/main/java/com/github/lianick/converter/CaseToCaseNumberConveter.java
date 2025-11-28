package com.github.lianick.converter;

import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import com.github.lianick.model.eneity.Cases;

@Component
public class CaseToCaseNumberConveter extends AbstractConverter<Cases, String> {

	@Override
	protected String convert(Cases source) {
		if (source == null) {
			return null;
		}
		
		return source.getCaseNumber();
	}

}
