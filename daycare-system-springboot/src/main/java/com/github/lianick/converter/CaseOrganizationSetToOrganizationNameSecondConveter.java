package com.github.lianick.converter;

import java.util.Set;

import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import com.github.lianick.model.eneity.CaseOrganization;
import com.github.lianick.model.eneity.Organization;
import com.github.lianick.model.enums.PreferenceOrder;

@Component
public class CaseOrganizationSetToOrganizationNameSecondConveter extends AbstractConverter<Set<CaseOrganization>, String> {

	@Override
	protected String convert(Set<CaseOrganization> source) {
		if (source == null) {
			return null;
		}
		
		return source.stream()
				.filter(organization -> {
					return organization.getPreferenceOrder().equals(PreferenceOrder.SECOND);
				})
				.findFirst()
				.map(CaseOrganization::getOrganization)
				.map(Organization::getName)
				.orElse(null);
	}
}
