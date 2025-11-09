package com.github.lianick.model.dto.clazz;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ClassCreateDTO {
	
	private Long id;	// 班級 ID
	
	private String name;	// 班級名稱
	private Integer maxCapacity;			// 班級 最大人數
	private Integer currentCount = 0;		// 班級 目前人數(預設 0)
	private Integer ageMinMonths;			// 最小公托年齡
	private Integer ageMaxMonths;			// 最大公托年齡
	private Integer serviceStartMonth;		// 服務起始月份
	private Integer serviceEndMonth;		// 服務結束月份
	
	private Long organizationId;	// 機構ID

}
