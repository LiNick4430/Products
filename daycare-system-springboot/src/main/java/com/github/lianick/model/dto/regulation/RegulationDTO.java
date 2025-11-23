package com.github.lianick.model.dto.regulation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegulationDTO {

	private Long id;			// 規範 ID
	private String type;		// 規範 類型
	private String content;		// 規範 內容
	
	private Long organizationId;	// 規範 所屬機構ID
}
