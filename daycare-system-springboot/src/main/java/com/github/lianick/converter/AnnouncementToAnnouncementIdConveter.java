package com.github.lianick.converter;

import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import com.github.lianick.model.eneity.Announcements;

@Component
public class AnnouncementToAnnouncementIdConveter extends AbstractConverter<Announcements, Long> {

	@Override
	protected Long convert(Announcements source) {
		if (source == null) {
			return null;
		}
		
		return source.getAnnouncementId();
	}

}
