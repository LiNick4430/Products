package com.github.lianick.model.dto.cases;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CaseQueueDTO {

	private Long id;					// 案件 ID
	
	private Long organizationId;		// 申辦的 機構ID
	
	private String status;				// CaseOrganization status
	
	private String message;				// 員工的訊息
}
