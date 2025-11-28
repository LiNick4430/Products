package com.github.lianick.model.enums;

import lombok.Getter;

@Getter
public enum LotteryResultStatus implements BaseEnum{

	SUCCESS("成功"),
	WAITLIST("備選"),
	FAILED("失敗");
	
	private final String description;

	LotteryResultStatus(String description) {
        this.description = description;
    }
}
