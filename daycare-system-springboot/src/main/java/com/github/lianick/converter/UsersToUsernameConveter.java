package com.github.lianick.converter;

import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import com.github.lianick.model.eneity.Users;

@Component
public class UsersToUsernameConveter extends AbstractConverter<Users, String> {

	@Override
	protected String convert(Users source) {
		if (source == null) {
			return null;
		}
		
		return source.getAccount();
	}

}
