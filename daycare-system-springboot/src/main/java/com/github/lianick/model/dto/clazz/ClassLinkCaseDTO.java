package com.github.lianick.model.dto.clazz;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ClassLinkCaseDTO {
	
	private Long id;	// 班級 ID
	
	private Long caseId;			// 案件ID
	
	private Long organizationId;	// 機構ID

}
