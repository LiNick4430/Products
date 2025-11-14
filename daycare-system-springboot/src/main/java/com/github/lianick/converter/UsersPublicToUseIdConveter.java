package com.github.lianick.converter;

import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import com.github.lianick.model.eneity.UserPublic;

@Component
public class UsersPublicToUseIdConveter extends AbstractConverter<UserPublic, Long> {

	@Override
	protected Long convert(UserPublic source) {
		if (source == null) {
			return null;
		}
		
		return source.getUsers().getUserId();
	}

}
