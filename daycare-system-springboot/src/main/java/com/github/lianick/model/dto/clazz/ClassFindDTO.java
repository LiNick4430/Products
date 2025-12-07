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
	
	private Long organizationId;		// 機構ID
	private String organizationName;	// 機構名稱

}
