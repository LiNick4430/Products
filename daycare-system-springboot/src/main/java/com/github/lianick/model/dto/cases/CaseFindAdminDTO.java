package com.github.lianick.model.dto.cases;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CaseFindAdminDTO {

	private Long id;					// 案件 ID
	
	private String caseStatus;			// 案件狀態
}
