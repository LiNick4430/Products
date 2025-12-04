package com.github.lianick.model.dto.cases;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CaseCompleteDTO {

	private Long caseId;					// 案件 ID
	
	private Long classId;					// 班級 ID
}
