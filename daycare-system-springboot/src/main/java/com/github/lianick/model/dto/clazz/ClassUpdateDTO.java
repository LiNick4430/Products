package com.github.lianick.model.dto.clazz;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ClassUpdateDTO {
	
	private Long id;	// 班級 ID
	
	private Integer currentCount = 0;	// 班級 目前人數(預設 0)
	
	private Long organizationId;	// 機構ID

}
