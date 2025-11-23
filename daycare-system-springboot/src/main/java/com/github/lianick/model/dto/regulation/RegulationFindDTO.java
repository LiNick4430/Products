package com.github.lianick.model.dto.regulation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegulationFindDTO {

	private Long id;			// 規範 ID
	
	private Long organizationId;	// 規範 所屬機構ID
}
