package com.github.lianick.converter;

import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import com.github.lianick.model.enums.CaseStatus;

@Component
public class CaseStatusToStatusDescriptionConveter extends AbstractConverter<CaseStatus, String> {

	@Override
	protected String convert(CaseStatus source) {
		if (source == null) {
			return null;
		}
		
		return source.getDescription();
	}
}
