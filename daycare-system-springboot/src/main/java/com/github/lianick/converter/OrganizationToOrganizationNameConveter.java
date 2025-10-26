package com.github.lianick.converter;

import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import com.github.lianick.model.eneity.Organization;

@Component
public class OrganizationToOrganizationNameConveter extends AbstractConverter<Organization, String> {

	@Override
	protected String convert(Organization source) {
		if (source == null) {
			return null;
		}
		
		return source.getName();
	}

}
