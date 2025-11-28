package com.github.lianick.converter;

import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import com.github.lianick.model.eneity.UserAdmin;

@Component
public class UserAdminToNameConveter extends AbstractConverter<UserAdmin, String> {

	@Override
	protected String convert(UserAdmin source) {
		if (source == null) {
			return null;
		}
		
		return source.getName();
	}

}
