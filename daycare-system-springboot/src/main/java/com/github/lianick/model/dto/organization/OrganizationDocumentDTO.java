package com.github.lianick.model.dto.organization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationDocumentDTO {

	private Long id;				// 機構ID
	
	private Long doucmnetId;		// 附件ID
	
}
