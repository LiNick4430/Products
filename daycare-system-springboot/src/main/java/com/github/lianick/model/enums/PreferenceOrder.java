package com.github.lianick.model.enums;

import lombok.Getter;

@Getter
public enum PreferenceOrder implements BaseEnum {

	FIRST("第一優先"),
	SECOND("第二優先");
	
	private String description;
	
	PreferenceOrder(String description) {
		this.description = description;
	}
	
	public static PreferenceOrder formCode(String code) {
		return BaseEnum.formCode(PreferenceOrder.class, code);
	}
}
