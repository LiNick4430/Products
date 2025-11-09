package com.github.lianick.model.dto.clazz;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ClassFindDTO {
	
	private Long id;	// 班級 ID
	private String name;	// 班級名稱
	
	private Long organizationId;	// 機構ID

}
