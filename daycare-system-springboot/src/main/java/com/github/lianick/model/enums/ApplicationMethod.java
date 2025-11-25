package com.github.lianick.model.enums;

import lombok.Getter;

@Getter
public enum ApplicationMethod implements BaseEnum{
	
	ONLINE("線上申請");	// 目前只有線上申請

	private String description;
	
	ApplicationMethod(String description) {
		this.description = description;
	}
	
	public static ApplicationMethod formCode(String code) {
		return BaseEnum.formCode(ApplicationMethod.class, code);
	}
}
