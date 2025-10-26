package com.github.lianick.converter;

import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import com.github.lianick.model.eneity.Organization;

@Component
public class OrganizationToOrganizationIdConveter extends AbstractConverter<Organization, Long> {

	@Override
	protected Long convert(Organization source) {
		if (source == null) {
			return null;
		}
		
		return source.getOrganizationId();
	}

}
